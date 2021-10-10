package com.mju.csmoa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mju.csmoa.databinding.ActivityMoreBinding;
import com.mju.csmoa.databinding.ItemMoreBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class MoreActivity extends AppCompatActivity {

    private ActivityMoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {

//        TypedArray bookmarkImages = getResources().obtainTypedArray(R.array.bookmarkImages);
//        String[] bookmarkNames = getResources().getStringArray(R.array.bookmarkNames);


        List<ItemMore> menuItemList = new ArrayList<>();
        menuItemList.add(ItemMore.builder()
                .drawableImgId(R.drawable.ic_all_filledheart)
                .menuItemName("즐겨찾기")
                .build());
        menuItemList.add(ItemMore.builder()
                .drawableImgId(R.drawable.ic_all_my_review)
                .menuItemName("내가 쓴 리뷰")
                .build());
        menuItemList.add(ItemMore.builder()
                .drawableImgId(R.drawable.ic_all_user)
                .menuItemName("추가 메뉴")
                .build());
        menuItemList.add(ItemMore.builder()
                .drawableImgId(R.drawable.ic_all_user)
                .menuItemName("추가 메뉴")
                .build());
        menuItemList.add(ItemMore.builder()
                .drawableImgId(R.drawable.ic_all_user)
                .menuItemName("추가 메뉴")
                .build());
        menuItemList.add(ItemMore.builder()
                .drawableImgId(R.drawable.ic_all_user)
                .menuItemName("추가 메뉴")
                .build());


        ItemMoreAdapter itemMoreAdapter = new ItemMoreAdapter(menuItemList);
        binding.recyclerViewMoreMenuList.setAdapter(itemMoreAdapter);
        binding.recyclerViewMoreMenuList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out);
    }
}

class ItemMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemMore> menuItemList;

    public ItemMoreAdapter(List<ItemMore> menuItemList) {
        this.menuItemList = menuItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemMoreViewHolder(ItemMoreBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemMoreViewHolder) holder).bind(menuItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return menuItemList.size();
    }
}

class ItemMoreViewHolder extends RecyclerView.ViewHolder {
    private final ItemMoreBinding itemMoreBinding;

    public ItemMoreViewHolder(@NonNull ItemMoreBinding itemMoreBinding) {
        super(itemMoreBinding.getRoot());
        this.itemMoreBinding = itemMoreBinding;
    }

    void bind(ItemMore itemMore) {
        itemMoreBinding.imageViewItemMoreMenuImg.setImageResource(itemMore.getDrawableImgId());
        itemMoreBinding.textViewItemMoreMenuName.setText(itemMore.getMenuItemName());
    }
}


@Getter
class ItemMore {

    private int drawableImgId;
    private String menuItemName;

    @Builder
    public ItemMore(int drawableImgId, String menuItemName) {
        this.drawableImgId = drawableImgId;
        this.menuItemName = menuItemName;
    }
}