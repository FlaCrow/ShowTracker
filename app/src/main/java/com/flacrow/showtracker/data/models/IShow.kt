package com.flacrow.showtracker.data.models

import com.flacrow.showtracker.api.Genres

interface IShow {
    val id: Int
    val title: String
    val posterUrl: String?
    val mediaType: String
    override fun equals(other: Any?): Boolean
}