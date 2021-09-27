package com.mju.cs_prototype;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mju.cs_prototype.databinding.FragmentReviewMainBinding;
import com.mju.cs_prototype.databinding.ItemReviewMainBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class ReviewMainFragment extends Fragment {

    private FragmentReviewMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewMainBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

        List<ItemReviewMain> reviewMainList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            reviewMainList.add(
                    ItemReviewMain.builder()
                            .itemImgSrc("https://dev.~~")
                            .itemStarScore(2.8f)
                            .itemName("파워에이드)퍼플스톰 700ml")
                            .itemPrice("1,800원")
                            .heartNum(5)
                            .commentNum(9)
                            .build()
            );
        }

        ReviewMainRecyclerViewAdapter reviewMainRecyclerViewAdapter = new ReviewMainRecyclerViewAdapter(reviewMainList);
        binding.recyclerViewItemReviewItemList.setAdapter(reviewMainRecyclerViewAdapter);
        binding.recyclerViewItemReviewItemList.setLayoutManager(
                new GridLayoutManager(getContext(), 2)
        );

    }

    // binding = null 안 해주면면
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

// 리사이클러뷰 관련 클래스 프로젝트 관리를 위해 임시로 이렇게 사용
class ReviewMainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ItemReviewMain> reviewItemList;

    public ReviewMainRecyclerViewAdapter(List<ItemReviewMain> reviewItemList) {
        this.reviewItemList = reviewItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("로그", "viewholder 생성");
        return new ReviewMainRecyclerViewHolder(ItemReviewMainBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    // 데이터 바인딩
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ReviewMainRecyclerViewHolder) holder).bind(reviewItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewItemList.size();
    }
}

class ReviewMainRecyclerViewHolder extends RecyclerView.ViewHolder {
    private final ItemReviewMainBinding itemReviewMainBinding;

    public ReviewMainRecyclerViewHolder(@NonNull ItemReviewMainBinding itemReviewMainBinding) {
        super(itemReviewMainBinding.getRoot());
        this.itemReviewMainBinding = itemReviewMainBinding;
    }

    // 데이터 바인딩
    public void bind(ItemReviewMain itemReviewMain) {
        itemReviewMainBinding.ratingBarItemReviewMainStarScore.setRating(itemReviewMain.getItemStarScore());
        itemReviewMainBinding.textViewItemReviewMainStarScore.setText(String.format("%s", itemReviewMain.getItemStarScore()));
        itemReviewMainBinding.textViewItemReviewMainItemName.setText(itemReviewMain.getItemName());
        itemReviewMainBinding.textViewItemReviewMainItemPrice.setText(itemReviewMain.getItemPrice());
        itemReviewMainBinding.textViewItemReviewMainHeartNum.setText(String.format("%s", itemReviewMain.getHeartNum()));
        itemReviewMainBinding.textViewItemReviewMainCommentNum.setText(String.format("%s", itemReviewMain.getCommentNum()));
    }
}

@Getter
class ItemReviewMain {

    private String itemImgSrc;
    private Float itemStarScore;
    private String itemName;
    private String itemPrice;
    private int heartNum;
    private int commentNum;

    @Builder
    public ItemReviewMain(String itemImgSrc, Float itemStarScore, String itemName, String itemPrice, int heartNum, int commentNum) {
        this.itemImgSrc = itemImgSrc;
        this.itemStarScore = itemStarScore;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.heartNum = heartNum;
        this.commentNum = commentNum;
    }

}
