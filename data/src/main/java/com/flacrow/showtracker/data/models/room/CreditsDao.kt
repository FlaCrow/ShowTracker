package com.flacrow.showtracker.data.models.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flacrow.core.utils.ConstantValues
import com.flacrow.showtracker.data.models.*
import java.util.Objects

@Dao
interface CreditsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCastList(creditsLists: CastCredits)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrewList(creditsLists: CrewCredits)

    @Query("SELECT * FROM CastCredits WHERE showId IS :showId AND mediaType IS :mediaType")
    fun getCastCredits(showId: Int, mediaType:String) : PagingSource<Int,CastCredits>

    @Query("SELECT * FROM crewcredits WHERE showId IS :showId AND mediaType IS :mediaType")
    fun getCrewCredits(showId: Int, mediaType:String) : PagingSource<Int,CrewCredits>

}

