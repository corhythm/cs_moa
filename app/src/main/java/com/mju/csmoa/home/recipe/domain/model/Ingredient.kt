package com.mju.csmoa.home.recipe.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val name: String,
    val price: String,
    val csBrand: String
) : Parcelable