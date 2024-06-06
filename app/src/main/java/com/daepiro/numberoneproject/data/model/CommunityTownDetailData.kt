package com.daepiro.numberoneproject.data.model

data class CommunityTownDetailData(
    val address: String="",
    val articleId: Int =0,
    val articleTag:String="",
    val content: String="",
    var createdAt: String="",
    val imageUrls: List<String>? = null,
    val likeCount: Int=0,
    val ownerName: String="",
    val ownerNickName: String="",
    val modifiedAt: String="",
    val ownerMemberId: Int=0,
    val ownerProfileImageUrl : String="",
    val thumbNailImageUrl: String = "",
    val isLiked : Boolean = true,
    val title: String="",
    val regionLv2: String = "",
    val commentCount: Int = 0
)
