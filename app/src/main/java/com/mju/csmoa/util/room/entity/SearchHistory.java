package com.mju.csmoa.util.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(@PrimaryKey(autoGenerate = true) var searchHistoryId: Int,
                         var searchWord: String,
                         var createdAt: String,
                         var type: String) {

    constructor(searchWord: String, createdAt: String, type: String) : this() {
        this. searchWord = searchWord
        this.createdAt = createdAt
        this.type = type
    }
}