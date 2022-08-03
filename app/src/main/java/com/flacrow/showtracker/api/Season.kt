package com.flacrow.showtracker.api

import com.flacrow.showtracker.data.models.SeasonLocal
import com.google.gson.annotations.SerializedName

data class Season(
    @SerializedName("air_date")
    val dateAired: String? = "No Info",
    @SerializedName("episode_count")
    val episodeCount: Int,
    val name: String,
    @SerializedName("poster_path")
    val posterUrl: String? = null,
    @SerializedName("season_number")
    val seasonNumber: Int,
)  {
    fun toInternalModel(): SeasonLocal {
        return SeasonLocal(dateAired = this.dateAired,
            episodeCount = this.episodeCount,
            name = this.name,
            posterUrl = this.posterUrl,
            seasonNumber = this.seasonNumber)

    }
}
