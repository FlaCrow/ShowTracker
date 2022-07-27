package com.flacrow.showtracker.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.utils.ConstantValues
import java.io.IOException


class ShowsSearchPagingSource(
    private val showAPI: ShowAPI,
    private val searchType: Int,
    private val query: String
) :
    PagingSource<Int, IShow>() {

    override fun getRefreshKey(state: PagingState<Int, IShow>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IShow> {
        return try {
            val pageNumber = params.key ?: ConstantValues.STARTING_PAGE
            val data =
                if (searchType == ConstantValues.SEARCH_TYPE_MOVIES) showAPI.searchMovieByQuery(
                    query,
                    pageNumber
                ).results.map {
                    Show(it, ConstantValues.MOVIE_TYPE_STRING)
                }
                else showAPI.searchTvByQuery(
                    query,
                    pageNumber
                ).results.map {
                    Show(it, ConstantValues.TV_TYPE_STRING)
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