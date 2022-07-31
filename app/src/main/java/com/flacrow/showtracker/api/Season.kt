package com.flacrow.showtracker.api

import com.flacrow.showtracker.presentation.adapters.DateItem
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem

data class Season(
    val air_date: String? = "No Info",
    val episode_count: Int,
    val name: String,
    var epDone: Int = 0,
    var listOfWatchDates: MutableList<DateItem?>?,
    val poster_path: String? = null,
    var watchStatus: Int = 0,
    val season_number: Int,
) : SeasonAdapterItem
