package com.flacrow.showtracker.data.models


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
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonLocal

        if (dateAired != other.dateAired) return false
        if (episodeCount != other.episodeCount) return false
        if (name != other.name) return false
        if (posterUrl != other.posterUrl) return false
        if (seasonNumber != other.seasonNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateAired?.hashCode() ?: 0
        result = 31 * result + episodeCount
        result = 31 * result + name.hashCode()
        result = 31 * result + (posterUrl?.hashCode() ?: 0)
        result = 31 * result + seasonNumber
        return result
    }
}