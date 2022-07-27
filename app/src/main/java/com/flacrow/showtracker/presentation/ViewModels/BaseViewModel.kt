package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.flacrow.showtracker.data.models.IShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel : ViewModel() {
    protected var currentData: Flow<PagingData<IShow>>? = null
    protected val tabSelectedMutable: MutableStateFlow<Int> =
        MutableStateFlow(0)
    val tabSelected: StateFlow<Int> = this.tabSelectedMutable

    abstract fun setSelectedTab(tab: Int)

    fun invalidateData() {
        currentData = null
    }

    abstract fun getShowList(query: String): Flow<PagingData<IShow>>
}