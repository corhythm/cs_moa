package com.mju.cs_prototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.mju.cs_prototype.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

    }

    private void init() {
        // 회원 가입
        binding.buttonSignInSignUp.setOnClickListener((View view) -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
//            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
        });

        binding.buttonSignInSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
//            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
        });

        binding.buttonSignInKakaoSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, ReviewDetailsActivity.class));
        });

        binding.buttonSignInGoogleSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, MyReviewActivity.class));
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out);
    }
}
