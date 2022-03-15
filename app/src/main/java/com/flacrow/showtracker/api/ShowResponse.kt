package com.flacrow.showtracker.api

import com.google.gson.annotations.SerializedName
import java.util.*

data class ShowResponse(
    @SerializedName("results") var results: ArrayList<Results>
)


data class Results(
    val id: Int,
    @SerializedName(value = "title", alternate = ["name"])
    val title: String,
    @SerializedName(value = "poster_path")
    val poster: String,
    @SerializedName(value = "vote_average")
    val score: Float,
    @SerializedName(value = "media_type")
    val mediaType: String,
    @SerializedName(value = "genre_ids")
    val genres: List<Int>,
//    @SerializedName(value = "first_air_date", alternate = ["release_date"])
//    val dateAiring: Date,
    val overview: String
)