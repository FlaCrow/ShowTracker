package com.flacrow.showtracker.api

import com.flacrow.showtracker.presentation.adapters.DateItem
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem
import java.util.*

data class Season(
    val air_date: String? = "No Info",
    val episode_count: Int,
    val name: String,
    var epDone: Int = 0,
    var listOfWatchDates: MutableList<DateItem?>?,
    val poster_path: String? = null,
    val season_number: Int,
) : SeasonAdapterItem
