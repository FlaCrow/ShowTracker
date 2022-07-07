package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ListTrendingViewModel @Inject constructor(private var repository: Repository) :
    BaseViewModel() {

    override fun getShowList(): Flow<PagingData<IShow>> {
        val newData = repository.getTrendingFlow().cachedIn(viewModelScope)
        if (currentData == null)
            currentData = newData
        return currentData ?: newData
    }

    override fun setSelectedTab(tab: Int) {
        //tabSelectedMutable.value = tab
        this.tabSelectedMutable.update { tab }
    }

    override fun getShowListByQuery(type: Int, query: String): Flow<PagingData<IShow>> {
        val newData =
            repository.getMovieOrTvByQuery(type = type, query = query).cachedIn(viewModelScope)
        currentData = newData
        return newData
    }
}