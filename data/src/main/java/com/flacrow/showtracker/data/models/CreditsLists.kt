package com.flacrow.showtracker.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flacrow.core.utils.ConstantValues


@Entity(primaryKeys = ["showId", "personId", "mediaType"])
data class CastCredits(
    val showId: Int,
    val personId: Int,
    val mediaType: String,
    val department: String,
    val name: String,
    val role: String,
    val photoUrl: String?
)

@Entity(primaryKeys = ["showId", "personId", "mediaType"])
data class CrewCredits(
    val showId: Int,
    val personId: Int,
    val mediaType: String,
    val department: String,
    val name: String,
    val role: String,
    val photoUrl: String?
)