package com.flacrow.showtracker

import android.app.Application
import com.flacrow.showtracker.di.AppComponent
import com.flacrow.showtracker.di.DaggerAppComponent

class ShowApp : Application(){

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }
    override fun onCreate() {
        super.onCreate()

    }
}