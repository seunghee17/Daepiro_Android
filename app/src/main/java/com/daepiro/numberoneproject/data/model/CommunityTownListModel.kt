package com.daepiro.numberoneproject.data.model


data class CommunityTownListModel(
    val content: List<Content> = listOf(),
    val empty: Boolean = true,
    val first: Boolean = true,
    val last: Boolean = true,
    val number: Int = 0,
    val numberOfElements: Int =0,
    val pageable: Pageabled = Pageabled(),
    val size: Int = 0,
    val sort: List<String> = listOf()
)

data class Content(
    val address: String = "",
    val articleLikeCount: Int = 0,
    val articleStatus: String = "",
    val commentCount: Int = 0,
    val content: String = "",
    val createdAt: String = "",
    val id: Int = 0,
    val isLiked: Boolean = true,
    val ownerId: Int = 0,
    val ownerNickName: String = "",
    val tag: String = "",
    val thumbNailImageId: Int = 0,
    val thumbNailImageUrl: String = "",
    val title: String = ""
)

data class Pageabled(
    val offset: Int = 0,
    val pageNumber: Int = 0,
    val pageSize: Int = 0,
    val paged: Boolean = true,
    val sort: List<String> = listOf(),
    val unpaged: Boolean = true
)

