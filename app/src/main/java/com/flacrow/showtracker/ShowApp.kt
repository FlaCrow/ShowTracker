package com.flacrow.showtracker

import android.app.Application
import com.flacrow.showtracker.data.di.AppComponent
import com.flacrow.showtracker.data.di.DaggerAppComponent

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