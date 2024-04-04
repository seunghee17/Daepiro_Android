package com.daepiro.numberoneproject.domain.repository

import androidx.paging.PagingData
import com.daepiro.numberoneproject.data.model.CommentWritingRequestBody
import com.daepiro.numberoneproject.data.model.CommentWritingResponse
import com.daepiro.numberoneproject.data.model.CommunityDisasterDetailResponse
import com.daepiro.numberoneproject.data.model.CommunityHomeDisasterResponse
import com.daepiro.numberoneproject.data.model.CommunityHomeSituationModel
import com.daepiro.numberoneproject.data.model.CommunityRereplyRequestBody
import com.daepiro.numberoneproject.data.model.CommunityTownDeleteCommentResponse
import com.daepiro.numberoneproject.data.model.CommunityTownDetailData
import com.daepiro.numberoneproject.data.model.CommunityTownListModel
import com.daepiro.numberoneproject.data.model.CommunityTownReplyDeleteResponse
import com.daepiro.numberoneproject.data.model.CommunityTownReplyRequestBody
import com.daepiro.numberoneproject.data.model.CommunityTownReplyResponse
import com.daepiro.numberoneproject.data.model.CommunityTownReplyResponseModel
import com.daepiro.numberoneproject.data.model.Content
import com.daepiro.numberoneproject.data.model.ConversationRequestBody
import com.daepiro.numberoneproject.data.model.GetRegionResponse
import com.daepiro.numberoneproject.data.network.ApiResult
import com.daepiro.numberoneproject.data.repositoryimpl.CommunityPagingSource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import java.io.File

interface CommunityRepository {
    suspend fun getTownList(token: String):ApiResult<GetRegionResponse>
    suspend fun getTownCommentList(
        token:String,
        tag:String?,
        longtitude: Double?,
        latitude: Double?,
        regionLv2:String
    ): Flow<PagingData<Content>>
    suspend fun getTownCommentDetail(token:String,articleId:Int):ApiResult<CommunityTownDetailData>

    suspend fun setTownDetail(
        token:String,
        title:String,
        content:String,
        articleTag:String,
        imageList:List<MultipartBody.Part>,
        longtitude:Double,
        latitude:Double,
        regionAgreementCheck:Boolean
        ):ApiResult<CommentWritingResponse>

    suspend fun getTownReply(token:String, articleId:Int) : ApiResult<CommunityTownReplyResponse>

    suspend fun setTownReply(token:String,articleId: Int, body: CommunityTownReplyRequestBody):ApiResult<CommunityTownReplyResponseModel>
    suspend fun setTownRereply(token:String, articleId: Int, commentid:Int, body: CommunityRereplyRequestBody):ApiResult<CommunityTownReplyResponseModel>
    suspend fun deleteComment(token:String,articleId: Int):ApiResult<CommunityTownDeleteCommentResponse>
    suspend fun deleteReply(token:String, commentid: Int):ApiResult<CommunityTownReplyDeleteResponse>
    suspend fun getDisasterHome(token:String):ApiResult<CommunityHomeDisasterResponse>
    suspend fun getDisasterHomeDetail(token:String, sort:String,disasterId:Int):ApiResult<CommunityDisasterDetailResponse>
    suspend fun postDisasterConversation(token:String, body: ConversationRequestBody):ApiResult<Unit>
}