package com.mju.csmoa.home.review.domain.model

import android.net.Uri


data class Photo(
    val type: Int,
    val date: String,
    val pictureUri: Uri?,
    val absoluteFilePath: String?
)
