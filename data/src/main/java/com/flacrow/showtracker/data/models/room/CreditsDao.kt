package com.flacrow.showtracker.data.models.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flacrow.showtracker.data.models.*

@Dao
interface CreditsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCastList(creditsLists: List<CastCredits>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrewList(creditsLists: List<CrewCredits>)

    @Query("SELECT * FROM CastCredits WHERE showId IS :showId AND mediaType IS :mediaType")
    fun getCastCredits(showId: Int, mediaType: String): List<CastCredits>

    @Query("SELECT * FROM CrewCredits WHERE showId IS :showId AND mediaType IS :mediaType")
    fun getCrewCredits(showId: Int, mediaType: String): List<CrewCredits>

}

