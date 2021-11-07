package com.mju.csmoa.home.event_item.adpater

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mju.csmoa.databinding.LoadStateViewBinding
import com.mju.csmoa.util.Constants.TAG

class EventItemLoadStateAdapter(
    private val retry: () -> Unit
) :
    LoadStateAdapter<EventItemLoadStateAdapter.LoadStateViewHolder>() {


    init {
       retry()
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        with(holder.loadStateViewBinding) {
            loadStateRetry.isVisible = loadState !is LoadState.Loading
            loadStateErrorMessage.isVisible = loadState !is LoadState.Loading
            loadStateProgress.isVisible = loadState !is LoadState.Loading

            if (loadState is LoadState.Error) {
                loadStateErrorMessage.text = loadState.error.localizedMessage
            }

            loadStateRetry.setOnClickListener { retry() }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            LoadStateViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    class LoadStateViewHolder(val loadStateViewBinding: LoadStateViewBinding) :
        RecyclerView.ViewHolder(loadStateViewBinding.root)
}