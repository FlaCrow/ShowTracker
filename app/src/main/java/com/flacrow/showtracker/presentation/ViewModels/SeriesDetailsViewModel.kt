package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SeriesDetailsViewModel @Inject constructor(private var repository: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesDetailsState.Success(TvDetailed()))
    val uiState: StateFlow<SeriesDetailsState> = _uiState

    fun getData(id: Int) {
        viewModelScope.launch {
            repository.getTvDetailed(id).collect { tvDetailed ->
                _uiState.value = SeriesDetailsState.Success(tvDetailed.toInternalModel())
            }
        }
    }

    sealed class SeriesDetailsState {
        data class Success(val tvDetailed: TvDetailed) : SeriesDetailsState()
        data class Error(val exception: Throwable) : SeriesDetailsState()
    }
}