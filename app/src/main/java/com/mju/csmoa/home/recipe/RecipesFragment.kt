package com.mju.csmoa.home.recipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.mju.csmoa.databinding.FragmentRecipesBinding
import com.mju.csmoa.util.Constants

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var writeRecipeLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.progressBarRecipesOnGoing.visibility = View.INVISIBLE

        // 레시피 작성한 후 리스트에 업데이트
        writeRecipeLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                Log.d(Constants.TAG, "writeReview 끝난 후 / result = $result / result.data = ${result.data}")
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {

                }
            }

        with(binding) {
            cardViewRecipesWriteRecipe.setOnClickListener {
                writeRecipeLauncher.launch(Intent(requireContext(), WriteRecipeActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}