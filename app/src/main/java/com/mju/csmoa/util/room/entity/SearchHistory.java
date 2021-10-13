package com.mju.csmoa.util.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Entity
public class SearchHistory {

    @PrimaryKey(autoGenerate = true)
    public Integer searchHistoryId;

    @NonNull
    @Getter
    public String searchWord;

    @NonNull
    public String createdAt;

    // 어디서 검색한 검색어인지 e.g. 제품리뷰(0), 행사상품(1), 레시피(?)
    @NonNull
    public Integer type;

    @Builder
    public SearchHistory(@NonNull String searchWord, @NonNull String createdAt, @NonNull Integer type) {
        this.searchWord = searchWord;
        this.createdAt = createdAt;
        this.type = type;
    }
}
