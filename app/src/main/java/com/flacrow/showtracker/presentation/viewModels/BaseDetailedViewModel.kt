package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.flacrow.showtracker.data.models.IShowDetailed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseDetailedViewModel : ViewModel() {

    protected val _uiState: MutableStateFlow<ShowsDetailsState> =
        MutableStateFlow(ShowsDetailsState.Empty)
    val uiState: StateFlow<ShowsDetailsState> = _uiState

    abstract fun getData(id: Int)

    abstract fun addToPTW()

    abstract fun addToWatching()

    abstract fun addToCMPL()


    sealed class ShowsDetailsState {
        data class Success(val showDetailed: IShowDetailed) : ShowsDetailsState()
        data class Error(val exception: Throwable) : ShowsDetailsState()
        object Loading : ShowsDetailsState()
        object Empty : ShowsDetailsState()

    }


}