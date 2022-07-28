package com.flacrow.showtracker.data.models.room

import androidx.room.TypeConverter
import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.presentation.adapters.DateItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Date

class DataConverters {
    @TypeConverter
    fun fromSeasonsToJson(seasons: List<Season>): String {
        val type = object : TypeToken<List<Season>>() {}.type
        return Gson().toJson(seasons, type)
    }

    @TypeConverter
    fun fromJsonToSeasons(seasonListString: String): List<Season> {
        val type = object : TypeToken<List<Season>>() {}.type
        return Gson().fromJson(seasonListString, type)
    }

    @TypeConverter
    fun fromDateToUnixTime(dateItem: DateItem?): Long? {
        return dateItem?.date?.time
    }

    @TypeConverter
    fun fromUnixTimeToDate(timestamp: Long?): DateItem? {
        return timestamp?.let { DateItem(Date(it)) }
    }

    @TypeConverter
    fun fromGenresToJson(genres: List<Genres>): String {
        val type = object : TypeToken<List<Genres>>() {}.type
        return Gson().toJson(genres, type)
    }

    @TypeConverter
    fun fromJsonToGenres(genreListString: String): List<Genres> {
        val type = object : TypeToken<List<Genres>>() {}.type
        return Gson().fromJson(genreListString, type)
    }
}