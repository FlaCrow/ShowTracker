package com.flacrow.showtracker.data.models

import java.text.SimpleDateFormat
import java.util.*

data class DateItem(var date: Date, val position: Int) : DetailedRecyclerItem
{
    fun getLongFormattedString(): String {
        return SimpleDateFormat("yyyy-MM-dd hh:mm").format(date)
    }

    fun getShortFormattedString(): String {
        return SimpleDateFormat("yyyy-MM-dd").format(date)
    }
}