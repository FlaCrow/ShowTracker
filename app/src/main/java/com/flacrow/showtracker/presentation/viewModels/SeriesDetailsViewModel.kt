package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.core.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.core.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.core.utils.Extensions.allReverseIteration
import com.flacrow.showtracker.data.models.DateItem
import com.flacrow.showtracker.data.models.DetailedRecyclerItem
import com.flacrow.showtracker.data.models.SeasonLocal
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


//TODO: tidy up code, it is horrible!!!!
class SeriesDetailsViewModel @Inject constructor(override var repository: Repository) :
    BaseDetailedViewModel() {

    private var _isRecyclerExpanded: MutableStateFlow<List<Boolean>> = MutableStateFlow(emptyList())

    private var _recyclerListStateFlow: MutableStateFlow<List<DetailedRecyclerItem>> =
        MutableStateFlow(listOf())
    val recyclerListStateFlow = _recyclerListStateFlow.asStateFlow()

    override fun getData(id: Int) {
        viewModelScope.launch {
            _uiState.value = ShowsDetailsState.Loading
            repository.getTvDetailed(id).catch { e ->
                _uiState.value = ShowsDetailsState.Error(e)
            }.collect { tvDetailedResponse ->
                _uiState.value = ShowsDetailsState.Success(tvDetailedResponse, null)
                _isRecyclerExpanded.update { BooleanArray(tvDetailedResponse.seasons.size).toList() }
                _recyclerListStateFlow.update { tvDetailedResponse.seasons.toMutableList() }
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
                    ShowsDetailsState.Success(
                        uiStateCopy.showDetailed.copy(watchStatus = STATUS_COMPLETED),
                        null
                    )
                }
                repository.saveSeriesToDatabase((uiState.value as ShowsDetailsState.Success).showDetailed as TvDetailed)
//                _isRecyclerExpanded.update { BooleanArray(uiStateCopy.showDetailed.seasons.size).toList() }
                //build list for adapter with expanded recycler
                if (_isRecyclerExpanded.value[position]) {
                    val seasonAdapterUpdatedList: MutableList<DetailedRecyclerItem> =
                        uiStateCopy.showDetailed.seasons.toMutableList()
                    seasonAdapterUpdatedList.addAll(
                        position + 1,
                        uiStateCopy.showDetailed.seasons[position].listOfWatchDates
                    )
                    _recyclerListStateFlow.update { seasonAdapterUpdatedList }
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
                ShowsDetailsState.Success(currentSeries, null)
            }
            _recyclerListStateFlow.update { currentSeries.seasons }
            _isRecyclerExpanded.update { BooleanArray(currentSeries.seasons.size).toList() }
            repository.saveSeriesToDatabase(currentSeries)
        }
    }

    fun expandRecycler(position: Int) {
        _recyclerListStateFlow.update { listOfSeasons ->
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
                _uiState.value = ShowsDetailsState.Success(tvDetailedResponse.result, tvDetailedResponse.exception)
                _isRecyclerExpanded.update { BooleanArray((tvDetailedResponse.result as TvDetailed).seasons.size).toList() }
                _recyclerListStateFlow.update { (tvDetailedResponse.result as TvDetailed).seasons.toMutableList() }
            }
        }
    }
}