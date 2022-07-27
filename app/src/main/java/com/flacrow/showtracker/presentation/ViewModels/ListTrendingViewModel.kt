package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ListTrendingViewModel @Inject constructor(private var repository: Repository) :

    BaseViewModel() {

    override fun setSelectedTab(tab: Int) {
        //tabSelectedMutable.value = tab
        this.tabSelectedMutable.update { tab }
    }

    override fun getShowList(query: String): Flow<PagingData<IShow>> {
        val newData: Flow<PagingData<IShow>> =
            if (query.isEmpty())
                currentData ?: repository.getTrendingFlow()
                    .cachedIn(viewModelScope)
            else
                repository.getMovieOrTvByQuery(type = tabSelected.value, query = query)
                    .cachedIn(viewModelScope)
        currentData = newData
        return newData
    }
}