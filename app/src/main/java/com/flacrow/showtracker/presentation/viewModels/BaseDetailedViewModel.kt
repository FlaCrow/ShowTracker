package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.flacrow.showtracker.data.models.CreditsRecyclerItem
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseDetailedViewModel : ViewModel() {

    abstract val repository: Repository

    protected val _uiState: MutableStateFlow<ShowsDetailsState> =
        MutableStateFlow(ShowsDetailsState.Empty)
    val uiState: StateFlow<ShowsDetailsState> = _uiState.asStateFlow()

    private val _creditsPagingDataState: MutableStateFlow<PagingData<CreditsRecyclerItem>> =
        MutableStateFlow(PagingData.empty())
    val creditsPagingDataState = _creditsPagingDataState.asStateFlow()

    abstract fun getData(id: Int)

    abstract fun saveWatchStatus(watchStatus: Int)

    fun getCrewData(id: Int, showType: String) {
        viewModelScope.launch {
            repository.getCrewData(id, showType).cachedIn(viewModelScope).collect { crew ->
                _creditsPagingDataState.update { crew }
            }
        }
    }

    fun getCastData(id: Int, showType: String) {
        viewModelScope.launch {
            repository.getCastData(id, showType).cachedIn(viewModelScope).collect { cast ->
                _creditsPagingDataState.update { cast }
            }
        }
    }

    sealed class ShowsDetailsState {
        data class Success(val showDetailed: IShowDetailed) : ShowsDetailsState()
        data class Error(val exception: Throwable) : ShowsDetailsState()
        object Loading : ShowsDetailsState()
        object Empty : ShowsDetailsState()

    }


}