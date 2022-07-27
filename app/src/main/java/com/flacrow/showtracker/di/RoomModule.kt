package com.flacrow.showtracker.di

import android.app.Application
import androidx.room.Room
import com.flacrow.showtracker.data.models.room.AppDatabase
import dagger.Module
import dagger.Provides

@Module
object RoomModule {
    @Provides
    @AppScope
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "AppDatabase")
            .build()
    }
}