package com.mju.cs_prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mju.cs_prototype.databinding.ActivityMyReviewBinding;
import com.mju.cs_prototype.databinding.ItemMyReviewBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class MyReviewActivity extends AppCompatActivity {

    private ActivityMyReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        List<ItemMyReview> myReviewList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            myReviewList.add(
                    ItemMyReview.builder()
                            .itemName("질러)부드러운 육포30g")
                            .itemPrice("3,000원")
                            .itemStarScore(2.5f)
                            .updatedAt("30/02/2021")
                            .content("가격이 조금 부담스럽기는 하지만 다른 소시지에 비해 부드러워요, 만족합니다!!가격이 조금 부담스럽기는 하지만 다른 소시지에 비해 부드러워요.가격이 조금 부담스럽기는 하지만 다른 소시지에 비해 부드러워요, 만족합니다!!가격이 조금 부담스럽기는 하지만 다른 소시지에 비해 부드러워요.가격이 조금 부담스럽기는 하지만 다른 소시지에 비해 부드러워요, 만족합니다!!가격이 조금 부담스럽기는 하지만 다른 소시지에 비해 부드러워요.")
                            .build()
            );
        }

        MyReviewAdapter myReviewAdapter = new MyReviewAdapter(myReviewList);

        binding.recyclerViewMyReviewMyReviewList.setAdapter(myReviewAdapter);
        binding.recyclerViewMyReviewMyReviewList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewMyReviewMyReviewList.addItemDecoration(new RecyclerViewDecoration(70));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out);
    }
}

class MyReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ItemMyReview> myReviewList;

    public MyReviewAdapter(List<ItemMyReview> myReviewList) {
        this.myReviewList = myReviewList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyReviewViewHolder(ItemMyReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyReviewViewHolder) holder).bind(myReviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return myReviewList.size();
    }
}

class MyReviewViewHolder extends RecyclerView.ViewHolder {
    private final ItemMyReviewBinding itemMyReviewBinding;

    public MyReviewViewHolder(@NonNull ItemMyReviewBinding itemMyReviewBinding) {
        super(itemMyReviewBinding.getRoot());
        this.itemMyReviewBinding = itemMyReviewBinding;
    }

    // 데이터 바인딩
    public void bind(ItemMyReview itemMyReview) {
        itemMyReviewBinding.textViewItemMyReviewItemName.setText(itemMyReview.getItemName());
        itemMyReviewBinding.textViewItemMyReviewItemPrice.setText(itemMyReview.getItemPrice());
        itemMyReviewBinding.ratingBarItemMyReviewStarScore.setRating(itemMyReview.getItemStarScore());
        itemMyReviewBinding.textViewItemMyReviewUpdatedAt.setText(itemMyReview.getUpdatedAt());
        itemMyReviewBinding.textViewItemMyReviewContent.setText(itemMyReview.getContent());
    }
}


@Getter
class ItemMyReview {

    private String itemImageSrc;
    private String itemName;
    private String itemPrice;
    private Float itemStarScore;
    private String updatedAt;
    private String content;

    @Builder
    public ItemMyReview(String itemImageSrc, String itemName, String itemPrice, Float itemStarScore, String updatedAt, String content) {
        this.itemImageSrc = itemImageSrc;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemStarScore = itemStarScore;
        this.updatedAt = updatedAt;
        this.content = content;
    }
}