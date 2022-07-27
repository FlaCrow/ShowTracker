package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

class SeriesDetailsViewModel @Inject constructor(private var repository: Repository) : BaseDetailedViewModel() {

    override fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.value = ShowsDetailsState.Loading
            repository.getTvDetailed(id).catch { e ->
                _uiState.value = ShowsDetailsState.Error(e)
            }.collect { tvDetailedResponse ->
                _uiState.value = ShowsDetailsState.Success(tvDetailedResponse)
            }
        }
    }


    @OptIn(FlowPreview::class)
    fun changeEpisodeWatchedValue(position: Int, newValueFlow: Flow<Int>) {
        viewModelScope.launch {
            val uiStateCopy = (_uiState.value as ShowsDetailsState.Success).copy()
            newValueFlow.debounce(500).collect { value ->

                (uiStateCopy.showDetailed as TvDetailed).seasons[position].epDone = value
                repository.saveSeriesToDatabase(uiStateCopy.showDetailed)
            }
        }
    }

    override fun addToPTW() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as TvDetailed).copy(
                    watchStatus = STATUS_PLAN_TO_WATCH
                )
            )
        }
    }

    override fun addToWatching() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as TvDetailed).copy(
                    watchStatus = STATUS_WATCHING
                )
            )
        }
    }

    override fun addToCMPL() {
        viewModelScope.launch {
            repository.saveSeriesToDatabase(
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as TvDetailed).copy(
                    watchStatus = STATUS_COMPLETED
                )
            )
        }

    }

}