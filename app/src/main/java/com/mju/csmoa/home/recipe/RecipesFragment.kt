package com.mju.csmoa.home.recipe

import android.Manifest
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.databinding.FragmentRecipesBinding
import com.mju.csmoa.home.recipe.adapter.PagingDataRecipeAdapter
import com.mju.csmoa.home.recipe.adapter.SealedRecommendedRecipeAdapter
import com.mju.csmoa.home.recipe.domain.model.DetailedRecipe
import com.mju.csmoa.home.recipe.domain.model.Recipe
import com.mju.csmoa.home.recipe.paging.PagingRecipeViewModel
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import www.sanju.motiontoast.MotionToastStyle

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var writeRecipeLauncher: ActivityResultLauncher<Intent>
    private lateinit var detailedRecipeLauncher: ActivityResultLauncher<Intent>

    private lateinit var recommendedRecipes: List<Recipe>
    private lateinit var sealedRecommendedRecipeAdapter: SealedRecommendedRecipeAdapter

    private lateinit var pagingDataRecipeAdapter: PagingDataRecipeAdapter
    private val pagingRecipeViewModel: PagingRecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        init()
        initLauncher()
        initRecipe()
        return binding.root
    }

    private fun init() {

        // ??? ??? ??????
        binding.cardViewRecipesWriteRecipe.setOnClickListener {
            writeRecipeLauncher.launch(
                Intent(requireContext(), WriteRecipeActivity::class.java)
            )
        }

        // ?????? ??????
        binding.swipeLayoutRecipesRoot.setOnRefreshListener {
            initRecipe()
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                binding.swipeLayoutRecipesRoot.isRefreshing = false
            }
        }

        // ??????
        binding.cardViewRecipesGotoTop.setOnClickListener {
            binding.recyclerViewRecipesRecipes.scrollToPosition(0)
        }
    }

    private fun initLauncher() {
        // ????????? ????????? ??? ???????????? ????????????
        writeRecipeLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                Log.d(TAG, "writeReview ?????? ??? / result = $result / result.data = ${result.data}")
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    pagingDataRecipeAdapter.refresh()
                }
            }

        // ????????? ?????? ?????? ??? ?????? ??? ???????????? (e.g. ????????? ????????? ??????)
        detailedRecipeLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val detailedRecipe =
                        result.data!!.getParcelableExtra<DetailedRecipe>("detailedRecipe")
                    val type = result.data!!.getIntExtra("type", -1)
                    val position = result.data!!.getIntExtra("position", -1)
                    val recipe: Recipe?

                    if (detailedRecipe == null || type == -1 || position == -1) {
                        Log.d(TAG, "????????? ??????")
                        return@registerForActivityResult
                    }

                    if (type == 0) {
                        recipe = recommendedRecipes[position]
                    } else {
                        recipe = pagingDataRecipeAdapter.peek(position - 1)
                    }

                    recipe?.likeNum = detailedRecipe.likeNum
                    recipe?.isLike = detailedRecipe.isLike
                    recipe?.viewNum = detailedRecipe.viewNum

                    if (type == 0) { // ????????? nested adapter?????? ??????????????? ???.
                        sealedRecommendedRecipeAdapter.notifyItemRangeChanged(0, recommendedRecipes.size)
                    } else { // ???????????? ?????????
                        pagingDataRecipeAdapter.notifyItemChanged(position - 1)
                    }
                }
            }

        // ?????? ?????? ?????? ?????? ??????
        val requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: MutableMap<String, Boolean> ->
                permissions.forEach { (_, v) ->
                    if (!v) {
                        MyApplication.makeToast(
                            requireActivity(),
                            "?????? ??????",
                            "????????? ?????? ??????????????????",
                            MotionToastStyle.ERROR
                        )
                        requireActivity().finish()
                    }
                }
            }
        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
    }

    private fun initRecipe() {
        // ????????? ????????????
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val accessToken =
                    MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
                val response =
                    RetrofitManager.retrofitService?.getRecommendedRecipes(accessToken = accessToken!!)
                Log.d(TAG, "response = $response")

                if (response?.result == null && response?.isSuccess == false) {
                    throw IllegalStateException("????????? ????????? ???????????? ??? ??????")
                }

                // ?????? ????????? ????????? ????????????
                recommendedRecipes = response!!.result!!

                withContext(Dispatchers.Main) {
                    binding.progressBarRecipesOnGoing.visibility = View.INVISIBLE

                    val onRecommendedRecipeClicked = { position: Int ->
                        goToDetailRecipe(recommendedRecipes[position].recipeId, position, 0)
                    }

                    val onRecipeClicked = { position: Int ->
                        val recipe = pagingDataRecipeAdapter.peek(position - 1)
                        goToDetailRecipe(recipe!!.recipeId, position, 1)
                    }

                    sealedRecommendedRecipeAdapter =
                        SealedRecommendedRecipeAdapter(
                            recommendedRecipes,
                            onRecommendedRecipeClicked
                        )
                    pagingDataRecipeAdapter = PagingDataRecipeAdapter(onRecipeClicked)
                    val concatAdapter =
                        ConcatAdapter(sealedRecommendedRecipeAdapter, pagingDataRecipeAdapter)

                    binding.recyclerViewRecipesRecipes.apply {
                        adapter = concatAdapter
                        setHasFixedSize(true)
                        addItemDecoration(RecyclerViewDecoration(10, 25, 20, 20))
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }

                pagingRecipeViewModel.getRecipes()
                    .collectLatest { pagingData -> pagingDataRecipeAdapter.submitData(pagingData) }

            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "RecipeFragment - exception / ${ex.stackTrace}")
                    Log.d(TAG, "RecipeFragment - exception / ${ex.message}")
                    ex.printStackTrace()
                    MyApplication.makeToast(
                        requireActivity(),
                        "????????? ??????",
                        "????????? ???????????? ???????????? ??? ??????????????????.",
                        MotionToastStyle.ERROR
                    )
                }
            }
        }
    }

    // ????????? ?????? ?????? ??????????????? ??????
    private fun goToDetailRecipe(recipeId: Long, position: Int, type: Int) {
        val detailedRecipeIntent = Intent(requireContext(), DetailedRecipeActivity::class.java).apply {
            putExtra("recipeId", recipeId)
            putExtra("position", position)
            putExtra("type", type)
        }
        detailedRecipeLauncher.launch(detailedRecipeIntent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}