package com.mju.csmoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mju.csmoa.databinding.ActivityBookmarkBinding;
import com.mju.csmoa.databinding.ItemBookmarkBinding;
import com.mju.csmoa.databinding.ItemMoreBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class BookmarkActivity extends AppCompatActivity {

    private ActivityBookmarkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookmarkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {

        List<ItemBookmark> bookmarkList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            bookmarkList.add(
                    ItemBookmark.builder()
                            .itemName("CJ)비비고 썰은 배추김치 100g")
                            .itemPrice("4,900원")
                            .itemImageSrc("https://~~")
                            .itemStarScore(3.4f)
                            .commentNum(4)
                            .heartNum(10)
                            .build()
            );
        }

        ItemBookmarkAdapter itemBookmarkAdapter = new ItemBookmarkAdapter(bookmarkList);
        binding.recyclerViewBookmarkBookmarkList.setAdapter(itemBookmarkAdapter);
        binding.recyclerViewBookmarkBookmarkList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
    }
}


class ItemBookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemBookmark> bookmarkList;

    public ItemBookmarkAdapter(List<ItemBookmark>  bookmarkList) {
        this. bookmarkList =  bookmarkList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemBookmarkViewHolder(ItemBookmarkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemBookmarkViewHolder) holder).bind( bookmarkList.get(position));
    }

    @Override
    public int getItemCount() {
        return  bookmarkList.size();
    }
}

class ItemBookmarkViewHolder extends RecyclerView.ViewHolder {
    private final ItemBookmarkBinding itemBookmarkBinding;

    public ItemBookmarkViewHolder(@NonNull ItemBookmarkBinding itemBookmarkBinding) {
        super(itemBookmarkBinding.getRoot());
        this.itemBookmarkBinding = itemBookmarkBinding;
    }

    void bind(ItemBookmark itemBookmark) {

    }
}


@Getter
class ItemBookmark {

    private String itemImageSrc;
    private String itemName;
    private String itemPrice;
    private Float itemStarScore;
    private Integer heartNum;
    private Integer commentNum;

    @Builder
    public ItemBookmark(String itemImageSrc, String itemName, String itemPrice, Float itemStarScore, Integer heartNum, Integer commentNum) {
        this.itemImageSrc = itemImageSrc;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemStarScore = itemStarScore;
        this.heartNum = heartNum;
        this.commentNum = commentNum;
    }
}



