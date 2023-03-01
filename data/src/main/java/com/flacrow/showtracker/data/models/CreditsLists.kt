package com.flacrow.showtracker.data.models

import androidx.room.Entity


@Entity(primaryKeys = ["showId", "personId", "mediaType", "role"])
data class CastCredits(
    val showId: Int,
    override val personId: Int,
    val mediaType: String,
    val department: String,
    val name: String,
    val role: String,
    val photoUrl: String?
) : CreditsRecyclerItem

@Entity(primaryKeys = ["showId", "personId", "mediaType", "role"])
data class CrewCredits(
    val showId: Int,
    override val personId: Int,
    val mediaType: String,
    val department: String,
    val name: String,
    val role: String,
    val photoUrl: String?
) : CreditsRecyclerItem

interface CreditsRecyclerItem : DetailedRecyclerItem {
    val personId: Int
}