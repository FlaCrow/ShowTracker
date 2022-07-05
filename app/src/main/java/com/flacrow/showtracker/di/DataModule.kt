package com.flacrow.showtracker.di

import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.data.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
object DataModule {
    @Provides
    @AppScope
    fun provideRepositoryImpl(showAPI: ShowAPI): RepositoryImpl = RepositoryImpl(showAPI)
}

@Module
interface BindingModule {
    @Binds
    fun bindRepositoryToImpl(repositoryImpl: RepositoryImpl) : Repository
}