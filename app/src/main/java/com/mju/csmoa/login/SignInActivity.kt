package com.mju.csmoa.login;

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivitySignInBinding
import com.mju.csmoa.main.HomeActivity
import com.mju.csmoa.util.Constants.TAG


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var getResultText: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getResultText = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }


    }

    private fun init() {

        // Set TextWatcher
        val emailTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
                val emailInputLayout = binding.textInputLayoutSignInEmailInputLayout
                val emailInputEditText = binding.textInputEditTextSignInEmailInput

                // check if email pattern matched
                if (emailInputEditText.text.toString().matches(emailPattern)) {
                    emailInputLayout.error = null
                    emailInputEditText.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.ic_all_checked
                        ), null
                    );
                } else {
                    emailInputLayout.error = "올바른 이메일 양식이 아닙니다"
                    emailInputEditText.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null
                    );
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        }

        // email input
        binding.textInputEditTextSignInEmailInput.addTextChangedListener(emailTextWatcher)

        // signUp
        binding.buttonSignInSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 로그인 성공 -> 홈
        binding.buttonSignInSignIn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // 카카오 로그인
        binding.buttonSignInKakaoSignIn.setOnClickListener {

            // 로그인 공통 callback 구성
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패 / error: $error / token: $token")
                } else if (token != null) {
                    Log.i(TAG, "로그인 성공 / token: $token / token.accessToken: ${token.accessToken}")
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }

            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    Log.i(
                        TAG, "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                    )
                }
            }

            // 로그아웃
//            UserApiClient.instance.logout { error ->
//                if (error != null) {
//                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
//                }
//                else {
//                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
//                }
//            }

        }

        // 구글 로그인
        binding.buttonSignInGoogleSignIn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("61701334575")
                .requestEmail()
                .requestProfile()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acct: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            val personName = acct.displayName

            val idToken = acct.idToken
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri = acct.photoUrl!!
            Log.d(TAG, "handleSignInResult:idToken $idToken")
            Log.d(TAG, "handleSignInResult:personName $personName")
            Log.d(TAG, "handleSignInResult:personGivenName $personGivenName")
            Log.d(TAG, "handleSignInResult:personEmail $personEmail")
            Log.d(TAG, "handleSignInResult:personId $personId")
            Log.d(TAG, "handleSignInResult:personFamilyName $personFamilyName")
            Log.d(TAG, "handleSignInResult:personPhoto $personPhoto")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }
}