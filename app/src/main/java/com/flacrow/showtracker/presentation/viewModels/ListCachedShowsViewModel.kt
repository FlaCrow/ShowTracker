package com.flacrow.showtracker.presentation.viewModels

import androidx.paging.PagingData
import androidx.paging.map
import com.flacrow.core.utils.ConstantValues.SEARCH_TYPE_MOVIES
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ListCachedShowsViewModel @Inject constructor(private var repository: Repository) :
    BaseViewModel() {

    override fun setSelectedTab(tab: Int) {
        this.tabSelectedMutable.update { tab }
    }

    override fun getShowList(query: String): Flow<PagingData<IShow>> {

        val newData: Flow<PagingData<IShow>> = if (tabSelected.value == SEARCH_TYPE_MOVIES) {

            repository.getSavedMovies(query).map {
                it.map { movie ->
                    movie
                }
            }
        } else {
            repository.getSavedSeries(query).map {
                it.map { tv ->
                    tv
                }
            }
        }
        currentData = newData
        return newData
    }

}
