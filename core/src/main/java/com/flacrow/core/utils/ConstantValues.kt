package com.flacrow.core.utils

import com.flacrow.core.R

object ConstantValues {
    //BaseListFragment
    const val ANIMATION_DURATION = 300L

    //PageSources
    const val STARTING_PAGE = 1

    //SearchTypeValues
    const val MOVIE_TYPE_STRING = "movie"
    const val TV_TYPE_STRING = "tv"
    const val SEARCH_TYPE_MOVIES = 0
    const val SEARCH_TYPE_TV = 1


    //detailed view tab names
    enum class TabNames(val tabName: String) {
        DETAILED_SEASON_TAB("Seasons"),
        DETAILED_CAST_TAB("Cast"),
        DETAILED_CREW_TAB("Crew")
    }


    //Intent Extras tag for notification
    const val SERIES_ID_EXTRA = "series_id"

    //WatchStatus
    val STATUS_PLAN_TO_WATCH = R.id.ptw_button
    val STATUS_WATCHING = R.id.watching_button
    val STATUS_COMPLETED = R.id.cmpl_button
}