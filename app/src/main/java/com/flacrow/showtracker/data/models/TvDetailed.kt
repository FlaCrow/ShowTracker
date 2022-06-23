package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Seasons
import com.google.gson.annotations.SerializedName

data class TvDetailed(
    val title: String = "",
    val backdropUrl: String = "",
    val firstAirDate: String = "",
    val genres: List<Genres> = emptyList(),
    val id: Int = 0,
    val overview: String = "",
    val posterUrl: String = "",
    val seasons: List<Seasons> = emptyList(),
    val status: String = "",
    val tagline: String = "",
    val rating: Float = 0.0f
)