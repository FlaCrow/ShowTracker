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

    @Query("SELECT * FROM CastCredits WHERE showId IS :showId AND mediaType IS :mediaType LIMIT :limit OFFSET :offset")
    fun getCastCredits(showId: Int, mediaType: String, limit: Int, offset: Int): List<CastCredits>

    @Query("SELECT * FROM crewcredits WHERE showId IS :showId AND mediaType IS :mediaType LIMIT :limit OFFSET :offset")
    fun getCrewCredits(showId: Int, mediaType: String, limit: Int, offset: Int): List<CrewCredits>

}

