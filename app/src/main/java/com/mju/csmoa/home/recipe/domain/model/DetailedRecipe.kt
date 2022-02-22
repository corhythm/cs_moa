package com.mju.csmoa.home.recipe.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailedRecipe(
    val recipeId: Long,
    val userId: Long,
    val userNickname: String,
    val userProfileImageUrl: String,
    val recipeName: String,
    val ingredients: List<Ingredient>,
    val recipeImageUrls: List<String>,
    val recipeContent: String,
    val viewNum: Int,
    var likeNum: Int,
    var isLike: Boolean,
    val createdAt: String,
) : Parcelable
