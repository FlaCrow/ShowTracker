package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.Genres

interface IShowDetailed : IShow {
    val backdropUrl: String?
    val firstAirDate: String
    val genres: List<Genres>
    val overview: String
    val rating: Float
    val tagline: String
}