package com.flacrow.showtracker.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.flacrow.showtracker.presentation.fragments.*
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Scope


@Component(modules = [AppModule::class])
@AppScope
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }

    fun inject(fragment: MovieDetailsFragment)
    fun inject(fragment: SeriesDetailsFragment)
    fun inject(fragment: ShowListFragment)
}

@Module(includes = [DataModule::class, NetworkModule::class, ViewModelModule::class, BindingModule::class])
class AppModule

@Scope
annotation class AppScope