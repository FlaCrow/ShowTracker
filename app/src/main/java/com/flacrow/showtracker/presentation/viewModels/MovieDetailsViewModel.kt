package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsViewModel @Inject constructor(override var repository: Repository) :
    BaseDetailedViewModel() {


    override fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.value = ShowsDetailsState.Loading
            repository.fetchMovieCredits(id)
            repository.getMovieDetailed(id).catch { e ->
                _uiState.value = ShowsDetailsState.Error(e)
            }.collect { movieDetailedResponse ->
                _uiState.value = ShowsDetailsState.Success(movieDetailedResponse)
            }
        }
    }



    override fun saveWatchStatus(watchStatus: Int) {
        viewModelScope.launch {
            repository.saveMovieToDatabase(
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as MovieDetailed).copy(
                    watchStatus = watchStatus
                )
            )
        }
    }

}