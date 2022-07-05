package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TrendingListViewModel @Inject constructor(private var repository: Repository) : ViewModel() {
    private var currentData: Flow<PagingData<Show>>? = null
    private val tabSelectedMutable: MutableStateFlow<Int> =
        MutableStateFlow(0)
    val tabSelected: StateFlow<Int> = this.tabSelectedMutable

    fun getTrendingList(): Flow<PagingData<Show>> {
        val newData = repository.getTrendingFlow().cachedIn(viewModelScope)
        currentData = newData
        return newData
    }

    fun setSelectedTab(tab: Int) {
        //tabSelectedMutable.value = tab
        this.tabSelectedMutable.update { tab }
    }

    fun getMovieOrTvList(type: Int, query: String): Flow<PagingData<Show>> {
        val newData =
            repository.getMovieOrTvByQuery(type = type, query = query).cachedIn(viewModelScope)
        currentData = newData
        return newData
    }
}