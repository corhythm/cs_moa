package com.mju.csmoa.home.recipe.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostRecipeRes(
    val userId: Long,
    val recipeId: Long,
    val recipeImageUrls: List<String>
): Parcelable