package com.mju.csmoa.home.review.domain

import android.net.Uri


data class ReviewPicture(
    val type: Int,
    val date: String,
    val pictureUri: Uri?,
    val absoluteFilePath: String?
)
