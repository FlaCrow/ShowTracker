package com.flacrow.showtracker.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flacrow.core.utils.ConstantValues.TV_TYPE_STRING
import com.flacrow.showtracker.data.api.Genres

@Entity(tableName = TV_TYPE_STRING)
data class TvDetailed(
    @PrimaryKey
    override val id: Int,
    override val title: String,
    override val mediaType: String = TV_TYPE_STRING,
    override val backdropUrl: String?,
    override val firstAirDate: String?,
    override val genres: List<Genres>,
    override val overview: String,
    override val posterUrl: String?,
    val seasons: List<SeasonLocal>,
    val status: String,
    override val tagline: String,
    override val rating: Float,
    override var watchStatus: Int,
    @Embedded
    val lastEpisode: Episode?,
    override val castList: List<CastCredits>,
    override val crewList: List<CrewCredits>
) : IShowDetailed