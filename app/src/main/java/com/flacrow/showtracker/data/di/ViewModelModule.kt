package com.flacrow.showtracker.data.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flacrow.showtracker.api.presentation.ViewModels.ShowListViewModel
import com.flacrow.showtracker.api.presentation.ViewModels.ViewModelFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
interface ViewModelModule {

    @Binds
    @[IntoMap ViewModelKey(ShowListViewModel::class)]
    fun provideShowListViewModel(showListViewModel: ShowListViewModel): ViewModel

    @Binds
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}

@MustBeDocumented
@Retention
@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)