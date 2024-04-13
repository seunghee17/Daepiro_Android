package com.daepiro.numberoneproject.presentation.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.daepiro.numberoneproject.data.model.ArticleLikeResponse
import com.daepiro.numberoneproject.data.model.CommentLikedResponseModel
import com.daepiro.numberoneproject.data.model.CommentWritingResponse
import com.daepiro.numberoneproject.data.model.CommunityDisasterDetailResponse
import com.daepiro.numberoneproject.data.model.CommunityHomeDisasterResponse
import com.daepiro.numberoneproject.data.model.CommunityRereplyRequestBody
import com.daepiro.numberoneproject.data.model.CommunityTownDetailData
import com.daepiro.numberoneproject.data.model.CommunityTownListModel
import com.daepiro.numberoneproject.data.model.CommunityTownReplyRequestBody
import com.daepiro.numberoneproject.data.model.CommunityTownReplyResponse
import com.daepiro.numberoneproject.data.model.Content
import com.daepiro.numberoneproject.data.model.ConversationRequestBody
import com.daepiro.numberoneproject.data.model.GetRegionResponse
import com.daepiro.numberoneproject.data.network.onFailure
import com.daepiro.numberoneproject.data.network.onSuccess
import com.daepiro.numberoneproject.domain.usecase.ArticleCancelUsecase
import com.daepiro.numberoneproject.domain.usecase.ArticleLikeUsecase
import com.daepiro.numberoneproject.domain.usecase.CommentLikeCancelUseCase
import com.daepiro.numberoneproject.domain.usecase.CommentLikeUsecase
import com.daepiro.numberoneproject.domain.usecase.ConversationCancelUseCase
import com.daepiro.numberoneproject.domain.usecase.ConversationLikeUseCase
import com.daepiro.numberoneproject.domain.usecase.DeleteCommunityReplyUseCase
import com.daepiro.numberoneproject.domain.usecase.DeleteCommunityTownCommentUseCase
import com.daepiro.numberoneproject.domain.usecase.GetCommunityHomeDetailUseCase
import com.daepiro.numberoneproject.domain.usecase.GetCommunityTownDetailUseCase
import com.daepiro.numberoneproject.domain.usecase.GetCommunityTownListUseCase
import com.daepiro.numberoneproject.domain.usecase.GetDisasterHomeUseCase
import com.daepiro.numberoneproject.domain.usecase.GetTownListUseCase
import com.daepiro.numberoneproject.domain.usecase.GetTownReplyUseCase
import com.daepiro.numberoneproject.domain.usecase.PostDisasterConversationUseCase
import com.daepiro.numberoneproject.domain.usecase.SetCommunityTownReplyWritingUseCase
import com.daepiro.numberoneproject.domain.usecase.SetCommunityTownRereplyWritingUseCase
import com.daepiro.numberoneproject.domain.usecase.SetCommunityWritingUseCase

