package com.flacrow.showtracker.data.api

import com.flacrow.core.utils.ConstantValues.MOVIE_TYPE_STRING
import com.flacrow.core.utils.ConstantValues.TV_TYPE_STRING
import com.flacrow.showtracker.data.models.CastCredits
import com.flacrow.showtracker.data.models.CrewCredits
import com.google.gson.annotations.SerializedName

data class CreditsListsResponse(
    @SerializedName("cast") val castList: List<CreditsResponse>,
    @SerializedName("crew") val crewList: List<CreditsResponse>,
    @SerializedName("id") val showId: Int
)


data class CreditsResponse(
    @SerializedName("id") val personId: Int,
    @SerializedName("known_for_department") val department: String,
    val name: String,
    @SerializedName(value = "character", alternate = ["job"]) val role: String?,
    @SerializedName(value = "roles", alternate = ["jobs"]) val roles: List<Roles>?,
    @SerializedName("profile_path") val photoUrl: String?
)

data class Roles(
    @SerializedName(value = "character", alternate = ["job"]) val characterName: String,
    @SerializedName("episode_count") val episodeCount: Int
)

fun CreditsListsResponse.getCastCreditsList(): List<CastCredits> {
    return castList.flatMap { response ->
        if (response.role != null) listOf(
            CastCredits(
                showId = showId,
                personId = response.personId,
                mediaType = MOVIE_TYPE_STRING,
                department = response.department,
                name = response.name,
                role = response.role,
                photoUrl = response.photoUrl
            )
        )
        else response.roles!!.map {
            CastCredits(
                showId = showId,
                personId = response.personId,
                mediaType = TV_TYPE_STRING,
                department = response.department,
                name = response.name,
                role = it.characterName,
                photoUrl = response.photoUrl
            )
        }
    }
}

fun CreditsListsResponse.getCrewCreditsList(): List<CrewCredits> {
    return crewList.flatMap { response ->
        if (response.role != null) listOf(
            CrewCredits(
                showId = showId,
                personId = response.personId,
                mediaType = MOVIE_TYPE_STRING,
                department = response.department,
                name = response.name,
                role = response.role,
                photoUrl = response.photoUrl
            )
        )
        else response.roles!!.map {
            CrewCredits(
                showId = showId,
                personId = response.personId,
                mediaType = TV_TYPE_STRING,
                department = response.department,
                name = response.name,
                role = it.characterName,
                photoUrl = response.photoUrl
            )
        }
    }
}
