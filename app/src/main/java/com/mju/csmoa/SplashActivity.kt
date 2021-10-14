package com.mju.csmoa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import java.lang.Runnable
import android.content.Intent
import android.os.Handler
import com.mju.csmoa.SignInActivity
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // go to next activity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }, 300)
    }
}