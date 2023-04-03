package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.flacrow.core.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.core.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.core.utils.Extensions.allReverseIteration
import com.flacrow.showtracker.data.models.*
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.Dispatchers
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
                _isRecyclerExpanded.update { BooleanArray(tvDetailedResponse.seasons.size + 1).toList() }
                _recyclerListStateFlow.update { tvDetailedResponse.seasons.toMutableList() }
            }
        }
    }

    fun changeEpisodeWatchedValue(seasonNumber: Int, newValue: Int) {
        viewModelScope.launch {
            val uiStateCopy = (_uiState.value as ShowsDetailsState.Success).copy()
            val recyclerListCopy = (recyclerListStateFlow.value).toMutableList()
            if (uiStateCopy.showDetailed is TvDetailed) {
                //Element 0 is always SeasonLocal
                val seasonsStartFromZero =
                    (recyclerListStateFlow.value[0] as SeasonLocal).seasonNumber == 0
                val changedItemPosition =
                    recyclerListStateFlow.value.indexOfFirst { if (it is SeasonLocal) it.seasonNumber == seasonNumber else false }
                val mappedPosition = if (seasonsStartFromZero) seasonNumber else seasonNumber - 1
                val oldValue = (recyclerListCopy[changedItemPosition] as SeasonLocal).episodeDone
                val date = Calendar.getInstance().time
                (recyclerListCopy[changedItemPosition] as SeasonLocal).let { season ->
                    for (i in oldValue until newValue) {
                        season.listOfWatchDates.add(i, DateItem(date, i + 1))
                    }
                    if (oldValue > newValue) {
                        season.listOfWatchDates.subList(newValue, oldValue).clear()
                    }
                }
                uiStateCopy.showDetailed.seasons[mappedPosition].episodeDone = newValue
                (recyclerListCopy[changedItemPosition] as SeasonLocal).episodeDone = newValue
                //check if episodeDone == episodeCount : if so for all seasons -> watch status "completed"
                val isCompleted =
                    uiStateCopy.showDetailed.seasons.allReverseIteration { it.episodeDone == it.episodeCount }
                if (isCompleted) {
                    _uiState.update {
                        ShowsDetailsState.Success(
                            uiStateCopy.showDetailed.copy(watchStatus = STATUS_COMPLETED), null
                        )
                    }
                    _recyclerListStateFlow.update { uiStateCopy.showDetailed.seasons }
                    return@launch
                }
                repository.saveSeriesToDatabase((uiState.value as ShowsDetailsState.Success).showDetailed as TvDetailed)
                if (_isRecyclerExpanded.value[seasonNumber]) {
                    val watchDatesList =
                        uiStateCopy.showDetailed.seasons[mappedPosition].listOfWatchDates
                    if (changedItemPosition + 1 < recyclerListCopy.size && recyclerListCopy[changedItemPosition + 1] is Episode) {
                        watchDatesList.forEachIndexed { index, dateItem ->
                            val curEp = recyclerListCopy[changedItemPosition + 1 + index]
                            curEp.let {
                                if (it is Episode) recyclerListCopy[changedItemPosition + 1 + index] =
                                    it.copy(epDateWatched = dateItem.getShortFormattedString())
                            }
                        }
                        val unwatchedEpPositionOffset =
                            changedItemPosition + 1 + watchDatesList.size
                        for (i in 0 until recyclerListCopy.size - unwatchedEpPositionOffset) {
                            val curEp = recyclerListCopy[unwatchedEpPositionOffset + i]
                            curEp.let {
                                if (it is Episode) recyclerListCopy[unwatchedEpPositionOffset + i] =
                                    it.copy(epDateWatched = null)
                            }
                        }
                    } else {
                        while (changedItemPosition + 1 < recyclerListCopy.size && recyclerListCopy[changedItemPosition + 1] !is SeasonLocal) recyclerListCopy.removeAt(
                            changedItemPosition + 1
                        )
                        recyclerListCopy.addAll(
                            changedItemPosition + 1, watchDatesList
                        )
                    }
                }
                _recyclerListStateFlow.update { recyclerListCopy }
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
            _isRecyclerExpanded.update { BooleanArray(currentSeries.seasons.size + 1).toList() }
            repository.saveSeriesToDatabase(currentSeries)
        }
    }

    fun expandRecycler(seasonNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _recyclerListStateFlow.update { listOfSeasons ->
                val listOfSeasonsCopy = listOfSeasons.toMutableList()
                val expandedSeasonPosition =
                    listOfSeasonsCopy.indexOfFirst { if (it is SeasonLocal) it.seasonNumber == seasonNumber else false }
                val expandedSeason = listOfSeasonsCopy[expandedSeasonPosition] as SeasonLocal
                _isRecyclerExpanded.value.forEachIndexed { index, expanded ->
                    if (expanded) {
                        if (index == seasonNumber) {
                            _isRecyclerExpanded.update { list ->
                                list.toMutableList().let { it[index] = false; it }
                            }
                            while (expandedSeasonPosition + 1 < listOfSeasonsCopy.size && listOfSeasonsCopy[expandedSeasonPosition + 1] !is SeasonLocal) listOfSeasonsCopy.removeAt(
                                expandedSeasonPosition + 1
                            )
                            return@update listOfSeasonsCopy
                        }
                    }
                }

                repository.getSeasonEpisodes(
                    (uiState.value as ShowsDetailsState.Success).showDetailed.id, seasonNumber
                ).catch {
                    //update expanded season number if list of watch dates is not empty
                    if (expandedSeason.listOfWatchDates.isNotEmpty()) _isRecyclerExpanded.update { list ->
                        list.toMutableList().let {
                            it[seasonNumber] = true; it
                        }
                    }
                    listOfSeasonsCopy.addAll(
                        expandedSeasonPosition + 1, expandedSeason.listOfWatchDates
                    )
                }.collect { episodes ->
                    //update expanded season number if list of watch dates is not empty
                    _isRecyclerExpanded.update { list ->
                        list.toMutableList().let {
                            it[seasonNumber] = true; it
                        }
                    }
                    episodes.forEachIndexed { index, episode ->
                        if (index < expandedSeason.listOfWatchDates.size) listOfSeasonsCopy.add(
                            expandedSeasonPosition + 1 + index,
                            episode.copy(epDateWatched = expandedSeason.listOfWatchDates[index].getShortFormattedString())
                        )
                        else listOfSeasonsCopy.add(expandedSeasonPosition + 1 + index, episode)
                    }
                }
                listOfSeasonsCopy
            }
        }
    }

    fun updateData(id: Int) {
        viewModelScope.launch {
            _uiState.value = ShowsDetailsState.Loading
            repository.updateTvDetailed(id).catch { e ->
                _uiState.value = ShowsDetailsState.Error(e)
            }.collect { tvDetailedResponse ->
                _uiState.value = ShowsDetailsState.Success(
                    tvDetailedResponse.result, tvDetailedResponse.exception
                )
                _isRecyclerExpanded.update { BooleanArray((tvDetailedResponse.result as TvDetailed).seasons.size + 1).toList() }
                _recyclerListStateFlow.update { (tvDetailedResponse.result as TvDetailed).seasons.toMutableList() }
            }
        }
    }
}