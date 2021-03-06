package com.mju.csmoa.login;

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.mju.csmoa.R
import com.mju.csmoa.common.SelectMenuDialog
import com.mju.csmoa.databinding.ActivitySignInBinding
import com.mju.csmoa.home.HomeActivity
import com.mju.csmoa.login.domain.JwtToken
import com.mju.csmoa.login.domain.PostLoginReq
import com.mju.csmoa.login.domain.PostLoginRes
import com.mju.csmoa.login.domain.PostOAuthLoginReq
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var isValid = false
    private var serverDialogCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        // launcher ??????
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    Log.d(TAG, "?????? ????????? ??????")
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            }
    }

    private fun init() {

        // set server address (???????????? ?????? 5??? ????????? server address ?????? ??????????????? ??????)
        binding.imageViewSignInAppImage.setOnClickListener {
            serverDialogCounter++
            if (serverDialogCounter == 5) {
                SetServerAddressDialog(this, R.style.BottomSheetDialogTheme).show()
                serverDialogCounter = 0
            }
        }


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
                    isValid = true
                    emailInputEditText.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.ic_all_checked
                        ), null
                    )
                } else {
                    isValid = false
                    emailInputLayout.error = "????????? ????????? ????????? ????????????"
                    emailInputEditText.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // email input
        binding.textInputEditTextSignInEmailInput.addTextChangedListener(emailTextWatcher)

        // signUp
        binding.buttonSignInSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // ?????? ????????? ?????? ?????????
        binding.buttonSignInSignIn.setOnClickListener {

            val email = binding.textInputEditTextSignInEmailInput.text.toString()
            val password = binding.textInputEditTextSignInPasswordInput.text.toString()

            if (email.isEmpty() || password.isEmpty() || !isValid) {
                makeToast("?????????", "????????? ????????? ??????????????????", MotionToastStyle.ERROR)
                return@setOnClickListener
            }

            val postLoginReq = PostLoginReq(email = email, password = password)

            Log.d(TAG, "SignInActivity -init() called / postLoginReq = $postLoginReq")
            RetrofitManager.instance.login(
                postLoginReq,
                completion = { statusCode: Int, postLoginRes: PostLoginRes? ->
                    loginCallback(statusCode, postLoginRes!!)
                }
            )
        }

        // ????????? ?????????
        binding.buttonSignInKakaoSignIn.setOnClickListener {

            // ????????? ?????? callback ?????? (?????? ?????? ???????????? ??? ??????, loginWithKakaoAccount??? Talk ?????? ??? ?????? ???)
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, tokenError ->
                if (tokenError != null) {
                    Log.e(TAG, "????????? ?????? / error: $tokenError / token: $token")
                } else if (token != null) {
                    Log.i(TAG, "????????? ?????? / token: $token")

                    UserApiClient.instance.me { user, userError ->
                        if (userError != null) {
                            Log.e(TAG, "????????? ?????? ?????? ??????", userError)
                            makeToast(
                                "????????? ??????",
                                "?????????????????? ?????? ????????? ???????????? ??? ??????????????????.",
                                MotionToastStyle.ERROR
                            )

                        } else if (user != null) {

                            // ???????????? ????????? ?????? ????????? ??? ??????
                            if (user.kakaoAccount?.email == null) {
                                makeToast(
                                    "????????? ??????",
                                    "????????? ????????? ??????????????????",
                                    MotionToastStyle.ERROR
                                )
                            }

                            Log.i(
                                TAG, "????????? ?????? ?????? ??????" +
                                        "\n????????????: ${user.id}" +
                                        "\n?????????: ${user.kakaoAccount?.email}" +
                                        "\n?????????: ${user.kakaoAccount?.profile?.nickname}" +
                                        "\n???????????????: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                            )

                            val postOAuthLoginReq = PostOAuthLoginReq(
                                email = user.kakaoAccount?.email,
                                nickname = user.kakaoAccount?.profile?.nickname,
                                profileImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl,
                                provider = "kakao"
                            )

                            // OAuth login
                            RetrofitManager.instance.oAuthLogin(
                                postOAuthLoginReq,
                                completion = { statusCode: Int, postLoginRes: PostLoginRes? ->
                                    loginCallback(statusCode, postLoginRes)
                                }
                            )
                        }
                    }
                }
            }

            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }

        // ?????? ?????????
        binding.buttonSignInGoogleSignIn.setOnClickListener {
            Log.d(TAG, "????????? ????????? ?????? ??????")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // ?????? ????????? ???????????? ??????
            launcher.launch(mGoogleSignInClient.signInIntent)
        }
    }

    private fun makeToast(title: String, message: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            this@SignInActivity,
            title,
            message,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(this@SignInActivity, R.font.helvetica_regular)
        )
    }

    // ????????? ????????????
    private fun loginSuccess(postLoginRes: PostLoginRes) {

        // DataStore??? accessToken?????? refreshToken ??????
        lifecycleScope.launch(Dispatchers.IO) {
            MyApplication.instance.jwtTokenInfoProtoManager.updateJwtTokenInfo(
                JwtToken(
                    userId = postLoginRes.userId,
                    accessToken = postLoginRes.accessToken,
                    refreshToken = postLoginRes.refreshToken
                )
            )
            val userInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()
            Log.d(TAG, "?????? userInfo = $userInfo")
        }

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    // OAuthLogin ???????????? ???
    private fun loginCallback(statusCode: Int, postLoginRes: PostLoginRes?) {

        Log.d(TAG, "statusCode = $statusCode")
        when (statusCode) {
            100 -> {
                loginSuccess(postLoginRes!!)
                Log.d(TAG, "SignInActivity -loginCallback() called / postLoginRes = $postLoginRes")
            }
            211 -> {
                makeToast("?????????", "?????? ????????? ??????????????????", MotionToastStyle.ERROR)
            }
            else -> {
                makeToast("?????????", "??? ??? ?????? ????????? ???????????? ??????????????????", MotionToastStyle.ERROR)
            }
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

            val postOAuthLogin = PostOAuthLoginReq(
                email = personEmail,
                nickname = personGivenName,
                profileImageUrl = personPhoto.toString(),
                provider = "google"
            )

            // OAuth login
            RetrofitManager.instance.oAuthLogin(
                postOAuthLogin,
                completion = { statusCode: Int, postLoginRes: PostLoginRes? ->
                    loginCallback(statusCode, postLoginRes!!)
                }
            )

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }
}