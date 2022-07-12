package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.api.TvDetailedResponse
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.utils.ConstantValues
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

    fun addCounter(position: Int) {
        _uiState.update { curUiState ->

            (curUiState as SeriesDetailsState.Success)
            val seasonListCopy = curUiState.tvDetailed.seasons.map { it.copy() }
            if (seasonListCopy[position].epDone != seasonListCopy[position].episode_count)
                seasonListCopy[position].epDone += 1
            val tvDetailedCopy: TvDetailed = curUiState.tvDetailed.copy(
                seasons = seasonListCopy
            )
            curUiState.copy(tvDetailedCopy)
        }
    }

    fun subCounter(position: Int) {
        _uiState.update { curUiState ->

            (curUiState as SeriesDetailsState.Success)
            val seasonListCopy = curUiState.tvDetailed.seasons.map { it.copy() }
            if (seasonListCopy[position].epDone > 0)
                seasonListCopy[position].epDone -= 1
            val tvDetailedCopy: TvDetailed = curUiState.tvDetailed.copy(
                seasons = seasonListCopy
            )
            curUiState.copy(tvDetailedCopy)
        }
    }

    fun addToPTW() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                (_uiState.value as SeriesDetailsState.Success).tvDetailed.copy(
                    watchStatus = ConstantValues.STATUS_PLAN_TO_WATCH
                )
            )
        }
    }

    fun addToWatching() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                (_uiState.value as SeriesDetailsState.Success).tvDetailed.copy(
                    watchStatus = ConstantValues.STATUS_WATCHING
                )
            )
        }
    }

    fun addToCMPL() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                (_uiState.value as SeriesDetailsState.Success).tvDetailed.copy(
                    watchStatus = ConstantValues.STATUS_COMPLETED
                )
            )
        }
    }

}