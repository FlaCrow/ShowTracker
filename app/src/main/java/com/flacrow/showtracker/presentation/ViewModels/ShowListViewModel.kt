package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShowListViewModel @Inject constructor(private var repository: Repository) : ViewModel() {
    private var currentData: Flow<PagingData<Show>>? = null

    fun getList(): Flow<PagingData<Show>> {
        var newData = repository.getTrendingFlow().cachedIn(viewModelScope)
        currentData = newData
        return newData
    }

}