package com.flacrow.showtracker.updatefeature

import androidx.annotation.RestrictTo
import com.flacrow.core.FeatureScope
import com.flacrow.showtracker.data.repository.Repository
import dagger.Component
import kotlin.properties.Delegates.notNull

@[FeatureScope Component(
    dependencies = [UpdateFeatureDependencies::class]
)]
internal interface UpdateFeatureComponent {

    fun inject(worker: CheckUpdateWorker)

    @Component.Builder
    interface Builder {
        fun dependencies(dependencies: UpdateFeatureDependencies): Builder
        fun build(): UpdateFeatureComponent
    }
}

interface UpdateFeatureDependencies {
    val repository: Repository
}


interface UpdateDependenciesProvider {

    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val dependencies: UpdateFeatureDependencies

    companion object : UpdateDependenciesProvider by UpdateDependenciesStore
}

object UpdateDependenciesStore : UpdateDependenciesProvider {

    override var dependencies: UpdateFeatureDependencies by notNull()
}