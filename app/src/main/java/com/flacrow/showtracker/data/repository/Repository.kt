package com.flacrow.showtracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.PagingSources.ShowsSearchPagingSource
import com.flacrow.showtracker.data.PagingSources.ShowsTrendingPagingSource
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.data.models.room.AppDatabase
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
    suspend fun saveTvToDatabase(tvDetailed: TvDetailed)
    fun getSavedMoviesFlow(): Flow<PagingData<MovieDetailed>>
    fun getSavedSeriesFlow(): Flow<PagingData<TvDetailed>>
    fun getSavedMoviesByQuery(query: String) : Flow<PagingData<MovieDetailed>>
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
        emit(showAPI.searchTvById(id).toInternalModel())
    }.flowOn(Dispatchers.IO)

    override fun getMovieDetailed(id: Int): Flow<MovieDetailed> = flow {
        emit(showAPI.searchMovieById(id).toInternalModel())
    }.flowOn(Dispatchers.IO)

    override suspend fun saveMovieToDatabase(movieDetailed: MovieDetailed) {
        database.movieDao().insertMovie(movieDetailed)
    }

    override suspend fun saveTvToDatabase(tvDetailed: TvDetailed) {
        database.tvDao().insertTv(tvDetailed)
    }

    override fun getSavedSeriesFlow(): Flow<PagingData<TvDetailed>> {
        return Pager(config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = { database.tvDao().getAllTv() })
            .flow
    }

    override fun getSavedMoviesFlow(): Flow<PagingData<MovieDetailed>> {
        return Pager(config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = { database.movieDao().getAllMovies() })
            .flow
    }

    override fun getSavedMoviesByQuery(query: String) : Flow<PagingData<MovieDetailed>> {
        return Pager(config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = { database.movieDao().getMoviesByQuery(query) })
            .flow
    }

}
