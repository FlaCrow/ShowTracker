package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.models.SeasonLocal
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.presentation.adapters.DateItem
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.Extensions.allReverseIteration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


//TODO: tidy up code, it is horrible!!!!
class SeriesDetailsViewModel @Inject constructor(private var repository: Repository) :
    BaseDetailedViewModel() {
    private var _seasonListStateFlow: MutableStateFlow<List<SeasonAdapterItem>> =
        MutableStateFlow(listOf())
    val seasonListStateFlow = _seasonListStateFlow.asStateFlow()
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


    fun changeEpisodeWatchedValue(position: Int, newValue: Int) {
        viewModelScope.launch {
            val uiStateCopy = (_uiState.value as ShowsDetailsState.Success).copy()
            if (uiStateCopy.showDetailed is TvDetailed) {
                val oldValue = uiStateCopy.showDetailed.seasons[position].episodeDone
                val date = Calendar.getInstance().time
                uiStateCopy.showDetailed.seasons[position].let { season ->
                    for (i in oldValue until newValue) {
                        season.listOfWatchDates.add(i, DateItem(date, i + 1))
                    }
                    if (oldValue > newValue) {
                        season.listOfWatchDates.subList(newValue, oldValue).clear()
                    }
                }
                uiStateCopy.showDetailed.seasons[position].episodeDone = newValue
                //check if episodeDone == episodeCount : if so for all seasons -> watch status "completed"
                val isCompleted =
                    uiStateCopy.showDetailed.seasons.allReverseIteration { it.episodeDone == it.episodeCount }
                if (isCompleted) _uiState.update {
                    ShowsDetailsState.Success(uiStateCopy.showDetailed.copy(watchStatus = STATUS_COMPLETED))
                }
                repository.saveSeriesToDatabase((uiState.value as ShowsDetailsState.Success).showDetailed as TvDetailed)
//                _isRecyclerExpanded.update { BooleanArray(uiStateCopy.showDetailed.seasons.size).toList() }
                //build list for adapter with expanded recycler
                if (_isRecyclerExpanded.value[position]) {
                    val seasonAdapterUpdatedList: MutableList<SeasonAdapterItem> =
                        uiStateCopy.showDetailed.seasons.toMutableList()
                    seasonAdapterUpdatedList.addAll(
                        position + 1,
                        uiStateCopy.showDetailed.seasons[position].listOfWatchDates
                    )
                    _seasonListStateFlow.update { seasonAdapterUpdatedList }
                }

            }

        }
    }

    override fun saveWatchStatus(watchStatus: Int) {
        viewModelScope.launch {
            val currentSeries =
                ((_uiState.value as ShowsDetailsState.Success).showDetailed as TvDetailed).let { series ->
                    series.copy(seasons = series.seasons.map { season ->
                        season.watchStatus = watchStatus
                        //reset epDone counter if Status = Plan to Watch
                        if (watchStatus == STATUS_PLAN_TO_WATCH) {
                            season.listOfWatchDates = mutableListOf()
                            season.episodeDone = 0
                        }
                        season
                    })
                }
            currentSeries.watchStatus = watchStatus
            _uiState.update {
                ShowsDetailsState.Success(currentSeries)
            }
            _seasonListStateFlow.update { currentSeries.seasons }
            repository.saveSeriesToDatabase(currentSeries)
        }
    }

    fun expandRecycler(position: Int) {
        _seasonListStateFlow.update { listOfSeasons ->
            val listOfSeasonsCopy = listOfSeasons.toMutableList()
            _isRecyclerExpanded.value.forEachIndexed { index, expanded ->
                if (expanded) {
                    listOfSeasonsCopy.removeAll { it is DateItem }
                    _isRecyclerExpanded.update { BooleanArray(listOfSeasonsCopy.size).toList() }
                    if (index == position) return@update listOfSeasonsCopy
                }
            }
            val expandedSeason = listOfSeasonsCopy[position] as SeasonLocal

            //update expanded season number if list of watch dates is not empty
            if (expandedSeason.listOfWatchDates.isNotEmpty())
                _isRecyclerExpanded.update {
                    BooleanArray(listOfSeasonsCopy.size).toMutableList()
                        .let { it[position] = true; it }
                }
            listOfSeasonsCopy.addAll(
                position + 1,
                (expandedSeason).listOfWatchDates
            )

            listOfSeasonsCopy
        }
    }

    fun updateData(id: Int) {
        viewModelScope.launch {
            _uiState.value = ShowsDetailsState.Loading
            repository.updateTvDetailed(id).catch { e ->
                _uiState.value = ShowsDetailsState.Error(e)
            }.collect { tvDetailedResponse ->
                _uiState.value = ShowsDetailsState.Success(tvDetailedResponse)
                _isRecyclerExpanded.update { BooleanArray(tvDetailedResponse.seasons.size).toList() }
                _seasonListStateFlow.update { tvDetailedResponse.seasons.toMutableList() }
            }
        }
    }
}