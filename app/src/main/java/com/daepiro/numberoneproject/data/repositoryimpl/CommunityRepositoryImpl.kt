package com.daepiro.numberoneproject.data.repositoryimpl

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.daepiro.numberoneproject.data.model.ArticleLikeResponse
import com.daepiro.numberoneproject.data.model.CommentLikedResponseModel
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
import com.daepiro.numberoneproject.data.network.ApiService
import com.daepiro.numberoneproject.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Url
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CommunityRepositoryImpl @Inject constructor(
    private val service:ApiService
):CommunityRepository {
    override suspend fun getTownList(
        token: String
    ):ApiResult<GetRegionResponse>{
        return service.getTownList(token)
    }
    override suspend fun getTownCommentList(
        token:String,
        tag:String?,
        longtitude: Double?,
        latitude: Double?,
        regionLv2:String
    ): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 10),
            pagingSourceFactory = {
                CommunityPagingSource(
                    service, token, tag ?: "", longtitude, latitude, regionLv2
                )
            }
        ).flow
    }

    override suspend fun getTownCommentDetail(token:String,articleId:Int):ApiResult<CommunityTownDetailData>{
        return service.getTownCommentDetail(token,articleId)
    }
    override suspend fun setTownDetail(
        token: String,
        title: String,
        content: String,
        articleTag: String,
        imageList: List<MultipartBody.Part>,
        longtitude: Double,
        latitude: Double,
        regionAgreementCheck: Boolean
    )
    : ApiResult<CommentWritingResponse> {

       val map = HashMap<String, RequestBody>()
        var title = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        var content = RequestBody.create("text/plain".toMediaTypeOrNull(), content)
        var articleTag = RequestBody.create("text/plain".toMediaTypeOrNull(), articleTag)
        var longitude = RequestBody.create("text/plain".toMediaTypeOrNull(), longtitude.toString())
        var latitude = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude.toString())
        var regionAgreementCheck = RequestBody.create("text/plain".toMediaTypeOrNull(), regionAgreementCheck.toString())

        map["title"] = title
        map["content"] = content
        map["articleTag"] = articleTag
        map["longitude"] = longitude
        map["latitude"] = latitude
        map["regionAgreementCheck"] = regionAgreementCheck

        return service.setTownDetail(token,imageList, map)

    }

    override suspend fun getTownReply(
        token:String,
        articleId:Int
    ):ApiResult<CommunityTownReplyResponse>{
        return service.getTownReply(token,articleId)
    }

    override suspend fun setTownReply(
        token:String,
        articleid: Int,
        body: CommunityTownReplyRequestBody)
    :ApiResult<CommunityTownReplyResponseModel>{
        return service.setTownReply(token,articleid,body)
    }

    override suspend fun setTownRereply(
        token:String,
        articleid: Int,
        commentid:Int,
        body: CommunityRereplyRequestBody
    ):ApiResult<CommunityTownReplyResponseModel> {
        return service.setTownRereply(token, articleid, commentid, body)
    }

    override suspend fun deleteComment(
        token:String,
        articleid: Int
    ):ApiResult<CommunityTownDeleteCommentResponse>{
        return service.deleteComment(token,articleid)
    }
    override suspend fun deleteReply(
        token:String,
        commentid:Int
    ):ApiResult<CommunityTownReplyDeleteResponse>{
        return service.deleteReply(token,commentid)
    }

    //재난상황 커뮤니티 홈
    override suspend fun getDisasterHome(token:String):ApiResult<CommunityHomeDisasterResponse>{
        return service.getDisasterHome(token)
    }

    //재난상황 커뮤니티 더보기
    override suspend fun getDisasterHomeDetail(token:String, sort:String,disasterId:Int):ApiResult<CommunityDisasterDetailResponse>{
        return service.getDisasterHomeDetail(token,disasterId,sort)
    }

    //재난상황 커뮤니티 댓글작성
    override suspend fun postDisasterConversation(token:String, body: ConversationRequestBody):ApiResult<Unit>{
        return service.postDisasterConversation(token,body)
    }

    fun File.getMimeType(): MediaType?{
        val extension = this.extension.toLowerCase()
        return when(extension){
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/octet-stream"
        }.toMediaTypeOrNull()
    }

    override suspend fun commentLikeControll(
        token: String,
        commentId: Int
    ): ApiResult<CommentLikedResponseModel> {
        return service.commentLikeControll(token, commentId)
    }

    override suspend fun commentLikeCancelControll(
        token: String,
        commentId: Int
    ): ApiResult<CommentLikedResponseModel> {
        return service.commentLikeCancelControll(token, commentId)
    }

    override suspend fun conversationLike(
        token: String,
        conversationId: Int
    ): ApiResult<Any> {
        return service.conversationLike(token, conversationId)
    }
    override suspend fun conversationLikeCancel(
        token: String,
        conversationId: Int
    ): ApiResult<Any> {
        return service.conversationLikeCancel(token, conversationId)
    }

    override suspend fun articleLike(
        token:String,
        articleId: Int
    ): ApiResult<ArticleLikeResponse> {
        return service.articleLikeControll(token, articleId)
    }
    override suspend fun articleCancel(
        token:String,
        articleId: Int
    ): ApiResult<ArticleLikeResponse> {
        return service.articleCancelControll(token, articleId)
    }
}