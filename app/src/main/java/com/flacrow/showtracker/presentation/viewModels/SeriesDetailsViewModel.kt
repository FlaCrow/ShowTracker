package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.presentation.adapters.DateItem
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class SeriesDetailsViewModel @Inject constructor(private var repository: Repository) :
    BaseDetailedViewModel() {
    private var _seasonListStateFlow: MutableStateFlow<List<SeasonAdapterItem>> =
        MutableStateFlow(listOf())
    val seasonListStateFlow: StateFlow<List<SeasonAdapterItem>>
        get() = _seasonListStateFlow.asStateFlow()
    private var _isRecyclerExpanded: MutableStateFlow<List<Boolean>> = MutableStateFlow(emptyList())

    override fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.value = ShowsDetailsState.Loading
            repository.getTvDetailed(id).catch { e ->
                _uiState.value = ShowsDetailsState.Error(e)
            }.collect { tvDetailedResponse ->
                _uiState.value = ShowsDetailsState.Success(tvDetailedResponse)
                _isRecyclerExpanded.update { BooleanArray(tvDetailedResponse.seasons.size).toList() }
                _seasonListStateFlow.update { tvDetailedResponse.seasons.toMutableList() }
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
                            season.listOfWatchDates?.add(i, DateItem(date, i + 1))
                        }
                        if (oldValue > value) {
                            for (i in oldValue - 1 downTo value) {
                                season.listOfWatchDates?.removeAt(i)
                            }
                        }
                    }
                    (uiStateCopy.showDetailed).seasons[position].epDone = value
                    repository.saveSeriesToDatabase(uiStateCopy.showDetailed)
                    _isRecyclerExpanded.update { BooleanArray(uiStateCopy.showDetailed.seasons.size).toList() }
                    _seasonListStateFlow.update { uiStateCopy.showDetailed.seasons }

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
        _seasonListStateFlow.update {
            val uiStateCopy = (_uiState.value as ShowsDetailsState.Success).copy()
            if (uiStateCopy.showDetailed is TvDetailed) {
                uiStateCopy.showDetailed.seasons
            } else emptyList()
        }

        _seasonListStateFlow.update { listOfSeasons ->
            val listCopy = listOfSeasons.toMutableList()
            listCopy[position].let { item ->
                if (item is Season) {
                    if (!_isRecyclerExpanded.value[position]) {
                        if (item.epDone != 0)
                            _isRecyclerExpanded.update { prevList ->
                                val newList = BooleanArray(prevList.size).toMutableList()
                                newList[position] = true
                                newList
                            }
                        listCopy.addAll(
                            position + 1,
                            item.listOfWatchDates?.filterNotNull() ?: emptyList()
                        )

                    } else {
                        _isRecyclerExpanded.update { prevList ->
                            val newList = prevList.toMutableList()
                            newList[position] = false
                            newList
                        }
                        listCopy.removeAll(
                            item.listOfWatchDates?.filterNotNull() ?: emptyList()
                        )

                    }
                }
            }
            return@update listCopy.toList()
        }
    }

}