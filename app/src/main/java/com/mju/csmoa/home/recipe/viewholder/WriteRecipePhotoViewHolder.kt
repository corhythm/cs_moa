package com.mju.csmoa.home.recipe.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mju.csmoa.databinding.ItemWriteRecipeCameraBinding
import com.mju.csmoa.databinding.ItemWriteRecipePhotoBinding
import com.mju.csmoa.home.review.domain.model.Photo

class WriteRecipePhotoViewHolder(
    private val parent: ViewGroup,
    onCancelClicked: (position: Int) -> Unit
) : RecyclerView.ViewHolder(
    ItemWriteRecipePhotoBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ).root
) {

    private val binding = ItemWriteRecipePhotoBinding.bind(itemView)

    init {
        binding.cardViewWriteRecipePhotoCancel.setOnClickListener {
            onCancelClicked.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(photo: Photo) {
        Glide.with(parent.context)
            .load(photo.pictureUri)
            .into(binding.imageViewWriteRecipePhotoImage)
    }
}

class WriteRecipeCameraViewHolder(parent: ViewGroup, onCameraClicked: () -> Unit) :
    RecyclerView.ViewHolder(
        ItemWriteRecipeCameraBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) {

    private val binding = ItemWriteRecipeCameraBinding.bind(itemView)

    init {
        binding.root.setOnClickListener { onCameraClicked.invoke() }
    }

    fun bind(photoNum: Int) {
        binding.textViewWriteRecipeCameraImageCount.text = "$photoNum/5"
    }
}