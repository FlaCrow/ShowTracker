package com.flacrow.showtracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.flacrow.showtracker.api.MovieDetailedResponse
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.api.TvDetailedResponse
import com.flacrow.showtracker.data.PagingSources.ShowsSearchPagingSource
import com.flacrow.showtracker.data.PagingSources.ShowsTrendingPagingSource
import com.flacrow.showtracker.data.models.TvDetailed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    fun getTvDetailed(id: Int): Flow<TvDetailedResponse> = flow {
        emit(showAPI.searchTvById(id))
    }.flowOn(Dispatchers.IO)

    fun getMovieDetailed(id: Int): Flow<MovieDetailedResponse> = flow {
        emit(showAPI.searchMovieById(id))
    }.flowOn(Dispatchers.IO)

}
