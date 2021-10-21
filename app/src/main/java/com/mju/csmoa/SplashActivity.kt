package com.mju.csmoa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.mju.csmoa.databinding.ActivitySplashBinding
import com.mju.csmoa.login.SignInActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // go to next activity
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
//            finish()
//        }, 300)

//        lifecycle.coroutineScope.launch {
//            delay(300L)
//            startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
//            finish()
//        }

        binding.root.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {
                delay(300L)
                startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
                finish()
            }
        }


    }
}