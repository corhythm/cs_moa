package com.mju.csmoa.home.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mju.csmoa.databinding.ActivityRecipeSearchResultBinding
import com.mju.csmoa.home.NoSearchResultFragment
import com.mju.csmoa.home.recipe.adapter.PagingDataRecipeSearchResultAdapter
import com.mju.csmoa.home.recipe.paging.PagingRecipeViewModel
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.mju.csmoa.common.util.RecyclerViewDecoration
import com.mju.csmoa.common.util.room.database.LocalRoomDatabase
import com.mju.csmoa.common.util.room.entity.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.*

class RecipeSearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeSearchResultBinding
    private var searchWord: String? = null

    private lateinit var pagingDataRecipeSearchResultAdapter: PagingDataRecipeSearchResultAdapter
    private val pagingRecipeViewModel by viewModels<PagingRecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeSearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 아무런 검색 결과 없으면
        val whenNoReviewSearchResult: () -> Unit = {
            binding.lottieAnimationViewRecipeSearchResultLottie.visibility = View.INVISIBLE
            binding.textViewRecipeSearchResultSearchWord.visibility = View.INVISIBLE
            binding.progressBarRecipeSearchResultLoading.visibility = View.INVISIBLE
            binding.recyclerViewRecipeSearchResultSearchResults.visibility = View.INVISIBLE

            supportFragmentManager.beginTransaction()
                .replace(
                    binding.frameLayoutRecipeSearchResultContainer.id,
                    NoSearchResultFragment(searchWord)
                )
                .commit()
        }
        if (!intent.hasExtra("searchWord")) { // 검색어가 넘어오지 않았을 때 (근데 루프로는 진입할 일이 없기는 함)
            Log.d(TAG, "검색어 넘어오지 않음")
            whenNoReviewSearchResult()
            return
        }

        searchWord = intent.getStringExtra("searchWord")
        binding.textViewRecipeSearchResultSearchWord.text = "'${searchWord}'에 대한 검색 결과입니다."
        saveSearchHistory(searchWord!!)

        try { // 데이터 가져오기
            val onRecipeClicked: (position: Int) -> Unit = { position: Int ->
                // 이건 concat이랑 같이 쓸 때랑 달라서 position - 1하면 0번 선택하면 NPE 발생
                val recipe = pagingDataRecipeSearchResultAdapter.peek(position)
                goToDetailedRecipe(
                    recipeId = recipe!!.recipeId,
                    position = position,
                    type = 1
                )
            }

            val whenSearchingComplete = {
                binding.progressBarRecipeSearchResultLoading.visibility = View.INVISIBLE
            }

            pagingDataRecipeSearchResultAdapter =
                PagingDataRecipeSearchResultAdapter(onRecipeClicked)

            binding.recyclerViewRecipeSearchResultSearchResults.apply {
                adapter = pagingDataRecipeSearchResultAdapter
                addItemDecoration(RecyclerViewDecoration(30, 30, 20, 20))
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(
                    this@RecipeSearchResultActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }

            // init viewModel
            lifecycleScope.launch(Dispatchers.IO) {
                pagingRecipeViewModel.setSearchForInfo(searchWord!!, whenSearchingComplete, whenNoReviewSearchResult)
                pagingRecipeViewModel.getRecipes().collectLatest { pagingData ->
                    pagingDataRecipeSearchResultAdapter.submitData(pagingData)
                }
            }

        } catch (ex: Exception) {
            Log.d(TAG, "ReviewsFragment - exception / ${ex.stackTrace}")
            Log.d(TAG, "ReviewsFragment - exception / ${ex.message}")

            MyApplication.makeToast(
                this@RecipeSearchResultActivity,
                "리뷰 검색 결과",
                "리뷰 검색 결과를 가져오는 데 실패했습니다.",
                MotionToastStyle.ERROR
            )
        }
    }

    // save search history
    private fun saveSearchHistory(searchWord: String) {
        if (searchWord.trim().isEmpty()) {
            return
        }
        val currentDate = SimpleDateFormat("yy.MM.dd HH:mm:ss", Locale.getDefault()).format(Date())
        val database = LocalRoomDatabase.getDatabase(this)

        lifecycleScope.launch {
            database.searchHistoryDao().insertSearchHistory(
                SearchHistory(searchWord = searchWord, createdAt = currentDate, type = 1)
            )
        }
    }

    private fun goToDetailedRecipe(
        recipeId: Long,
        position: Int,
        type: Int
    ) {
        val detailedRecipeIntent =
            Intent(this, DetailedRecipeActivity::class.java).apply {
                putExtra("recipeId", recipeId)
                putExtra("position", position)
                putExtra("type", type)
            }
        startActivity(detailedRecipeIntent)
    }
}