package com.mju.csmoa.home.recipe.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.home.recipe.viewholder.WriteRecipeCameraViewHolder
import com.mju.csmoa.home.recipe.viewholder.WriteRecipePhotoViewHolder
import com.mju.csmoa.home.review.adapter.WriteReviewPhotoAdapter.Companion.CAMERA
import com.mju.csmoa.home.review.domain.model.Photo

class WriteRecipePhotoAdapter(
    private val recipePhotos: List<Photo>,
    private val onCameraClicked: () -> Unit,
    private val onCancelClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int) = recipePhotos[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == CAMERA)
        WriteRecipeCameraViewHolder(parent, onCameraClicked)
    else
        WriteRecipePhotoViewHolder(parent, onCancelClicked)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WriteRecipePhotoViewHolder) {
            holder.bind(recipePhotos[position])
        } else {
            (holder as WriteRecipeCameraViewHolder).bind(recipePhotos.size - 1)
        }
    }

    override fun getItemCount() = recipePhotos.size
}