package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsViewModel @Inject constructor(override var repository: Repository) :
    BaseDetailedViewModel() {


    override fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.update { ShowsDetailsState.Loading }
            repository.getMovieDetailed(id).catch { e ->
                _uiState.update {
                    ShowsDetailsState.Error(e)
                }
                }.collect { movieDetailedResponse ->
                _uiState.update { ShowsDetailsState.Success(movieDetailedResponse.result, movieDetailedResponse.exception) }
                _creditsPagingDataState.update { CreditsState.Success(movieDetailedResponse.result.castList) }
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