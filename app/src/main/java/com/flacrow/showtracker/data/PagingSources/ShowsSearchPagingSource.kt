package com.flacrow.showtracker.data.PagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.utils.ConstantValues
import java.io.IOException


class ShowsSearchPagingSource(
    private val showAPI: ShowAPI,
    private val searchType: Int,
    private val query: String
) :
    PagingSource<Int, Show>() {

    override fun getRefreshKey(state: PagingState<Int, Show>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Show> {
        return try {
            val pageNumber = params.key ?: ConstantValues.STARTING_PAGE
            val data =
                if (searchType == ConstantValues.SEARCH_TYPE_MOVIES) showAPI.searchMovieByQuery(
                    query,
                    pageNumber
                ).results
                else showAPI.searchTvByQuery(
                    query,
                    pageNumber
                ).results

            val dataMapped = data.map {
                Show(
                    id = it.id,
                    title = it.title,
                    poster = it.poster?: " ",
                    score = it.score,
                    mediaType = it.mediaType?: " ",
                    genres = it.genres,
                    //dateAiring = it.dateAiring,
                    overview = it.overview
                )
            }

            LoadResult.Page(
                data = dataMapped,
                prevKey = if (pageNumber == ConstantValues.STARTING_PAGE) null else pageNumber - 1,
                nextKey = if (dataMapped.isEmpty()) null else pageNumber + 1
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