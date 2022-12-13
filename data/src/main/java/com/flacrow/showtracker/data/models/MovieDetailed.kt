package com.flacrow.showtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flacrow.core.utils.Config.MOVIE_TYPE_STRING
import com.flacrow.showtracker.data.api.Genres

@Entity(tableName = MOVIE_TYPE_STRING)
data class MovieDetailed(
    @PrimaryKey
    override val id: Int,
    override val title: String,
    override val mediaType: String = MOVIE_TYPE_STRING,
    override val backdropUrl: String?,
    override val firstAirDate: String?,
    override val genres: List<Genres>,
    override val overview: String,
    override val posterUrl: String?,
    val status: String,
    override val tagline: String,
    override val rating: Float,
    override var watchStatus: Int
) : IShowDetailed
