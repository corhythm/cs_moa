package com.mju.csmoa

import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.text.TextWatcher
import android.text.Editable
import androidx.core.content.ContextCompat
import com.mju.csmoa.R
import android.content.Intent
import com.mju.csmoa.SignUpActivity
import com.mju.csmoa.databinding.ActivitySignInBinding
import java.util.*

class SignInActivity : AppCompatActivity() {
    private var binding: ActivitySignInBinding? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()

//        window.statusBarColor = Color.TRANSPARENT // statusBar transparent
//        window.navigationBarColor = Color.TRANSPARENT // navigationBar transparent
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }

    private fun init() {

        // email input
        binding!!.textInputEditTextSignInEmailInput.addTextChangedListener(object : TextWatcher {
            // 입력하기 전에
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            // 입력란에 변화가 있을 시
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
                // email pattern matched
                if (Objects.requireNonNull(binding!!.textInputEditTextSignInEmailInput.text).toString().trim { it <= ' ' }.matches(emailPattern)) {
//                    binding.textInputEditTextSignInEmailInput.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                    binding!!.textInputLayoutSignInEmailInputLayout.error = null
                    binding!!.textInputEditTextSignInEmailInput.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(this@SignInActivity, R.drawable.ic_all_checked), null)
                } else { // email pattern not matched
                    binding!!.textInputLayoutSignInEmailInputLayout.error = "올바른 이메일 양식이 아닙니다"
                    binding!!.textInputEditTextSignInEmailInput.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null)
                }
            }

            // 입력이 끝났을 때
            override fun afterTextChanged(s: Editable) {}
        })

        // signUp
        binding!!.buttonSignInSignUp.setOnClickListener { startActivity(Intent(this@SignInActivity, SignUpActivity::class.java)) }

        // 로그인 성공 -> 홈
        // TODO

        // 카카오 로그인
        // TODO

        // 구글 로그인
        // TODO
    }
}