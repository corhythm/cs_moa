package com.mju.cs_prototype;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mju.cs_prototype.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
