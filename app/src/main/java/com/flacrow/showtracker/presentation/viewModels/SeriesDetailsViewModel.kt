package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.presentation.adapters.DateItem
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class SeriesDetailsViewModel @Inject constructor(private var repository: Repository) :
    BaseDetailedViewModel() {
    var list: MutableList<SeasonAdapterItem>? = null

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
                if (uiStateCopy.showDetailed is TvDetailed) {
                    val oldValue = uiStateCopy.showDetailed.seasons[position].epDone
                    val date = Calendar.getInstance().time
                    uiStateCopy.showDetailed.seasons[position].let { season ->
                        for (i in oldValue until value) {
                            season.listOfWatchDates = season.listOfWatchDates ?: mutableListOf()
                            season.listOfWatchDates?.add(i, DateItem(date))
                        }
                        if (oldValue > value) {
                            for (i in oldValue - 1 downTo value) {
                                season.listOfWatchDates?.removeAt(i)
                            }
                        }
                    }
                    (uiStateCopy.showDetailed).seasons[position].epDone = value
                    repository.saveSeriesToDatabase(uiStateCopy.showDetailed)
                }
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

    fun expandRecycler(position: Int) {
        val uiStateCopy =
            ((_uiState.value as ShowsDetailsState.Success)).copy()
        if (uiStateCopy.showDetailed !is TvDetailed) return
        if (!uiStateCopy.showDetailed.seasons[position].isExpanded) {
            uiStateCopy.showDetailed.seasons[position].isExpanded = true
            _uiState.update { uiStateCopy }
            list?.addAll(position + 1,
                uiStateCopy.showDetailed.seasons[position].listOfWatchDates?.filterNotNull()
                    ?: emptyList())
        } else {
            uiStateCopy.showDetailed.seasons[position].isExpanded = false
            _uiState.update { uiStateCopy }
            list?.removeAll(
                uiStateCopy.showDetailed.seasons[position].listOfWatchDates?.filterNotNull()
                    ?: emptyList())
        }
    }

    fun saveSeasonAdapterList(seasons: MutableList<SeasonAdapterItem>) {
        list = seasons
    }

}