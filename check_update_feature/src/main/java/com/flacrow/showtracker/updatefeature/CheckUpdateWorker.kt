package com.flacrow.showtracker.updatefeature

import android.content.Context
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flacrow.core.utils.ConstantValues.STATUS_WATCHING
import com.flacrow.showtracker.data.models.TvDetailed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckUpdateWorker(
    appContext: Context, params: WorkerParameters
) : CoroutineWorker(appContext, params) {


    @Inject
    lateinit var dependency: UpdateFeatureDependencies

    init {
        DaggerUpdateFeatureComponent.builder().dependencies(UpdateDependenciesProvider.dependencies)
            .build().inject(this)
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val notificationService = CheckUpdateNotificationService(applicationContext)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationService.createNotificationChannel()
            dependency.repository.getSavedSeriesAsList()
                .filter { it.watchStatus == STATUS_WATCHING }.forEach { show ->
                    val newShow =
                        dependency.repository.updateTvDetailed(show.id).firstOrNull { newShow ->
                            show.lastEpisode != (newShow.result as TvDetailed).lastEpisode
                        }
                    if (newShow != null) {
                        notificationService.showNotification(newShow.result.id, newShow.result.title)
                        notificationService.showSummary()
                    }

                }
            return@withContext Result.success()
        }
    }
}

