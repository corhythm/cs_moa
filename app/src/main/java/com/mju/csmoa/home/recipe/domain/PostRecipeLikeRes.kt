package com.mju.csmoa.home.recipe.domain

data class PostRecipeLikeRes(
    val recipeId: Long,
    val userId: Long,
    val isLike: Boolean
)