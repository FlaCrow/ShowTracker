package com.flacrow.showtracker.di

import android.app.Application
import com.flacrow.showtracker.data.repository.Repository
import com.flacrow.showtracker.presentation.fragments.*
import com.flacrow.showtracker.updatefeature.UpdateFeatureDependencies
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Scope


@Component(modules = [AppModule::class])
@AppScope
interface AppComponent : UpdateFeatureDependencies {
    override val repository: Repository

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }

    fun inject(fragment: MovieDetailsFragment)
    fun inject(fragment: SeriesDetailsFragment)
    fun inject(fragment: ListTrendingFragment)
    fun inject(fragment: ListWatchingFragment)
    fun inject(fragment: ListPlanToWatchFragment)
    fun inject(fragment: ListCompletedFragment)
    fun inject(fragment: SettingsFragment)

}

@Module(includes = [DataModule::class, NetworkModule::class, ViewModelModule::class, BindingModule::class, RoomModule::class])
class AppModule

@Scope
annotation class AppScope