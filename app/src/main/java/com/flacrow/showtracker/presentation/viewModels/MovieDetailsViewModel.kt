package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsViewModel @Inject constructor(private var repository: Repository) :
    BaseDetailedViewModel() {


    override fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.value = ShowsDetailsState.Loading
            repository.getMovieDetailed(id).catch { e ->
                _uiState.value = ShowsDetailsState.Error(e)
            }.collect { movieDetailedResponse ->
                _uiState.value = ShowsDetailsState.Success(movieDetailedResponse)
            }
        }
    }

    override fun addToPTW() {
        viewModelScope.launch {
            repository.saveMovieToDatabase(
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as MovieDetailed).copy(
                    watchStatus = STATUS_PLAN_TO_WATCH
                )
            )
        }

    }

    override fun addToWatching() {
        viewModelScope.launch {
            repository.saveMovieToDatabase(
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as MovieDetailed).copy(
                    watchStatus = STATUS_WATCHING
                )
            )
        }

    }

    override fun addToCMPL() {
        viewModelScope.launch {
            repository.saveMovieToDatabase(
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as MovieDetailed).copy(
                    watchStatus = STATUS_COMPLETED
                )
            )
        }

    }
}