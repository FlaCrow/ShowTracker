package com.flacrow.showtracker.data.models

import androidx.core.util.rangeTo
import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Results
import com.flacrow.showtracker.api.ShowResponse
import com.flacrow.showtracker.utils.ConstantValues
import java.util.*

data class Show(
    override val id: Int,
    override val title: String,
    override val posterUrl: String,
    val score: Float,
    override val mediaType: String,
    val genres: List<Int>,
    val overview: String,
    override var watchStatus: Int
) : IShow {
    constructor(responseResult: Results, mediaType: String) : this(
        id = responseResult.id,
        title = responseResult.title,
        posterUrl = responseResult.poster ?: "",
        score = responseResult.score,
        genres = responseResult.genres,
        mediaType = mediaType,
        overview = responseResult.overview,
        watchStatus = 0
    )
}

