package com.flacrow.showtracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flacrow.showtracker.api.MovieDetailedResponse
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.api.TvDetailedResponse
import com.flacrow.showtracker.data.PagingSources.ShowsSearchPagingSource
import com.flacrow.showtracker.data.PagingSources.ShowsTrendingPagingSource
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.data.models.TvDetailed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


interface Repository {
    fun getTrendingFlow(): Flow<PagingData<IShow>>
    fun getMovieOrTvByQuery(type: Int, query: String): Flow<PagingData<IShow>>
    fun getTvDetailed(id: Int): Flow<TvDetailed>
    fun getMovieDetailed(id: Int): Flow<MovieDetailed>
}

class RepositoryImpl @Inject constructor(private val showAPI: ShowAPI) : Repository {


    override fun getTrendingFlow() = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = 20),
        pagingSourceFactory = { ShowsTrendingPagingSource(showAPI) })
        .flow

    override fun getMovieOrTvByQuery(type: Int, query: String) = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = 20),
        pagingSourceFactory = {
            ShowsSearchPagingSource(
                showAPI = showAPI,
                query = query,
                searchType = type
            )
        })
        .flow

    override fun getTvDetailed(id: Int): Flow<TvDetailed> = flow {
        emit(showAPI.searchTvById(id).toInternalModel())
    }.flowOn(Dispatchers.IO)

    override fun getMovieDetailed(id: Int): Flow<MovieDetailed> = flow {
        emit(showAPI.searchMovieById(id).toInternalModel())
    }.flowOn(Dispatchers.IO)

}
