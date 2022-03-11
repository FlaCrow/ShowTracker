package com.flacrow.showtracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.PagingSources.ShowsPagingSource
import javax.inject.Inject

class Repository @Inject constructor(private val showAPI: ShowAPI) {

//    fun getTrending(page: Int) = showAPI.getTrending(page)

    fun getTrendingFlow() = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = 20),
        pagingSourceFactory = { ShowsPagingSource(showAPI) })
        .flow
}