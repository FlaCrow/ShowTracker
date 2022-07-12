package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.utils.ConstantValues.SEARCH_TYPE_MOVIES
import com.flacrow.showtracker.utils.ConstantValues.SEARCH_TYPE_TV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ListCachedShowsViewModel @Inject constructor(private var repository: Repository) :
    BaseViewModel() {

    override fun setSelectedTab(tab: Int) {
        this.tabSelectedMutable.update { tab }
    }

    @Suppress("USELESS_CAST")
    override fun getShowList(query: String): Flow<PagingData<IShow>> {

        val newData: Flow<PagingData<IShow>>?
        newData = if (tabSelected.value == SEARCH_TYPE_MOVIES) {

            repository.getSavedMovies(query).map {
                it.map {
                    it as IShow
                }
            }
        } else {
            repository.getSavedSeries(query).map {
                it.map {
                    it as IShow
                }
            }
        }
        currentData = newData
        return newData
    }

}
