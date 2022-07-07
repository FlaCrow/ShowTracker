package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.utils.ConstantValues

data class TvDetailed(
    override val id: Int,
    override val title: String,
    override val mediaType: String = ConstantValues.TV_TYPE_STRING,
    val backdropUrl: String?,
    val firstAirDate: String,
    val genres: List<Genres>,
    val overview: String,
    override val posterUrl: String?,
    val seasons: List<Season>,
    val status: String,
    val tagline: String,
    val rating: Float
) : IShow