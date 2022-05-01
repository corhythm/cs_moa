package com.mju.csmoa.common.util

import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RecyclerViewDecoration(
    private val top: Int,
    private val bottom: Int,
    private val start: Int,
    private val end: Int
) : ItemDecoration() {


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // Objects.requireNonNull(parent.adapter).itemCount - 1 하면 맨 마지막 아이템 적용 안 됨
        if (parent.getChildAdapterPosition(view) != Objects.requireNonNull(parent.adapter).itemCount) {
            outRect.top = this.top
            outRect.bottom = this.bottom
            outRect.left = start
            outRect.right = end
        }
    }
}