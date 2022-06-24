package com.flacrow.showtracker.api

import com.flacrow.showtracker.data.models.TvDetailed
import com.google.gson.annotations.SerializedName

data class TvDetailedResponse(
    val name: String,
    @SerializedName(value = "backdrop_path")
    val backdropUrl: String,
    @SerializedName(value = "first_air_date")
    val firstAirDate: String,
    val genres: List<Genres>,
    val id: Int,
    val overview: String,
    @SerializedName(value = "poster_path")
    val posterUrl: String,
    val seasons: List<Season>,
    val status: String,
    val tagline: String,
    @SerializedName(value = "vote_average")
    val rating: Float
) {
    fun toInternalModel(): TvDetailed {
        return TvDetailed(
            backdropUrl = this.backdropUrl,
            title = name,
            firstAirDate = this.firstAirDate,
            genres = this.genres,
            id = this.id,
            overview = this.overview,
            posterUrl = this.posterUrl,
            seasons = this.seasons,
            status = this.status,
            tagline = this.tagline,
            rating = this.rating
        )
    }
}