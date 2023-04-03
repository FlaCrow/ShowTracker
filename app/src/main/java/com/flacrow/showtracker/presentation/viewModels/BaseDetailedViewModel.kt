package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.flacrow.showtracker.data.models.CreditsRecyclerItem
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseDetailedViewModel : ViewModel() {

    abstract val repository: Repository

    protected val _uiState: MutableStateFlow<ShowsDetailsState> =
        MutableStateFlow(ShowsDetailsState.Empty)
    val uiState: StateFlow<ShowsDetailsState> = _uiState.asStateFlow()

    protected val _creditsPagingDataState: MutableStateFlow<CreditsState> =
        MutableStateFlow(CreditsState.Empty)
    val creditsPagingDataState = _creditsPagingDataState.asStateFlow()

    abstract fun getData(id: Int)

    abstract fun saveWatchStatus(watchStatus: Int)

    fun getCrewData() {
        _creditsPagingDataState.update { CreditsState.Success((uiState.value as ShowsDetailsState.Success).showDetailed.crewList) }
    }

    fun getCastData() {
        _creditsPagingDataState.update { CreditsState.Success((uiState.value as ShowsDetailsState.Success).showDetailed.castList) }
    }

    sealed class ShowsDetailsState {
        data class Success(val showDetailed: IShowDetailed, val exception: Throwable?) :
            ShowsDetailsState()
        data class Error(val exception: Throwable) : ShowsDetailsState()
        object Loading : ShowsDetailsState()
        object Empty : ShowsDetailsState()

    }

    sealed class CreditsState {
        data class Success(val creditsRecyclerItem: List<CreditsRecyclerItem>) : CreditsState()
        object Empty : CreditsState()
    }

}