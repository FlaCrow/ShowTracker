package com.flacrow.showtracker

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.flacrow.showtracker.di.AppComponent
import com.flacrow.showtracker.di.DaggerAppComponent
import com.flacrow.showtracker.presentation.adapters.SwitchableTypes
import com.flacrow.showtracker.updatefeature.CheckUpdateWorker
import com.flacrow.showtracker.updatefeature.UpdateDependenciesStore
import java.util.concurrent.TimeUnit

class ShowApp : Application() {


    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        UpdateDependenciesStore.dependencies = appComponent
        initCheckUpdateWorker()
    }

    private fun initCheckUpdateWorker() {
        val sharedPrefs = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        setupCheckUpdateWorker(sharedPrefs.getBoolean(SwitchableTypes.UPDATE_IN_BACKGROUND.name, true))
        //        val uniqueWorkRequest = OneTimeWorkRequestBuilder<CheckUpdateWorker>().build()
        //        WorkManager.getInstance(this).enqueueUniqueWork("CheckUpdateWork",
        //            ExistingWorkPolicy.REPLACE,
        //            uniqueWorkRequest)
    }

}

val Context.appComponent: AppComponent
    get() = when (this) {
        is ShowApp -> appComponent
        else -> this.applicationContext.appComponent
    }

fun Context.setupCheckUpdateWorker(allowUpdate: Boolean) {
    if (!allowUpdate) {
        WorkManager.getInstance(this).cancelUniqueWork("CheckUpdateWork")
        return
    }
    val constraints =
        Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()
    val periodicWorkRequest =
        PeriodicWorkRequestBuilder<CheckUpdateWorker>(12, TimeUnit.HOURS)
            .addTag("UpdateWorker")
            .setConstraints(constraints)
            .build()
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        "CheckUpdateWork",
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest
    )
}