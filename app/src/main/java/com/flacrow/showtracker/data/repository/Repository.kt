package com.flacrow.showtracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.PagingSources.ShowsSearchPagingSource
import com.flacrow.showtracker.data.PagingSources.ShowsTrendingPagingSource
import javax.inject.Inject

class Repository @Inject constructor(private val showAPI: ShowAPI) {

//    fun getTrending(page: Int) = showAPI.getTrending(page)

    fun getTrendingFlow() = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = 20),
        pagingSourceFactory = { ShowsTrendingPagingSource(showAPI) })
        .flow

    fun getMovieOrTvByQuery(type: Int, query: String) = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = 20),
        pagingSourceFactory = {
            ShowsSearchPagingSource(
                showAPI = showAPI,
                query = query,
                searchType = type
            )
        })
        .flow
}