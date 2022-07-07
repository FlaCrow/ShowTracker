package com.flacrow.showtracker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flacrow.showtracker.presentation.ViewModels.*
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
interface ViewModelModule {

    @Binds
    @[IntoMap ViewModelKey(ListTrendingViewModel::class)]
    fun provideShowListViewModel(listTrendingViewModel: ListTrendingViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(SeriesDetailsViewModel::class)]
    fun provideSeriesDetailsViewModel(seriesDetailsViewModel: SeriesDetailsViewModel): ViewModel


    @Binds
    @[IntoMap ViewModelKey(MovieDetailsViewModel::class)]
    fun provideMovieDetailsViewModel(movieDetailsViewModel: MovieDetailsViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(ListCachedShowsViewModel::class)]
    fun provideListCachedShowsViewModel(listCachedShowsViewModel: ListCachedShowsViewModel): ViewModel

    @Binds
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}

@MustBeDocumented
@Retention
@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)