package com.flacrow.showtracker.data.models.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.utils.ConstantValues

@Dao
interface TvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTv(movie: TvDetailed)

    @Query("SELECT * FROM ${ConstantValues.TV_TYPE_STRING}")
    fun getAllSeries(): PagingSource<Int, TvDetailed>

    @Query("SELECT * FROM ${ConstantValues.TV_TYPE_STRING} WHERE id is :id")
    fun getSeriesById(id: Int): TvDetailed?

    @Query("SELECT * FROM ${ConstantValues.TV_TYPE_STRING} WHERE INSTR(LOWER(title) , LOWER(:query)) > 0")
    fun getSeriesByQuery(query: String): PagingSource<Int, TvDetailed>
}