package com.mju.csmoa;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mju.csmoa.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {

        // '이미 계정이 있으신가요? 로그인'을 눌렀을 때
        binding.textViewSignUpSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });

        // 닉네임 중복 처리
        // TODO

        // 아이디(이메일) 중복 처리
        // TODO

        // 비밀번호 확인
        binding.textInputEditTextSignUpConfirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 비밀번호랑 비밀번호 확인이랑 일치할 때
                if (Objects.requireNonNull(binding.textInputEditTextSignUpPasswordInput.getText()).toString().trim()
                        .equals(Objects.requireNonNull(binding.textInputEditTextSignUpConfirmPasswordInput.getText()).toString())) {
                    binding.textInputEditTextSignUpConfirmPasswordInput.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(SignUpActivity.this, R.drawable.ic_all_checked), null);
                    binding.textInputLayoutSignUpConfirmPasswordInputLayout.setError(null);
                } else {
                    binding.textInputLayoutSignUpConfirmPasswordInputLayout.setError("비밀번호가 서로 일치하지 않습니다");
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // click signUp button
        binding.buttonSignUpSignUp.setOnClickListener(v -> {

        });


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out);
    }
}