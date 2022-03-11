package com.flacrow.showtracker.data.PagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.models.Show
import java.io.IOException

const val STARTING_PAGE = 1

class ShowsPagingSource(private val showAPI: ShowAPI) : PagingSource<Int, Show>() {

    override fun getRefreshKey(state: PagingState<Int, Show>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Show> {
        return try {
            val pageNumber = params.key ?: STARTING_PAGE
            val data = showAPI.getTrending(pageNumber).map {
                Show(
                    id = it.id,
                    title = it.title,
                    poster = it.poster,
                    score = it.score,
                    mediaType = it.mediaType,
                    genres = it.genres,
                    dateAiring = it.dateAiring,
                    overview = it.overview
                )
            }
            LoadResult.Page(
                data = data,
                prevKey = if (pageNumber == STARTING_PAGE) null else pageNumber - 1,
                nextKey = if (data.isEmpty()) null else pageNumber + 1
            )
        } catch (throwable: Throwable) {
            var exception = throwable
            if (throwable is IOException) {
                exception = IOException("Please check internet connection")
            }
            LoadResult.Error(exception)
        }
    }

}