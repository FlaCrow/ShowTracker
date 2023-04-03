package com.flacrow.showtracker.data.models.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flacrow.showtracker.data.models.*

@Database(entities = [MovieDetailed::class, TvDetailed::class], version = 1, exportSchema = false)
@TypeConverters(DataConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun tvDao(): TvDao
}