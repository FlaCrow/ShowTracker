package com.flacrow.showtracker.data.models.room

import androidx.room.TypeConverter
import com.flacrow.showtracker.api.Genres
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.presentation.adapters.DateItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    fun fromDateToGson(dateItem: DateItem?): String {
        return Gson().toJson(dateItem)
    }

    @TypeConverter
    fun fromGsonToDate(gsonString: String): DateItem? {
        return Gson().fromJson(gsonString, DateItem::class.java)
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