package com.mju.csmoa.home.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.mju.csmoa.R
import com.mju.csmoa.common.EitherAOrBDialog
import com.mju.csmoa.databinding.ActivityDetailedRecipeBinding
import com.mju.csmoa.home.cs_location.CSMapActivity
import com.mju.csmoa.home.recipe.adapter.DetailedRecipeIngredientAdapter
import com.mju.csmoa.home.recipe.domain.model.DetailedRecipe
import com.mju.csmoa.home.review.adapter.DetailedReviewOrRecipeImageAdapter
import com.mju.csmoa.retrofit.RetrofitManager
import com.mju.csmoa.common.util.Constants
import com.mju.csmoa.common.util.Constants.TAG
import com.mju.csmoa.common.util.MyApplication
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToastStyle

class DetailedRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedRecipeBinding
    private var recipeId: Long? = null
    private var position: Int? = null
    private var type: Int? = null
    private var detailedRecipe: DetailedRecipe? = null
    private lateinit var detailedRecipeIngredientAdapter: DetailedRecipeIngredientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initDetailedRecipe()
    }

    private fun init() {
        // intent로부터 데이터 가져오기
        if (!intent.hasExtra("recipeId") && !intent.hasExtra("position") && !intent.hasExtra("type")) {
            Log.d(Constants.TAG, "필수 값 다 안 넘어 왔음")
            MyApplication.makeToast(
                this,
                "레시피 상세 정보",
                "레시피 데이터를 가져오는데 실패했습니다.",
                MotionToastStyle.ERROR
            )
            return
        }

        recipeId = intent.getLongExtra("recipeId", -1)
        position = intent.getIntExtra("position", -1)
        type = intent.getIntExtra("type", -1)

        if (recipeId == (-1).toLong() && position == -1 && type == -1) {
            MyApplication.makeToast(
                this,
                "리뷰 상세 정보",
                "리뷰 데이터를 가져오는데 실패했습니다.",
                MotionToastStyle.ERROR
            )
            return
        }
    }

    // NOTE: 전체 리사이클러뷰 설정
    private fun initDetailedRecipe() {
        lifecycleScope.launch(Dispatchers.IO) {
            val accessToken =
                MyApplication.instance.jwtTokenInfoProtoManager.getJwtTokenInfo()?.accessToken
            val response = RetrofitManager.retrofitService?.getDetailedRecipe(
                accessToken ?: "",
                recipeId!!
            )
            if (response?.result == null) {
                launch(Dispatchers.Main) {
                    MyApplication.makeToast(
                        this@DetailedRecipeActivity,
                        "레시피 상세 정보",
                        "레시피 데이터를 가져오는데 실패했습니다.",
                        MotionToastStyle.ERROR
                    )
                }
                return@launch
            }

            // NOTE: 상세 레시피 정보 가져오기
            detailedRecipe = response.result!!

            // NOTE: 좋아요 클릭하면 / 좋아요 <-> 싫어요
            val onLikeClickListener = View.OnClickListener {
                Log.d(TAG, "SetOnClickListener")
                lifecycleScope.launch(Dispatchers.IO) {
                    // 왜 단순히 launch로만 하면 실행 안 될까...
                    val postLikeResponse = RetrofitManager.retrofitService.postRecipeLike(
                        accessToken = accessToken!!,
                        recipeId = detailedRecipe!!.recipeId
                    )
                    Log.d(TAG, "postLikeResponse = $postLikeResponse")

                    if (postLikeResponse.isSuccess && postLikeResponse.result != null) { // 성공하면
                        withContext(Dispatchers.Main) {
                            detailedRecipe!!.isLike = !(detailedRecipe!!.isLike)
                            detailedRecipe!!.likeNum =
                                if (detailedRecipe!!.isLike) detailedRecipe!!.likeNum.plus(1)
                                else detailedRecipe!!.likeNum.minus(1)

                            binding.textViewDetailedRecipeLikeNum.text =
                                detailedRecipe!!.likeNum.toString()
                        }
                    }
                    if (detailedRecipe!!.isLike) { // 사용자가 좋아요 했는지 여부 처리
                        binding.imageViewDetailedRecipeLikeImage.setImageResource(R.drawable.ic_all_filledheart)
                    } else {
                        binding.imageViewDetailedRecipeLikeImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
                    }
                }
            }
            binding.imageViewDetailedRecipeLikeImage.setOnClickListener(onLikeClickListener)
            binding.textViewDetailedRecipeLikeNum.setOnClickListener(onLikeClickListener)


            // NOTE: 편의점 위치 보러 맵으로 이동
            val goToMapClicked = { anchorView: View, csBrand: String ->
                createBalloon(this@DetailedRecipeActivity) {
                    setArrowSize(10)
                    setWidth(BalloonSizeSpec.WRAP)
                    setHeight(65)
                    setPadding(10)
                    setArrowPosition(0.7f)
                    setCornerRadius(4f)
                    setAutoDismissDuration(2500)
                    setAlpha(0.9f)
                    setText("가까운 주변 편의점 보러 가실래요?")
                    setTextColorResource(R.color.white)
                    setTextIsHtml(true)
                    setIconDrawable(
                        ContextCompat.getDrawable(
                            this@DetailedRecipeActivity,
                            R.drawable.ic_all_place
                        )
                    )
                    setBackgroundColorResource(R.color.balloon_color)
                    setOnBalloonClickListener {
                        // Map으로 이동하기 전에 원하시는 물건이 없을 수도 있다고 사전 고지
                        EitherAOrBDialog(
                            context = this@DetailedRecipeActivity,
                            theme = R.style.BottomSheetDialogTheme,
                            lottieName = "map3.json",
                            title = "주의하세요!!!",
                            message = "주변 편의점에는 해당 상품이 없을 수도 있어요 :(",
                            buttonAText = "취소",
                            buttonBText = "확인",
                            onButtonAClicked = { },
                            ouButtonBClicked = {  // Map으로 이동
                                this@DetailedRecipeActivity.startActivity(
                                    Intent(
                                        this@DetailedRecipeActivity,
                                        CSMapActivity::class.java
                                    ).apply {
                                        putExtra("csBrand", csBrand) // 편의점 브랜드 가치 전송
                                    })
                            }
                        ).show()
                    }
                    setBalloonAnimation(BalloonAnimation.FADE)
                    setLifecycleOwner(lifecycleOwner)
                }.showAlignBottom(anchorView)
            }


            // recipe view init
            launch(Dispatchers.Main) {
                with(binding) {

                    // 뷰페이저 설정 (recipe images)
                    viewpager2DetailedRecipeRecipes.apply {
                        adapter = DetailedReviewOrRecipeImageAdapter(detailedRecipe!!.recipeImageUrls)
                        orientation = ViewPager2.ORIENTATION_HORIZONTAL
                        dotsIndicatorDetailedRecipeIndicator.setViewPager2(this)
                    }

                    // 프로필 이미지
                    Glide.with(this@DetailedRecipeActivity)
                        .load(detailedRecipe!!.userProfileImageUrl)
                        .placeholder(R.drawable.ic_all_loading)
                        .error(R.drawable.img_all_basic_profile)
                        .fallback(R.drawable.img_all_basic_profile)
                        .into(imageViewDetailedRecipeProfileImage)

                    textViewDetailedRecipeNickname.text = detailedRecipe!!.userNickname // 닉네임
                    textViewDetailedRecipeCreatedAt.text = detailedRecipe!!.createdAt // 작성 일시
                    textViewDetailedRecipeRecipeName.text = detailedRecipe!!.recipeName // 레시피 제목
                    textViewDetailedRecipeRecipeContent.text =
                        detailedRecipe!!.recipeContent // 레시피 이름
                    textViewDetailedRecipeLikeNum.text = detailedRecipe!!.likeNum.toString() // 좋아요 개수
                    textViewDetailedRecipeViewNum.text = detailedRecipe!!.viewNum.toString() // 조회수

                    if (detailedRecipe!!.isLike) { // 사용자가 좋아요 했는지 여부 처리리
                        imageViewDetailedRecipeLikeImage.setImageResource(R.drawable.ic_all_filledheart)
                    } else {
                        imageViewDetailedRecipeLikeImage.setImageResource(R.drawable.ic_all_empty_stroke_colored_heart)
                    }

                    // 리사이클러뷰(재료 목록) 초기화
                    // NOTE: 리사이클러뷰 초기화
                    detailedRecipeIngredientAdapter =
                        DetailedRecipeIngredientAdapter(detailedRecipe!!.ingredients, goToMapClicked)
                    progressBarDetailedRecipeLoading.visibility = View.INVISIBLE
                    recyclerViewDetailedRecipeIngredients.apply {
                        adapter = detailedRecipeIngredientAdapter
                        layoutManager = LinearLayoutManager(
                            this@DetailedRecipeActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }


            }


        }
    }

    override fun onBackPressed() {
        if (detailedRecipe != null && position != null && type != null) {
            val detailedReviewIntent = Intent().apply {
                putExtra("detailedRecipe", detailedRecipe)
                putExtra("position", position)
                putExtra("type", this@DetailedRecipeActivity.type)
            }
            setResult(RESULT_OK, detailedReviewIntent)
            super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }
}