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

    fun getRecipes(): Flow<PagingData<Recipe>> {
        return Pager(config = PagingConfig(pageSize = RecipePagingDataSource.PAGE_SIZE),
            pagingSourceFactory = { RecipePagingDataSource() }).flow.cachedIn(viewModelScope)
    }
}