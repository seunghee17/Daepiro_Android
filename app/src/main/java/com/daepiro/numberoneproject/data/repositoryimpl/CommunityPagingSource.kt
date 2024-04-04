package com.daepiro.numberoneproject.data.repositoryimpl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.daepiro.numberoneproject.data.model.CommunityTownListModel
import com.daepiro.numberoneproject.data.model.Content
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.data.network.ApiService
import javax.inject.Inject

class CommunityPagingSource @Inject constructor(
    private val service: ApiService,
    private val token: String,
    private val tag: String,
    private val longitude: Double?,
    private val latitude: Double?,
    private val regionLv2: String
) : PagingSource<Int, Content>() {
    override fun getRefreshKey(state: PagingState<Int, Content>): Int? {
        return null
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Content> {
        return try{
            val lastArticleId = params.key
            val response = service.getTownCommentList(
                token = token,
                size = params.loadSize,
                page = 0,
                sort = listOf(),
                tag = tag,
                lastArticleId = lastArticleId,
                longitude = longitude,
                latitude = latitude,
                regionLv2 = regionLv2
            )
            if (response is ApiResult.Success) {
                LoadResult.Page(
                    data = response.data.content,
                    prevKey = null,
                    nextKey = response.data.content.lastOrNull()?.id
                )
            } else {
                LoadResult.Error(Throwable("Api 호출 실패"))
            }
        } catch (e: Exception){
            return LoadResult.Error(e)
        }
    }
}