package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.presentation.adapters.DateItem
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem

data class SeasonLocal(
    val dateAired: String? = "No Info",
    val episodeCount: Int,
    val name: String,
    var episodeDone: Int = 0,
    var listOfWatchDates: MutableList<DateItem> = mutableListOf(),
    val posterUrl: String? = null,
    var watchStatus: Int = 0,
    val seasonNumber: Int,
) : SeasonAdapterItem