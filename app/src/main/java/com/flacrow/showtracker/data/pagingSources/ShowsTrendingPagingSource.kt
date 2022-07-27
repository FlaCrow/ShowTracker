package com.flacrow.showtracker.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.utils.ConstantValues
import java.io.IOException

class ShowsTrendingPagingSource(private val showAPI: ShowAPI) : PagingSource<Int, IShow>() {

    override fun getRefreshKey(state: PagingState<Int, IShow>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IShow> {
        return try {
            val pageNumber = params.key ?: ConstantValues.STARTING_PAGE
            val data =
                showAPI.getTrending(pageNumber).results
                    .filter { it.mediaType != "person" }
                    .map {
                        Show(it, mediaType = it.mediaType)
                    }

            LoadResult.Page(
                data = data,
                prevKey = if (pageNumber == ConstantValues.STARTING_PAGE) null else pageNumber - 1,
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