package com.flacrow.showtracker.api

import com.google.gson.annotations.SerializedName

data class TvResponse(
    val id: Int,
    @SerializedName(value = "title", alternate = ["name"])
    val title: String,
    @SerializedName(value = "poster_path")
    val poster: String,
    @SerializedName(value = "vote_average")
    val score: Float,
    val overview: String

)
