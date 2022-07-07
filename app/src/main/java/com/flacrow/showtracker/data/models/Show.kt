package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.ShowResponse
import java.util.*

data class Show(
    override val id: Int,
    override val title: String,
    override val posterUrl: String,
    val score: Float,
    override val mediaType: String,
    val genres: List<Int>,
    val overview: String
) : IShow