import com.daepiro.numberoneproject.presentation.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getTownListUseCase:GetTownListUseCase,
    private val getCommunityTownListUseCase: GetCommunityTownListUseCase,
    private val getCommunityTownDetailUseCase: GetCommunityTownDetailUseCase,
    private val setCommunityWritingUseCase: SetCommunityWritingUseCase,
    private val getTownReplyUseCase: GetTownReplyUseCase,
    private val setCommunityTownReplyWritingUseCase: SetCommunityTownReplyWritingUseCase,
    private val setCommunityTownRereplyWritingUseCase: SetCommunityTownRereplyWritingUseCase,
    private val deleteCommunityTownCommentUseCase: DeleteCommunityTownCommentUseCase,
    private val deleteCommunityReplyUseCase: DeleteCommunityReplyUseCase,
    private val getDisasterHomeUseCase: GetDisasterHomeUseCase,
    private val getCommunityHomeDetailUseCase: GetCommunityHomeDetailUseCase,
    private val postDisasterConversationUseCase: PostDisasterConversationUseCase,
    private val commentLikeUsecase: CommentLikeUsecase,
    private val commentLikeCancelUseCase: CommentLikeCancelUseCase,
    private val conversationLikeUseCase: ConversationLikeUseCase,
    private val conversationCancelUseCase: ConversationCancelUseCase,
    private val articlelikeUseCase: ArticleLikeUsecase,
    private val articlecancelUseCase: ArticleCancelUsecase
) : ViewModel() {

    private val _tag = MutableStateFlow<String?>("")
    private val _longtitude = MutableStateFlow<Double?>(0.0)
    private val _latitude = MutableStateFlow<Double?>(0.0)
    private val _regionLv2 = MutableStateFlow<String>("")


    private val _townDetail = MutableStateFlow(CommunityTownDetailData())
    val townDetail=_townDetail.asStateFlow()

    private val _articleLike = MutableStateFlow(ArticleLikeResponse())
    val articleLike = _articleLike.asStateFlow()

    val _isVisible = MutableLiveData<Boolean>()
    val isVisible:LiveData<Boolean> = _isVisible

    private val _writingResult = MutableStateFlow(CommentWritingResponse())
    val writingResult = _writingResult.asStateFlow()

    private val _replyResult = MutableStateFlow(CommunityTownReplyResponse())
    val replyResult = _replyResult.asStateFlow()

    val _tagData = MutableLiveData<String>()
    val tagData:LiveData<String> = _tagData

    private val _replycontent= MutableStateFlow("")
    val replycontent:StateFlow<String> = _replycontent.asStateFlow()

    private val _additionalState = MutableStateFlow("")
    val additionalState:StateFlow<String> = _additionalState.asStateFlow()

    private val _disasterHome = MutableStateFlow(CommunityHomeDisasterResponse())
    val disasterHome = _disasterHome.asStateFlow()

    private val _listLoadingState = MutableStateFlow(true)
    val listLoadingState = _listLoadingState.asStateFlow()

    private val _townList = MutableStateFlow(GetRegionResponse())
    val townList = _townList.asStateFlow()


     var isLastLoaded = false

    fun updateAdditionalType(input:String){
        _additionalState.value= input
    }

    var id:Int=0

    fun updateContent(input:String){
        _replycontent.value = input
    }


    val _selectRegion = MutableStateFlow("")
    val selectRegion:StateFlow<String> = _selectRegion.asStateFlow()
    fun getTownList(){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            getTownListUseCase(token)
                .onSuccess {
                    _townList.value = it
                }
                .onFailure {
                    Log.e("getTownList", "$it")
                }
        }
    }

    //커뮤니티 리스트 무한 스크롤 데이터 호출
    val combinedData = combine(
        _tag, _longtitude, _latitude, _regionLv2
    ) {
        tag, longtitude, latitude, regionLv2 ->
        Triple(tag, Triple(longtitude, latitude, regionLv2), null)
    }.flatMapLatest { (tag, locationInfo, _) ->
        val (longitude, latitude, regionLv2) = locationInfo
        val token = "Bearer ${tokenManager.accessToken.first()}"
        getCommunityTownListUseCase(token, tag, longitude, latitude, regionLv2)
    }

    fun setTag(tag: String){
        _tag.value = tag
    }
    fun setLongitude(longitude: Double){
        _longtitude.value = longitude
    }
    fun setLatitude(latitude: Double){
        _latitude.value = latitude
    }
    fun setRegion(regionLv2: String){
        _regionLv2.value = regionLv2
    }


    fun getTownDetail(articleId: Int){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            getCommunityTownDetailUseCase(token,articleId)
                .onSuccess {deatilData->
                    _townDetail.value = deatilData
                }
                .onFailure {
                    Log.e("getTownDetail","$it")
                }
        }
    }

    fun postComment(title:String, content:String, articleTag:String, imageList: List<MultipartBody.Part>, longtitude:Double, latitude:Double, regionAgreementCheck:Boolean){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            setCommunityWritingUseCase.invoke(token,title,content,articleTag,imageList,longtitude,latitude,regionAgreementCheck)
                .onSuccess {
                    _writingResult.value = it
                }
                .onFailure {
                    Log.d("CommunityViewModel1", "$it")
                }

        }
    }

    //댓글 조회
    fun setReply(articleId:Int){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            getTownReplyUseCase.invoke(token,articleId)
                .onSuccess {response->
                    _replyResult.value = response
                }
                .onFailure {
                    Log.e("getReply","$it")
                }
        }
    }

    //댓글 작성
    fun writeReply(articleId: Int, body: CommunityTownReplyRequestBody){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            setCommunityTownReplyWritingUseCase.invoke(token,articleId,body)
                .onSuccess {
                    setReply(articleId)
                }
                .onFailure {
                    Log.e("writeReply","$it")
                }
        }
    }

    //대댓글 작성
    fun writeRereply(articleid: Int,commentid:Int,body: CommunityRereplyRequestBody){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            setCommunityTownRereplyWritingUseCase.invoke(token,articleid,commentid,body)
                .onSuccess {
                    setReply(articleid)
                }
                .onFailure {
                    Log.e("writeRereply","$it")
                }
        }
    }
    //게시글 삭제
    fun deleteComment(articleId: Int){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            deleteCommunityTownCommentUseCase.invoke(token,articleId)
                .onSuccess{
                    //게시글 삭제 주석처리 추후 포함시켜야함
                    //getTownCommentList(10,null,null,null,null,"신길동")
                }
        }
    }
    //동네생활 댓글 삭제
    fun deleteReply(commentid: Int){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            deleteCommunityReplyUseCase.invoke(token,commentid)
                .onSuccess {
                    townDetail.value?.articleId?.let { it1 -> setReply(it1) }
                }
        }
    }

    val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    //재난상황 api
    fun getDisasterHome(){
        viewModelScope.launch {
            _isLoading.value = true
            val token = "Bearer ${tokenManager.accessToken.first()}"
            getDisasterHomeUseCase.invoke(token)
                .onSuccess { response->
                    _disasterHome.value = response
                    _isLoading.value = false
                    Log.d("getDisasterHome", "${response}")
                }
                .onFailure {
                    _isLoading.value = true
                    Log.e("getDisasterHome" , "${isLoading.value}")
                }
        }
    }

    private val _disasterHomeDetail = MutableStateFlow(CommunityDisasterDetailResponse())
    val disasterHomeDetail = _disasterHomeDetail.asStateFlow()

    var disasterId:Int=0
    //var likeStatus:Boolean = false
    private val _likeStatus = MutableLiveData<Boolean>()
    val likeStatus: LiveData<Boolean> = _likeStatus

    //재난상황 댓글 모두
    fun getDisasterDetail(sort:String,disasterId:Int){
        viewModelScope.launch {
            _isLoading.value = true
            val token = "Bearer ${tokenManager.accessToken.first()}"
            getCommunityHomeDetailUseCase.invoke(token,sort,disasterId)
                .onSuccess { response->
                    _disasterHomeDetail.value = response
                    _isLoading.value = false
                }
                .onFailure {
                    _isLoading.value = true
                }
        }
    }

    fun postDisasterConversation(body: ConversationRequestBody){
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            postDisasterConversationUseCase(token,body)
                .onSuccess {
                    getDisasterDetail("time", body.disasterId)
                }
        }
    }

    fun conversationLike(conversationId: Int) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            conversationLikeUseCase(token, conversationId)
                .onSuccess {
                }
        }
    }

    fun conversationCancel(conversationId: Int) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            conversationCancelUseCase(token, conversationId)
                .onSuccess {
                }
        }
    }

    fun articleLike(articleId: Int) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            articlelikeUseCase(token, articleId)
                .onSuccess {
                    _articleLike.value = it
                    _likeStatus.postValue(true)
                }
        }
    }
    fun articleCancel(articleId: Int) {
        viewModelScope.launch {
            val token = "Bearer ${tokenManager.accessToken.first()}"
            articlecancelUseCase(token, articleId)
                .onSuccess {
                    _articleLike.value = it
                    _likeStatus.postValue(false)
                }
        }
    }

    //좋아요를 위해 추가함
    fun updateLikeState(conversationId: Int, isLiked: Boolean) {
        val updatedConversations = _disasterHomeDetail.value.conversations.map { conversation ->
            if (conversation.conversationId == conversationId) {
                // 해당 대화의 좋아요 상태를 업데이트합니다.
                conversation.copy(isLiked = isLiked)
            } else {
                conversation
            }
        }
        val updatedDetail = _disasterHomeDetail.value.copy(conversations = updatedConversations)
        _disasterHomeDetail.value = updatedDetail
    }

    val tagText: StateFlow<String> = townDetail
        .map { detail -> tagTextForDetail(detail.articleTag) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )
    @RequiresApi(Build.VERSION_CODES.O)
    val detailTime:StateFlow<String> = townDetail
        .map { detail -> getTimeDifference(detail.createdAt) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )


    private fun tagTextForDetail(articleTag:String):String{
        return when(articleTag){
            "SAFETY" -> "치안"
            "LIFE" -> "일상"
            "TRAFFIC" -> "교통"
            "NONE" -> "기타"
            else->"기타"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeDifference(createdTime: String): String {
        if (createdTime.isBlank()) {
            return "기본값 또는 오류 메시지"
        }

        val formatters = listOf(//2023-11-22 08:21
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        )
        var parsedDateTime : LocalDateTime? = null
        for(formatter in formatters){
            try {
                parsedDateTime = LocalDateTime.parse(createdTime, formatter)
                val currentDateTime = LocalDateTime.now()
                val duration = Duration.between(parsedDateTime, currentDateTime)
                return when {
                    duration.toHours() < 1 -> "${duration.toMinutes()}분 전"
                    duration.toDays() < 1 -> String.format("%02d:%02d",parsedDateTime.hour,parsedDateTime.minute)
                    else -> "${parsedDateTime.monthValue}/${parsedDateTime.dayOfMonth}"
                }
            } catch (e: DateTimeParseException) {
                // 현재 형식으로 파싱 실패; 다음 형식으로 시도
                Log.e("getTimeDifference", "$e")

            }
        }
        return "파싱 오류"
    }
    init{
        _isLoading.value = true
        getTownList()
    }
}

