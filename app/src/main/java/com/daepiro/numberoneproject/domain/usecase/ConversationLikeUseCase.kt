package com.daepiro.numberoneproject.domain.usecase

import com.daepiro.numberoneproject.data.model.CommentLikedResponseModel
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.domain.repository.CommunityRepository
import javax.inject.Inject

class ConversationLikeUseCase @Inject constructor(
    private val communityRepository: CommunityRepository
) {
    suspend operator fun invoke(
        token: String,
        conversationId: Int
    ): ApiResult<Any> {
        return communityRepository.conversationLike(token, conversationId)
    }
}