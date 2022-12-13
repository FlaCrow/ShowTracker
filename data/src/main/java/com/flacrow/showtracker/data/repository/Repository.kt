package com.flacrow.showtracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.flacrow.core.utils.ConstantValues.STATUS_WATCHING
import com.flacrow.showtracker.data.api.ShowAPI
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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


interface Repository {
    fun getTrendingFlow(): Flow<PagingData<IShow>>
    fun getMovieOrTvByQuery(type: Int, query: String): Flow<PagingData<IShow>>
    fun getTvDetailed(id: Int): Flow<TvDetailed>
    fun getMovieDetailed(id: Int): Flow<MovieDetailed>
    suspend fun saveMovieToDatabase(movieDetailed: MovieDetailed)
    suspend fun saveSeriesToDatabase(tvDetailed: TvDetailed)
    fun importBackupFile(inputStream: InputStream, outputStream: FileOutputStream)
    fun exportBackupFile(inputStream: FileInputStream, outputStream: OutputStream)
    suspend fun nukeDatabase()
    fun getSavedMovies(query: String): Flow<PagingData<MovieDetailed>>
    fun getSavedSeries(query: String): Flow<PagingData<TvDetailed>>
    fun getSavedSeriesAsList(): List<TvDetailed>
    fun updateTvDetailed(id: Int): Flow<TvDetailed>

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

    override fun updateTvDetailed(id: Int): Flow<TvDetailed> = flow {
        val cachedTvDetailed = database.tvDao().getSeriesById(id)
        if (cachedTvDetailed == null) {
            emit(showAPI.searchTvById(id).toInternalModel())
            return@flow
        }

        val updatedTvDetailed = showAPI.searchTvById(id).toInternalModel()
        //data class SeasonLocal equals is overridden
        if (cachedTvDetailed.isMutableFieldEqual(updatedTvDetailed)) {
            emit(cachedTvDetailed)
            return@flow
        }

        val newSeasonList = cachedTvDetailed.seasons.toMutableList()
        //check if "extras/season 0" season was added, insert it at the start of the new list if so
        if (cachedTvDetailed.seasons[0].seasonNumber != updatedTvDetailed.seasons[0].seasonNumber) {
            newSeasonList.add(0, updatedTvDetailed.seasons[0].copy())
        }


        val newTvDetailed = cachedTvDetailed.copy(
            seasons = newSeasonList.mapIndexed { index, newSeason ->
                newSeason.copy(
                    episodeCount = updatedTvDetailed.seasons[index].episodeCount,
                    dateAired = updatedTvDetailed.seasons[index].dateAired,
                    watchStatus = STATUS_WATCHING
                )
            },
            rating = updatedTvDetailed.rating,
            status = updatedTvDetailed.status,
            watchStatus = STATUS_WATCHING
        )
        saveSeriesToDatabase(newTvDetailed)
        emit(newTvDetailed)
    }.flowOn(Dispatchers.IO)

    override suspend fun nukeDatabase() {
        database.clearAllTables()
    }

    override fun importBackupFile(
        inputStream: InputStream,
        outputStream: FileOutputStream
    ) {
        database.close()
        inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }
        inputStream.close()
        outputStream.close()
    }

    override fun exportBackupFile(
        inputStream: FileInputStream,
        outputStream: OutputStream
    ) {
        database.movieDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
        inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }
        inputStream.close()
        outputStream.close()
    }

    override fun getSavedSeriesAsList() : List<TvDetailed> {
        return database.tvDao().getAllSeriesAsList()
    }


}
fun TvDetailed.isMutableFieldEqual(tvDetailed: TvDetailed): Boolean {

    if (this.seasons != tvDetailed.seasons) return false
    if (this.rating != tvDetailed.rating) return false
    if (this.status != tvDetailed.status) return false
    return true
}