package com.flacrow.showtracker.api

import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.models.TvDetailed
import com.google.gson.annotations.SerializedName

data class MovieDetailedResponse(
    @SerializedName(value = "title", alternate = ["name"])
    val title: String,
    @SerializedName(value = "backdrop_path")
    val backdropUrl: String?,
    @SerializedName(value = "first_air_date", alternate = ["release_date"])
    val firstAirDate: String,
    val genres: List<Genres>,
    val id: Int,
    val overview: String,
    @SerializedName(value = "poster_path")
    val posterUrl: String?,
    val status: String,
    val tagline: String,
    @SerializedName(value = "vote_average")
    val rating: Float
) {
    fun toInternalModel(): MovieDetailed {
        return MovieDetailed(
            backdropUrl = this.backdropUrl,
            title = this.title,
            firstAirDate = this.firstAirDate,
            genres = this.genres,
            id = this.id,
            overview = this.overview,
            posterUrl = this.posterUrl,
            status = this.status,
            tagline = this.tagline,
            rating = this.rating,
            watchStatus = 0
        )
    }
}
