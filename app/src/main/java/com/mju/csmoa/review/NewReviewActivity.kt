package com.mju.csmoa.review

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mju.csmoa.databinding.ActivityNewReviewBinding

class NewReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}