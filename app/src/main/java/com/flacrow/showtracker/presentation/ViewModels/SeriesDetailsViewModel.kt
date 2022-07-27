package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SeriesDetailsViewModel @Inject constructor(private var repository: Repository) : ViewModel() {

    private val _uiState: MutableStateFlow<SeriesDetailsState> =
        MutableStateFlow(SeriesDetailsState.Empty)
    val uiState: StateFlow<SeriesDetailsState> = _uiState

    fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.value = SeriesDetailsState.Loading
            repository.getTvDetailed(id).catch { e ->
                _uiState.value = SeriesDetailsState.Error(e)
            }.collect { tvDetailedResponse ->
                _uiState.value = SeriesDetailsState.Success(tvDetailedResponse)
            }
        }
    }

    sealed class SeriesDetailsState {
        data class Success(val tvDetailed: TvDetailed) : SeriesDetailsState()
        data class Error(val exception: Throwable) : SeriesDetailsState()
        object Loading : SeriesDetailsState()
        object Empty : SeriesDetailsState()

    }

    @OptIn(FlowPreview::class)
    fun changeEpisodeWatchedValue(position: Int, newValueFlow: Flow<Int>) {
        viewModelScope.launch {
            val uiStateCopy = (_uiState.value as SeriesDetailsState.Success).copy()
            newValueFlow.debounce(500).collect { value ->
                uiStateCopy.tvDetailed.seasons[position].epDone = value
                repository.saveSeriesToDatabase(uiStateCopy.tvDetailed)
            }
        }
    }

    fun addToPTW() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                (_uiState.value as SeriesDetailsState.Success).tvDetailed.copy(
                    watchStatus = STATUS_PLAN_TO_WATCH
                )
            )
        }
    }

    fun addToWatching() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                (_uiState.value as SeriesDetailsState.Success).tvDetailed.copy(
                    watchStatus = STATUS_WATCHING
                )
            )
        }
    }

    fun addToCMPL() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                (_uiState.value as SeriesDetailsState.Success).tvDetailed.copy(
                    watchStatus = STATUS_COMPLETED
                )
            )
        }

    }

}