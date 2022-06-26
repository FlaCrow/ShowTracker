package com.flacrow.showtracker.api

data class Season(
    val air_date: String? = "No Info",
    val episode_count: Int,
    val name: String,
    var epDone: Int = 0,
    val poster_path: String? = null,
    val season_number: Int
)
