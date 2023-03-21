package com.flacrow.showtracker.data.api

import com.flacrow.showtracker.data.models.TvDetailed
import com.google.gson.annotations.SerializedName

data class TvDetailedResponse(
    @SerializedName(value = "title", alternate = ["name"])
    val title: String,
    @SerializedName(value = "backdrop_path")
    val backdropUrl: String?,
    @SerializedName(value = "first_air_date")
    val firstAirDate: String,
    val genres: List<Genres>,
    val id: Int,
    val overview: String,
    @SerializedName(value = "poster_path")
    val posterUrl: String?,
    @SerializedName("seasons")
    val seasonsResponse: List<Season>,
    val status: String,
    val tagline: String,
    @SerializedName(value = "vote_average")
    val rating: Float,
    @SerializedName("last_episode_to_air")
    val lastEpisode: EpisodeResponse?
) {
    fun toLocalModel(): TvDetailed {
        return TvDetailed(
            backdropUrl = this.backdropUrl,
            title = this.title,
            firstAirDate = this.firstAirDate,
            genres = this.genres,
            id = this.id,
            overview = this.overview,
            posterUrl = this.posterUrl,
            seasons = this.seasonsResponse.map { it.toLocalModel() },
            status = this.status,
            tagline = this.tagline,
            rating = this.rating,
            lastEpisode = this.lastEpisode?.toLocalModel(),
            watchStatus = 0
        )
    }
}