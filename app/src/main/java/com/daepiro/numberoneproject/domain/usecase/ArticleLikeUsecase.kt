package com.daepiro.numberoneproject.domain.usecase

import com.daepiro.numberoneproject.data.model.ArticleLikeResponse
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.domain.repository.CommunityRepository
import javax.inject.Inject

class ArticleLikeUsecase @Inject constructor(
    private val communityRepository: CommunityRepository
) {
    suspend operator fun invoke(
        token: String,
        articleId: Int
    ): ApiResult<ArticleLikeResponse> {
        return communityRepository.articleLike(token, articleId)
    }
}