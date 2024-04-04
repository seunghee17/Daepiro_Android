package com.daepiro.numberoneproject.domain.usecase

import androidx.paging.PagingData
import com.daepiro.numberoneproject.data.model.CommunityTownListModel
import com.daepiro.numberoneproject.data.model.Content
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommunityTownListUseCase @Inject constructor(private val communityRepository: CommunityRepository) {
    suspend operator fun invoke(
        token:String,
        tag:String?,
        longtitude: Double?,
        latitude: Double?,
        regionLv2:String
    ): Flow<PagingData<Content>> {
        return communityRepository.getTownCommentList(token,tag,longtitude,latitude,regionLv2)
    }
}