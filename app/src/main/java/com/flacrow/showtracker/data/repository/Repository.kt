package com.flacrow.showtracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.models.room.AppDatabase
import com.flacrow.showtracker.data.pagingSources.ShowsSearchPagingSource
import com.flacrow.showtracker.data.pagingSources.ShowsTrendingPagingSource
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
    suspend fun saveMovieToDatabase(movieDetailed: MovieDetailed)
    suspend fun saveSeriesToDatabase(tvDetailed: TvDetailed)
    fun getSavedMovies(query: String): Flow<PagingData<MovieDetailed>>
    fun getSavedSeries(query: String): Flow<PagingData<TvDetailed>>
}

class RepositoryImpl @Inject constructor(
    private val showAPI: ShowAPI,
    private val database: AppDatabase
) : Repository {


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
        emit(database.tvDao().getSeriesById(id) ?: showAPI.searchTvById(id).toInternalModel())
    }.flowOn(Dispatchers.IO)

    override fun getMovieDetailed(id: Int): Flow<MovieDetailed> = flow {
        emit(database.movieDao().getMovieById(id) ?: showAPI.searchMovieById(id).toInternalModel())
    }.flowOn(Dispatchers.IO)

    override suspend fun saveMovieToDatabase(movieDetailed: MovieDetailed) {
        database.movieDao().insertMovie(movieDetailed)
    }

    override suspend fun saveSeriesToDatabase(tvDetailed: TvDetailed) {
        database.tvDao().insertTv(tvDetailed)
    }


    override fun getSavedMovies(query: String): Flow<PagingData<MovieDetailed>> {
        return Pager(config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                if (query.isEmpty()) database.movieDao().getAllMovies() else database.movieDao()
                    .getMoviesByQuery(query)
            })
            .flow
    }

    override fun getSavedSeries(query: String): Flow<PagingData<TvDetailed>> {
        return Pager(config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                if (query.isEmpty()) database.tvDao().getAllSeries() else database.tvDao()
                    .getSeriesByQuery(query)
            })
            .flow
    }

}
