package com.flacrow.showtracker.di

import com.flacrow.showtracker.data.api.ShowAPI
import com.flacrow.showtracker.data.models.room.AppDatabase
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.data.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
object DataModule {
    @Provides
    @AppScope
    fun provideRepositoryImpl(showAPI: ShowAPI, database: AppDatabase): RepositoryImpl =
        RepositoryImpl(showAPI, database)
}

@Module
interface BindingModule {
    @Binds
    fun bindRepositoryToImpl(repositoryImpl: RepositoryImpl): Repository
}