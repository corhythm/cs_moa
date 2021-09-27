package com.mju.cs_prototype;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mju.cs_prototype.databinding.ActivityReviewDetailsBinding;
import com.mju.cs_prototype.databinding.ItemUserReviewBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class ReviewDetailsActivity extends AppCompatActivity {

    private ActivityReviewDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {

        List<ItemUserReview> reviewList = new ArrayList<ItemUserReview>();

        // 리뷰 데이터 넣기
        for (int i = 0; i < 10; i++) {
            reviewList.add(
                    ItemUserReview.builder()
                            .imageSrc("https://~~")
                            .username("인생은 실전이다")
                            .updatedAt("04/04/2021")
                            .ratingScore(4.5f)
                            .content("저희 집 근처 CU에서는 팔지 않고 특정 매장에만 팔아서 찾기가 힘들었어요. 그래도 먹어보니 맛있네요. 가격도 저렴하고 겉도 바삭바삭한 감이 유지돼서 만족도가 높았어요.").build()
            );
        }


        userReviewAdapter userReviewAdapter = new userReviewAdapter(reviewList);
        RecyclerViewDecoration decoration = new RecyclerViewDecoration(70);

        binding.recyclerViewReviewDetailsReviewList.setAdapter(userReviewAdapter);
        binding.recyclerViewReviewDetailsReviewList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewReviewDetailsReviewList.addItemDecoration(decoration);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out);
    }
}

// 리사이클러뷰 관련 클래스 프로젝트 관리를 위해 임시로 이렇게 사용
class userReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ItemUserReview> reviewList;

    public userReviewAdapter(List<ItemUserReview> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("로그", "viewholder 생성");
        return new UserReviewViewHolder(ItemUserReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        ));
    }

    // 데이터 바인딩
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((UserReviewViewHolder) holder).bind(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}

class UserReviewViewHolder extends RecyclerView.ViewHolder {
    private final ItemUserReviewBinding itemUserReviewBinding;

    public UserReviewViewHolder(@NonNull ItemUserReviewBinding itemUserReviewBinding) {
        super(itemUserReviewBinding.getRoot());
        this.itemUserReviewBinding = itemUserReviewBinding;
    }

    // 데이터 바인딩
    public void bind(ItemUserReview itemUserReview) {
        itemUserReviewBinding.ratingBarItemUserReviewStarScore.setRating(itemUserReview.getRatingScore());
        itemUserReviewBinding.textViewItemUserReviewUpdatedAt.setText(itemUserReview.getUpdatedAt());
        itemUserReviewBinding.textViewItemUserReviewUsername.setText(itemUserReview.getUsername());
        itemUserReviewBinding.textViewItemUserReviewContent.setText(itemUserReview.getContent());
    }
}

@Getter
class ItemUserReview {

    private String imageSrc;
    private String username;
    private String updatedAt;
    private Float ratingScore;
    private String content;

    @Builder
    public ItemUserReview(String imageSrc, String username, String updatedAt, Float ratingScore, String content) {
        this.imageSrc = imageSrc;
        this.username = username;
        this.updatedAt = updatedAt;
        this.ratingScore = ratingScore;
        this.content = content;
    }
}