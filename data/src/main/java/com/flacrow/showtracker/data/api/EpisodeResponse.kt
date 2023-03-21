package com.flacrow.showtracker.data.api

import com.flacrow.showtracker.data.models.Episode
import com.google.gson.annotations.SerializedName


data class SeasonEpisodesResponse(
    val id: Int,
    @SerializedName(value = "season_number")
    val seasonNumber: Int,
    val episodes: List<EpisodeResponse>
)

data class EpisodeResponse(
    val id: Int,
    val name: String,
    val overview: String,
    @SerializedName(value = "air_date")
    val dateAired: String?,
    @SerializedName(value = "episode_number")
    val episodeNumber: Int,
    @SerializedName(value = "still_path")
    val stillUrl: String?,
    @SerializedName(value = "vote_average")
    val rating: Float,
    @SerializedName(value = "vote_count")
    val voteCount: Int
) {
    fun toLocalModel(): Episode {
        return Episode(
            epId = this.id,
            epName = this.name,
            epOverview = this.overview,
            epDateAired = this.dateAired,
            episodeNumber = this.episodeNumber,
            stillUrl = this.stillUrl,
            epRating = this.rating,
            epVoteCount = this.voteCount
        )
    }
}
