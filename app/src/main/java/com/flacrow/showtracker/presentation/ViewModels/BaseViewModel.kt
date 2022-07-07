package com.flacrow.showtracker.presentation.ViewModels

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

abstract class BaseViewModel() : ViewModel() {
    protected var currentData: Flow<PagingData<IShow>>? = null
    protected val tabSelectedMutable: MutableStateFlow<Int> =
        MutableStateFlow(0)
    val tabSelected: StateFlow<Int> = this.tabSelectedMutable

    abstract fun getShowList(): Flow<PagingData<IShow>>

    abstract fun setSelectedTab(tab: Int)

    abstract fun getShowListByQuery(type: Int, query: String): Flow<PagingData<IShow>>
}