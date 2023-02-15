package com.hardus.storyapp.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hardus.storyapp.api.ApiService
import com.hardus.storyapp.database.entity.EntityRemoteKeys
import com.hardus.storyapp.database.entity.EntityStory
import com.hardus.storyapp.database.room.StoryDatabase
import com.hardus.storyapp.util.Constant.INITIAL_PAGE_INDEX

@ExperimentalPagingApi
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String

) : RemoteMediator<Int, EntityStory>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EntityStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }
        try {
            val responseData = apiService.getStory(token, page, state.config.pageSize)
            val endOfPaginationReached = responseData.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    EntityRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                // Save RemoteKeys information to database
                database.remoteKeyDao().insertAll(keys)

                // Convert StoryResponseItem class to Story class
                // We need to convert because the response from API is different from local database Entity
                responseData.listStory.forEach { storyResponseItem ->
                    val story = EntityStory(
                        storyResponseItem.id,
                        storyResponseItem.name,
                        storyResponseItem.description,
                        storyResponseItem.createdAt,
                        storyResponseItem.photoUrl,
                        storyResponseItem.lon,
                        storyResponseItem.lat
                    )

                    // Save Story to the local database
                    database.storyDao().insertStory(story)
                }
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, EntityStory>): EntityRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeyDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, EntityStory>): EntityRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeyDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, EntityStory>): EntityRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeyDao().getRemoteKeysId(id)
            }
        }
    }

}