package com.daepiro.numberoneproject.data.model

data class ConversationModel(
    val childs: List<ChildModel>,
    val content: String,
    val conversationId: Int,
    val info: String,
    val isEditable: Boolean,
    var isLiked: Boolean,
    var like: Int
)