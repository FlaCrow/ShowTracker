package com.flacrow.showtracker.data.models.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.utils.ConstantValues

@Dao
interface TvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTv(movie: TvDetailed)

    @Query("SELECT * FROM ${ConstantValues.TV_TYPE_STRING}")
    fun getAllTv(): PagingSource<Int, TvDetailed>

}