package com.flacrow.showtracker.data.di

import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.repository.Repository
import dagger.Module
import dagger.Provides

@Module
object DataModule {
    @Provides
    @AppScope
    fun provideRepository(showAPI: ShowAPI): Repository = Repository(showAPI)
}