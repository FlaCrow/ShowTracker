package com.flacrow.showtracker.data.models.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.utils.ConstantValues

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieDetailed)

    @Query("SELECT * FROM ${ConstantValues.MOVIE_TYPE_STRING}")
    fun getAllMovies(): PagingSource<Int, MovieDetailed>

    @Query("SELECT * FROM ${ConstantValues.MOVIE_TYPE_STRING} WHERE id IS :id")
    fun getMovieById(id: Int): MovieDetailed?

    @Query("SELECT * FROM ${ConstantValues.MOVIE_TYPE_STRING} WHERE INSTR(LOWER(title) , LOWER(:query)) > 0")
    fun getMoviesByQuery(query: String): PagingSource<Int, MovieDetailed>
}