package com.flacrow.showtracker.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.utils.ConstantValues
import com.google.gson.annotations.SerializedName

@Entity(tableName = ConstantValues.MOVIE_TYPE_STRING)
data class MovieDetailed(
    @PrimaryKey
    override val id: Int,
    override val title: String,
    override val mediaType: String = ConstantValues.MOVIE_TYPE_STRING,
    val backdropUrl: String?,
    val firstAirDate: String,
    val genres: List<Genres>,
    val overview: String,
    override val posterUrl: String?,
    val status: String,
    val tagline: String,
    val rating: Float
) : IShow
