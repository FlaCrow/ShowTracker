package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.ShowResponse
import java.util.*

data class Show(
    val id: Int,
    val title: String,
    val poster: String,
    val score: Float,
    val mediaType: String,
    val genres: List<Int>,
    val dateAiring: Date,
    val overview: String
)


