package com.mju.csmoa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.mju.csmoa.databinding.ActivitySplashBinding
import com.mju.csmoa.home.HomeActivity
import com.mju.csmoa.login.SignInActivity
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // accessToken Expiration 확인
        binding.root.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            lifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.IO) {

                val jwtTokenInfo = MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()
                Log.d(TAG, "SplashActivity -onCreate() called / jwtTokenInfo = $jwtTokenInfo")

                val isAccessTokenExpired = if (jwtTokenInfo == null) true
                else MyApplication.instance.jwtService.isAccessTokenExpired(jwtTokenInfo.accessToken)

                // JWT 토큰 만료되면
                withContext(Dispatchers.Main) {
                    delay(300L)
                    if (isAccessTokenExpired)
                        startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
                    else
                        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))

                    finish()
                }
            }
        }
    }


}
