package com.mju.csmoa

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
import lombok.Builder
import lombok.Getter
import java.util.*

class ReviewMainFragment : Fragment() {
    private var binding: FragmentReviewMainBinding? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReviewMainBinding.inflate(inflater, container, false)
        init()
        return binding!!.root
    }

    private fun init() {
        val reviewMainList: MutableList<ItemReviewMain> = ArrayList()
        for (i in 0..19) {
            reviewMainList.add(
                    ItemReviewMain.builder()
                            .itemImgSrc("https://dev.~~")
                            .itemStarScore(2.8f)
                            .itemName("파워에이드)퍼플스톰 700ml")
                            .itemPrice("1,800원")
                            .heartNum(5)
                            .commentNum(9)
                            .build()
            )
        }
        val reviewMainRecyclerViewAdapter = ReviewMainRecyclerViewAdapter(reviewMainList)
        binding!!.recyclerViewItemReviewItemList.adapter = reviewMainRecyclerViewAdapter
        binding!!.recyclerViewItemReviewItemList.layoutManager = GridLayoutManager(context, 2)
    }

    // binding = null 안 해주면면
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
} // 리사이클러뷰 관련 클래스 프로젝트 관리를 위해 임시로 이렇게 사용

internal class ReviewMainRecyclerViewAdapter(private val reviewItemList: List<ItemReviewMain>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.i("로그", "viewholder 생성")
        return ReviewMainRecyclerViewHolder(ItemReviewMainBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReviewMainRecyclerViewHolder).bind(reviewItemList[position])
    }

    override fun getItemCount(): Int {
        return reviewItemList.size
    }
}

internal class ReviewMainRecyclerViewHolder(private val itemReviewMainBinding: ItemReviewMainBinding) : RecyclerView.ViewHolder(itemReviewMainBinding.root) {
    // 데이터 바인딩
    fun bind(itemReviewMain: ItemReviewMain) {
        itemReviewMainBinding.ratingBarItemReviewMainStarScore.rating = itemReviewMain.getItemStarScore()
        itemReviewMainBinding.textViewItemReviewMainStarScore.text = String.format("%s", itemReviewMain.getItemStarScore())
        itemReviewMainBinding.textViewItemReviewMainItemName.setText(itemReviewMain.getItemName())
        itemReviewMainBinding.textViewItemReviewMainItemPrice.setText(itemReviewMain.getItemPrice())
        itemReviewMainBinding.textViewItemReviewMainHeartNum.text = String.format("%s", itemReviewMain.getHeartNum())
        itemReviewMainBinding.textViewItemReviewMainCommentNum.text = String.format("%s", itemReviewMain.getCommentNum())
    }
}

@Getter
internal class ItemReviewMain @Builder constructor(private val itemImgSrc: String, private val itemStarScore: Float, private val itemName: String, private val itemPrice: String, private val heartNum: Int, private val commentNum: Int)