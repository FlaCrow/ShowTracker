package com.flacrow.showtracker.data.models

data class Episode(
    val epId: Int,
    val epName: String,
    val epOverview: String,
    val epDateAired: String?,
    val episodeNumber: Int,
    val stillUrl: String?,
    val epRating: Float,
    val epVoteCount: Int
): DetailedRecyclerItem
