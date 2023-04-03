package com.flacrow.showtracker.data.models


data class CastCredits(
    override val personId: Int,
    val department: String,
    val name: String,
    val role: String,
    val photoUrl: String?
) : CreditsRecyclerItem

data class CrewCredits(
    override val personId: Int,
    val department: String,
    val name: String,
    val role: String,
    val photoUrl: String?
) : CreditsRecyclerItem

interface CreditsRecyclerItem : DetailedRecyclerItem {
    val personId: Int
}