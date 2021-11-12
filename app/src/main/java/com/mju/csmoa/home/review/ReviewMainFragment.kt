package com.mju.csmoa.home.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.FragmentReviewMainBinding
import com.mju.csmoa.databinding.ItemReviewMainBinding
import java.util.*

class ReviewMainFragment : Fragment() {
    private var _binding: FragmentReviewMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewMainBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        val reviewMainList: MutableList<ItemReviewMain> = ArrayList()
        for (i in 0..19) {
            reviewMainList.add(
                ItemReviewMain(
                    itemImgSrc = "https://dev.~~",
                    itemStarScore = 2.8f,
                    itemName = "파워에이드)퍼플스톰 700ml",
                    itemPrice = "1,800원",
                    heartNum = 5,
                    commentNum = 9
                ))
        }
        val reviewMainRecyclerViewAdapter = ReviewMainRecyclerViewAdapter(reviewMainList)
        binding.recyclerViewReviewMainMvpReviewList.adapter = reviewMainRecyclerViewAdapter
        binding.recyclerViewReviewMainMvpReviewList.layoutManager = GridLayoutManager(context, 2)
    }

    // binding = null 안 해주면면
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} // 리사이클러뷰 관련 클래스 프로젝트 관리를 위해 임시로 이렇게 사용

class ReviewMainRecyclerViewAdapter(private val reviewItemList: List<ItemReviewMain>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReviewMainRecyclerViewHolder(
            ItemReviewMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReviewMainRecyclerViewHolder).bind(reviewItemList[position])
    }

    override fun getItemCount(): Int {
        return reviewItemList.size
    }
}

class ReviewMainRecyclerViewHolder(private val itemReviewMainBinding: ItemReviewMainBinding) :
    RecyclerView.ViewHolder(itemReviewMainBinding.root) {
    // 데이터 바인딩
    fun bind(itemReviewMain: ItemReviewMain) {
        itemReviewMainBinding.ratingBarItemReviewMainStarScore.rating = itemReviewMain.itemStarScore
        itemReviewMainBinding.textViewItemReviewMainStarScore.text =
            itemReviewMain.itemStarScore.toString()
        itemReviewMainBinding.textViewItemReviewMainItemName.text = itemReviewMain.itemName
        itemReviewMainBinding.textViewItemReviewMainItemPrice.text = itemReviewMain.itemPrice
        itemReviewMainBinding.textViewItemReviewMainHeartNum.text =
            itemReviewMain.heartNum.toString()
        itemReviewMainBinding.textViewItemReviewMainCommentNum.text =
            itemReviewMain.commentNum.toString()
    }
}

data class ItemReviewMain(
    val itemImgSrc: String,
    val itemStarScore: Float,
    val itemName: String,
    val itemPrice: String,
    val heartNum: Int,
    val commentNum: Int
)