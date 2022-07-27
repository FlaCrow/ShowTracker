package com.flacrow.showtracker.data.models

interface IShow {
    val id: Int
    val title: String
    val posterUrl: String?
    val mediaType: String
    var watchStatus: Int
    override fun equals(other: Any?): Boolean
}