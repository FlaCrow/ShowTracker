package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
                _uiState.value = MovieDetailsState.Success(movieDetailedResponse.toInternalModel())
            }
        }
    }
    sealed class MovieDetailsState {
        data class Success(val movieDetailed: MovieDetailed) : MovieDetailsState()
        data class Error(val exception: Throwable) : MovieDetailsState()
        object Loading : MovieDetailsState()
        object Empty : MovieDetailsState()

    }
}