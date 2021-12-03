package com.mju.csmoa.home.recipe.domain.model

data class Recipe(
    val name: String,
    val recipeMainImageUrl: String,
    val ingredients: List<Ingredient>,
    val likeNum: Int,
    val isLike: Boolean
)
