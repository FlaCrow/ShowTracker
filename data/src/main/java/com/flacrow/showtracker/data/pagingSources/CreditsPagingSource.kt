package com.flacrow.showtracker.data.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.flacrow.showtracker.data.models.CreditsRecyclerItem
import com.flacrow.showtracker.data.models.room.CreditsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreditsPagingSource(
    private val creditsDao: CreditsDao,
    private val creditsType: String,
    private val showType: String,
    private val showId: Int
) : PagingSource<Int, CreditsRecyclerItem>() {
    override fun getRefreshKey(state: PagingState<Int, CreditsRecyclerItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CreditsRecyclerItem> = withContext(Dispatchers.IO) {
        val pageIndex = params.key ?: 0

        val offset = pageIndex * params.loadSize
        val data = if (creditsType == "Cast") creditsDao.getCastCredits(
            showId,
            showType, params.loadSize, offset
        ) else creditsDao.getCrewCredits(showId, showType, params.loadSize, offset)
        LoadResult.Page(
            data = data,
            prevKey = if (pageIndex == 0) null else pageIndex - 1,
            nextKey = if (data.isEmpty()) null else pageIndex + 1
        )
    }
}