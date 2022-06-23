package com.flacrow.showtracker.di

import android.app.Application
import com.flacrow.showtracker.presentation.fragments.SeriesDetailsFragment
import com.flacrow.showtracker.presentation.fragments.SeriesDetailsFragmentArgs
import com.flacrow.showtracker.presentation.fragments.ShowListFragment
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

    fun inject(fragment: ShowListFragment)
    fun inject(fragment: SeriesDetailsFragment)

}

@Module(includes = [DataModule::class, NetworkModule::class, ViewModelModule::class])
class AppModule

@Scope
annotation class AppScope