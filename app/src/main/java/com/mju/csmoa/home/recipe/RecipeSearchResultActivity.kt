package com.mju.csmoa.home.recipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mju.csmoa.databinding.ActivityRecipeSearchResultBinding

class RecipeSearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeSearchResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeSearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

    }
}