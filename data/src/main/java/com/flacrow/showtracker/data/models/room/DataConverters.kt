package com.flacrow.showtracker.data.models.room

import androidx.room.TypeConverter
import com.flacrow.showtracker.data.api.Genres
import com.flacrow.showtracker.data.models.CastCredits
import com.flacrow.showtracker.data.models.CrewCredits
import com.flacrow.showtracker.data.models.SeasonLocal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverters {
    @TypeConverter
    fun fromSeasonsToJson(seasons: List<SeasonLocal>): String {
        val type = object : TypeToken<List<SeasonLocal>>() {}.type
        return Gson().toJson(seasons, type)
    }

    @TypeConverter
    fun fromJsonToSeasons(seasonListString: String): List<SeasonLocal> {
        val type = object : TypeToken<List<SeasonLocal>>() {}.type
        return Gson().fromJson(seasonListString, type)
    }

    @TypeConverter
    fun fromGenresToJson(genres: List<Genres>): String {
        val type = object : TypeToken<List<Genres>>() {}.type
        return Gson().toJson(genres, type)
    }

    @TypeConverter
    fun fromJsonToGenres(crewListString: String): List<Genres> {
        val type = object : TypeToken<List<Genres>>() {}.type
        return Gson().fromJson(crewListString, type)
    }

    @TypeConverter
    fun fromCrewListToJson(crewList: List<CrewCredits>): String {
        val type = object : TypeToken<List<CrewCredits>>() {}.type
        return Gson().toJson(crewList, type)
    }

    @TypeConverter
    fun fromJsonToCrewList(genreListString: String): List<CrewCredits> {
        val type = object : TypeToken<List<CrewCredits>>() {}.type
        return Gson().fromJson(genreListString, type)
    }

    @TypeConverter
    fun fromCastListToJson(castList: List<CastCredits>): String {
        val type = object : TypeToken<List<CastCredits>>() {}.type
        return Gson().toJson(castList, type)
    }

    @TypeConverter
    fun fromJsonToCastList(castListString: String): List<CastCredits> {
        val type = object : TypeToken<List<CastCredits>>() {}.type
        return Gson().fromJson(castListString, type)
    }
}