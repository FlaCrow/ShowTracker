package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ListCachedShowsViewModel @Inject constructor(private var repository: Repository) :
    BaseViewModel() {
    protected var currentData: Flow<PagingData<IShow>>? = null

    override fun getShowList(): Flow<PagingData<IShow>> {
        @Suppress("USELESS_CAST")
        val newData = repository.getSavedMoviesFlow().cachedIn(viewModelScope)
            .map { it.map { it as IShow } }
        if (currentData == null)
            currentData = newData
        return currentData ?: newData
    }

    override fun setSelectedTab(tab: Int) {
        this.tabSelectedMutable.update { tab }
    }

    override fun getShowListByQuery(type: Int, query: String): Flow<PagingData<IShow>> {
        @Suppress("USELESS_CAST")
        val newData = repository.getSavedMoviesByQuery(query).cachedIn(viewModelScope)
            .map { it.map { it as IShow } }
        currentData = newData
        return newData
    }

}
