package com.hardus.storyapp.utils

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hardus.storyapp.database.entity.EntityStory


class PagedTestData :
    PagingSource<Int, EntityStory>() {

    private val data = mutableListOf<EntityStory>()

    companion object {
        fun snapshot(items: List<EntityStory>): PagingData<EntityStory> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, EntityStory>): Int {
        return (data.lastOrNull()?.id ?: 0) as Int
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EntityStory> {
        val nextKey = params.key ?: 0
        val items = data.filter { it.id > nextKey.toString() }
        return LoadResult.Page(items, null, null)
    }

}

