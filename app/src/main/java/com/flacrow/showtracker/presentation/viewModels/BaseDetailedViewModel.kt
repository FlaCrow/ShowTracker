package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.flacrow.showtracker.data.models.IShowDetailed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseDetailedViewModel : ViewModel() {

    protected val _uiState: MutableStateFlow<ShowsDetailsState> =
        MutableStateFlow(ShowsDetailsState.Empty)
    val uiState: StateFlow<ShowsDetailsState> = _uiState.asStateFlow()

    abstract fun getData(id: Int)


    abstract fun saveWatchStatus(watchStatus: Int)


    sealed class ShowsDetailsState {
        data class Success(val showDetailed: IShowDetailed) : ShowsDetailsState()
        data class Error(val exception: Throwable) : ShowsDetailsState()
        object Loading : ShowsDetailsState()
        object Empty : ShowsDetailsState()

    }


}