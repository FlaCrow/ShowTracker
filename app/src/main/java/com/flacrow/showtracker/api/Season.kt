package com.flacrow.showtracker.api

import com.flacrow.showtracker.presentation.adapters.DateItem
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem
import com.google.gson.annotations.SerializedName

data class Season(
    @SerializedName("air_date")
    val dateAired: String? = "No Info",
    @SerializedName("episode_count")
    val episodeCount: Int,
    val name: String,
    var episodeDone: Int = 0,
    var listOfWatchDates: MutableList<DateItem>?,
    @SerializedName("poster_path")
    val posterUrl: String? = null,
    var watchStatus: Int = 0,
    @SerializedName("season_number")
    val seasonNumber: Int,
) : SeasonAdapterItem
