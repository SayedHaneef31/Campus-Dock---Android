package com.sayed.campusdock.Data.Socials

import java.time.LocalDateTime
import java.util.UUID
import com.google.gson.annotations.SerializedName

data class PostResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val imageUrl: String?,
    val authorName: String,
    val authorAnonymousName: String?,
    @SerializedName("userId")
    val authorId: UUID?,
    val topicName: String,
    val isAnonymous: Boolean,
    val createdAt: LocalDateTime?,
    val upvoteCount: Int = 0,
    val downvoteCount: Int = 0,
    val commentCount: Int = 0
)