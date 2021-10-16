package com.mju.csmoa;

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mju.csmoa.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

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
    }

    private fun init() {

        // Set TextWatcher
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
                // check if email pattern mathed
                if (binding.textInputEditTextSignInEmailInput.text.toString().trim().matches(emailPattern)) {
                    binding.textInputLayoutSignInEmailInputLayout.error = null
                    binding.textInputEditTextSignInEmailInput.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(baseContext, R.drawable.ic_all_checked), null);
                } else {


                    binding.textInputLayoutSignInEmailInputLayout.error = "올바른 이메일 양식이 아닙니다"
                    binding.textInputEditTextSignInEmailInput.setCompoundDrawablesWithIntrinsicBounds(
                        null, null, null, null);
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        }

        // email input
        binding.textInputEditTextSignInEmailInput.addTextChangedListener(textWatcher)

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
        // TODO

        // 구글 로그인
        // TODO
    }
}