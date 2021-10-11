package com.mju.csmoa;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mju.csmoa.databinding.ActivitySignInBinding;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

//        getWindow().setStatusBarColor(Color.TRANSPARENT); // statusBar transparent
//        getWindow().setNavigationBarColor(Color.TRANSPARENT); // navigationBar transparent
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void init() {

        // email input
        binding.textInputEditTextSignInEmailInput.addTextChangedListener(new TextWatcher() {
            @Override // 입력하기 전에
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override // 입력란에 변화가 있을 시
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                // email pattern matched
                if (Objects.requireNonNull(binding.textInputEditTextSignInEmailInput.getText()).toString().trim().matches(emailPattern)) {
//                    binding.textInputEditTextSignInEmailInput.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
                    binding.textInputLayoutSignInEmailInputLayout.setError(null);
                    binding.textInputEditTextSignInEmailInput.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(SignInActivity.this, R.drawable.ic_all_checked), null);
                } else { // email pattern not matched
                    binding.textInputLayoutSignInEmailInputLayout.setError("올바른 이메일 양식이 아닙니다");
                    binding.textInputEditTextSignInEmailInput.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null);
                }
            }

            @Override // 입력이 끝났을 때
            public void afterTextChanged(Editable s) { }
        });

         // signUp
        binding.buttonSignInSignUp.setOnClickListener((View view) -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        // 로그인 성공 -> 홈
        // TODO

        // 카카오 로그인
        // TODO

        // 구글 로그인
        // TODO
    }
}
