package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.Results

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

