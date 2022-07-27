package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsViewModel @Inject constructor(private var repository: Repository) : ViewModel() {
    private val _uiState: MutableStateFlow<MovieDetailsState> =
        MutableStateFlow(MovieDetailsState.Empty)
    val uiState: StateFlow<MovieDetailsState> = _uiState


    fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsState.Loading
            repository.getMovieDetailed(id).catch { e ->
                _uiState.value = MovieDetailsState.Error(e)
            }.collect { movieDetailedResponse ->
                _uiState.value = MovieDetailsState.Success(movieDetailedResponse)
            }
        }
    }

    fun addToPTW() {
        viewModelScope.launch {
            repository.saveMovieToDatabase(
                (_uiState.value as MovieDetailsState.Success).movieDetailed.copy(
                    watchStatus = STATUS_PLAN_TO_WATCH
                )
            )
        }

    }

    fun addToWatching() {
        viewModelScope.launch {
            repository.saveMovieToDatabase(
                (_uiState.value as MovieDetailsState.Success).movieDetailed.copy(
                    watchStatus = STATUS_WATCHING
                )
            )
        }

    }

    fun addToCMPL() {
        viewModelScope.launch {
            repository.saveMovieToDatabase(
                (_uiState.value as MovieDetailsState.Success).movieDetailed.copy(
                    watchStatus = STATUS_COMPLETED
                )
            )
        }

    }

    sealed class MovieDetailsState {
        data class Success(val movieDetailed: MovieDetailed) : MovieDetailsState()
        data class Error(val exception: Throwable) : MovieDetailsState()
        object Loading : MovieDetailsState()
        object Empty : MovieDetailsState()

    }
}