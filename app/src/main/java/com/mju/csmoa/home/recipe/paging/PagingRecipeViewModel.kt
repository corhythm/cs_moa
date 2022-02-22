package com.mju.csmoa.home.recipe.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mju.csmoa.home.recipe.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

class PagingRecipeViewModel : ViewModel() {

    // 이렇게 하는 게 맞는지 잘 모르겠다....
    private var searchWord: String? = null
    private var whenSearchingComplete: (() -> Unit)? = null
    private var whenNoReviewSearchResult: (() -> Unit)? = null

    fun setSearchForInfo(
        searchWord: String,
        whenSearchingComplete: () -> Unit,
        whenNoReviewSearchResult: () -> Unit
    ) {
        this.searchWord = searchWord
        this.whenSearchingComplete = whenSearchingComplete
        this.whenNoReviewSearchResult = whenNoReviewSearchResult
    }

    fun getRecipes(): Flow<PagingData<Recipe>> {
        return Pager(config = PagingConfig(pageSize = RecipePagingDataSource.PAGE_SIZE),
            pagingSourceFactory = {
                RecipePagingDataSource(
                    searchWord,
                    whenSearchingComplete,
                    whenNoReviewSearchResult
                )
            }).flow.cachedIn(viewModelScope)
    }
}