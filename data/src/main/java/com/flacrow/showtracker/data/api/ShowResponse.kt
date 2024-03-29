package com.flacrow.showtracker.data.api

import com.google.gson.annotations.SerializedName

data class ShowResponse(
    @SerializedName("results") var results: ArrayList<Results>
)


data class Results(
    val id: Int,
    @SerializedName(value = "title", alternate = ["name"])
    val title: String,
    @SerializedName(value = "poster_path")
    val poster: String?,
    @SerializedName(value = "vote_average")
    val rating: Float,
    @SerializedName(value = "media_type")
    var mediaType: String,
    @SerializedName(value = "genre_ids")
    val genres: List<Int>,
    @SerializedName(value = "first_air_date", alternate = ["release_date"])
    val firstAirDate: String,
    val overview: String
)