package com.flacrow.showtracker.data.models.room

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.flacrow.core.utils.Config.MOVIE_TYPE_STRING
import com.flacrow.showtracker.data.models.MovieDetailed


@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieDetailed)

    @Query("SELECT * FROM ${MOVIE_TYPE_STRING}")
    fun getAllMovies(): PagingSource<Int, MovieDetailed>

    @Query("SELECT * FROM ${MOVIE_TYPE_STRING} WHERE id IS :id")
    fun getMovieById(id: Int): MovieDetailed?

    @Query("SELECT * FROM ${MOVIE_TYPE_STRING} WHERE INSTR(LOWER(title) , LOWER(:query)) > 0")
    fun getMoviesByQuery(query: String): PagingSource<Int, MovieDetailed>

    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}