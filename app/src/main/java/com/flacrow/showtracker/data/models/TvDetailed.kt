package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Season

data class TvDetailed(
    val title: String,
    val backdropUrl: String,
    val firstAirDate: String,
    val genres: List<Genres>,
    val id: Int,
    val overview: String,
    val posterUrl: String,
    val seasons: List<Season>,
    val status: String,
    val tagline: String,
    val rating: Float
)