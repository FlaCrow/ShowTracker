package com.flacrow.showtracker.data.di

import android.app.Application
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


}

@Module(includes = [DataModule::class, NetworkModule::class, ViewModelModule::class])
class AppModule

@Scope
annotation class AppScope