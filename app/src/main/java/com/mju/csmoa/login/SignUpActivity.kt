package com.mju.csmoa.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.mju.csmoa.R
import com.mju.csmoa.common.EitherAOrBDialog
import com.mju.csmoa.databinding.ActivitySignUpBinding
import com.mju.csmoa.login.domain.PostSignUpReq
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var isEmailValidate = false
    private var isPasswordValidate = false
    private var isNicknameValidate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 입력 데이터 validation 처리
        initValidation()

        // '이미 계정이 있으신가요? 로그인'을 눌렀을 때
        binding.textViewSignUpSignIn.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }


        // click signUp button
        binding.buttonSignUpSignUp.setOnClickListener {
            if (!validateBeforeSignUp()) {
                return@setOnClickListener
            }

            RetrofitManager.instance.signUp(
                PostSignUpReq(
                email = binding.textInputEditTextSignUpEmailInput.text.toString(),
                password = binding.textInputEditTextSignUpPasswordInput.text.toString(),
                nickname = binding.textInputEditTextSignUpNicknameInput.text.toString()
            ),
                completion = { statusCode ->
                    Log.d(TAG, "SignUpActivity -init() called / statusCode = $statusCode")
                    when (statusCode) {
                        100 -> {
                            makeToast("회원가입", "회원가입을 성공적으로 완료했습니다.", MotionToastStyle.SUCCESS)
                            finish()
                        }
                        200 -> {
                            makeToast("회원가입", "입력값을 확인해주세요.", MotionToastStyle.ERROR)
                        }
                        210 -> {
                            makeToast("회원가입", "이미 존재하는 닉네임입니다.", MotionToastStyle.ERROR)
                            binding.textInputEditTextSignUpNicknameInput.requestFocus()
                            binding.textInputEditTextSignUpNicknameInput.setText("")
                        }
                        211 -> {
                            makeToast("회원가입", "이미 가입된 계정입니다.", MotionToastStyle.ERROR)
                            binding.textInputEditTextSignUpEmailInput.requestFocus()
                            binding.textInputEditTextSignUpEmailInput.setText("")
                        }
                        else -> makeToast("회원가입", "알 수 없는 오류로 가입이 불가합니다. 다시 시도해보세요", MotionToastStyle.ERROR)
                    }
                }
            )
        }
    }

    private fun makeToast(title: String, message: String, motionToastStyle: MotionToastStyle) {
        MotionToast.createColorToast(
            this@SignUpActivity,
            title,
            message,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(this@SignUpActivity, R.font.helvetica_regular)
        )
    }

    // 회원 가입 버튼 누르기 전에
    private fun validateBeforeSignUp(): Boolean {
        if (binding.textInputEditTextSignUpEmailInput.text.toString().isEmpty() ||
            binding.textInputEditTextSignUpNicknameInput.text.toString().isEmpty() ||
            binding.textInputEditTextSignUpPasswordInput.text.toString().isEmpty() ||
            binding.textInputEditTextSignUpConfirmPasswordInput.toString().isEmpty()
        ) {
            makeToast("회원가입", "입력양식을 모두 채워주세요.", MotionToastStyle.WARNING)
            return false
        }

        if (!isEmailValidate || !isNicknameValidate || !isPasswordValidate) {
            makeToast("회원가입", "입력양식을 모두 준수해주세요.", MotionToastStyle.WARNING)
            return false
        }

        return true
    }

    // EditText 입력 양식 validation
    private fun initValidation() {

        // TODO: 시간되면 닉네임 중복 처리 (Debounce)
        // 닉네임 양식 validation
        binding.textInputEditTextSignUpNicknameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 닉네임 regex -한글, 영어, 숫자만, 띄어쓰기 안 됨
                val nicknamePattern = Regex("^[가-힣ㄱ-ㅎa-zA-Z0-9]{1,20}+\$")
                val nicknameInputLayout = binding.textInputLayoutSignUpNicknameInputLayout
                val nicknameInput = binding.textInputEditTextSignUpNicknameInput

                // check if email pattern matched
                if (nicknameInput.text.toString().matches(nicknamePattern)) {
                    nicknameInputLayout.error = null
                    nicknameInput.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.ic_all_checked
                        ), null
                    )
                    isNicknameValidate = true
                } else {
                    nicknameInputLayout.error = "닉네임은 띄어쓰기 없이 한글, 영문, 숫자만 가능해요."
                    nicknameInput.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null
                    )
                    isNicknameValidate = false
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // TODO: 시간되면 이메일 중복 처리 (Debounce)
        // 이메일 양식 validation
        binding.textInputEditTextSignUpEmailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
                val emailInputLayout = binding.textInputLayoutSignUpEmailInputLayout
                val emailInputEditText = binding.textInputEditTextSignUpEmailInput

                // check if email pattern matched
                if (emailInputEditText.text.toString().matches(emailPattern)) {
                    emailInputLayout.error = null
                    emailInputEditText.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(
                            baseContext,
                            R.drawable.ic_all_checked
                        ), null
                    )
                    isEmailValidate = true
                } else {
                    emailInputLayout.error = "올바른 이메일 양식이 아닙니다"
                    emailInputEditText.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null
                    )
                    isEmailValidate = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 비밀번호
        binding.textInputEditTextSignUpPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPassword()
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        // 비밀번호 확인
        binding.textInputEditTextSignUpConfirmPasswordInput.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkPassword()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    // 비밀번호와 비밀번호 확인 텍스트 일치하는지 확인
    private fun checkPassword() {
        // 비밀번호랑 비밀번호 확인이랑 일치할 때
        val password =
            Objects.requireNonNull(binding.textInputEditTextSignUpPasswordInput.text)
                .toString()
        val confirmPassword =
            Objects.requireNonNull(binding.textInputEditTextSignUpConfirmPasswordInput.text)
                .toString()

        if (password == confirmPassword && confirmPassword.isNotEmpty()) {
            binding.textInputEditTextSignUpConfirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(this@SignUpActivity, R.drawable.ic_all_checked),
                null
            )
            binding.textInputLayoutSignUpConfirmPasswordInputLayout.error = null
            isPasswordValidate = true
        } else {
            binding.textInputEditTextSignUpConfirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
            binding.textInputLayoutSignUpConfirmPasswordInputLayout.error =
                "비밀번호가 서로 일치하지 않습니다"
            isPasswordValidate = false
        }
    }

    override fun onBackPressed() {

        EitherAOrBDialog(
            context = this,
            theme = R.style.BottomSheetDialogTheme,
            lottieName = "man_question.json",
            title = "Exit?",
            message = "회원가입을 중단하실 건가요?\n중간에 나가시면 기존 정보는 저장되지 않아요 :(",
            buttonAText = "No",
            buttonBText = "Yes",
            onButtonAClicked = { },
            ouButtonBClicked = { super@SignUpActivity.onBackPressed() }
        ).show()

    }
}