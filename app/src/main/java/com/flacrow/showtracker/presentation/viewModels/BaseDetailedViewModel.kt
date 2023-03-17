package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.CreditsRecyclerItem
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseDetailedViewModel : ViewModel() {

    abstract val repository: Repository

    protected val _uiState: MutableStateFlow<ShowsDetailsState> =
        MutableStateFlow(ShowsDetailsState.Empty)
    val uiState: StateFlow<ShowsDetailsState> = _uiState.asStateFlow()

    private val _creditsPagingDataState: MutableStateFlow<CreditsState> =
        MutableStateFlow(CreditsState.Empty)
    val creditsPagingDataState = _creditsPagingDataState.asStateFlow()

    abstract fun getData(id: Int)

    abstract fun saveWatchStatus(watchStatus: Int)

    fun getCrewData(id: Int, showType: String) {
        viewModelScope.launch {
            repository.getCrewData(id, showType).catch { error ->
                _creditsPagingDataState.update { CreditsState.Error(error) }
            }.collect { crew ->
                _creditsPagingDataState.update { CreditsState.Success(crew) }
            }
        }
    }

    fun getCastData(id: Int, showType: String) {
        viewModelScope.launch {
            repository.getCastData(id, showType).catch { error ->
                _creditsPagingDataState.update { CreditsState.Error(error) }

            }.collect { cast ->
                _creditsPagingDataState.update { CreditsState.Success(cast) }
            }
        }
    }

    sealed class ShowsDetailsState {
        data class Success(val showDetailed: IShowDetailed, val exception: Throwable?) : ShowsDetailsState()
        data class Error(val exception: Throwable) : ShowsDetailsState()
        object Loading : ShowsDetailsState()
        object Empty : ShowsDetailsState()

    }

    sealed class CreditsState {
        data class Success(val creditsRecyclerItem: List<CreditsRecyclerItem>) : CreditsState()
        data class Error(val exception: Throwable) : CreditsState()
        object Empty : CreditsState()
    }


}