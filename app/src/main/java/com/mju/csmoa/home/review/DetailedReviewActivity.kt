package com.mju.csmoa.home.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mju.csmoa.databinding.ActivityDetailEventItemBinding

class DetailedReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

    }
}