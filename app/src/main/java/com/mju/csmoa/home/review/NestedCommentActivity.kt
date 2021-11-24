package com.mju.csmoa.home.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mju.csmoa.R
import com.mju.csmoa.databinding.ActivityNestedCommentBinding

class NestedCommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNestedCommentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestedCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        with(binding) {
            setSupportActionBar(toolbarNestedCommentToolbar)
            toolbarNestedCommentToolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }
}