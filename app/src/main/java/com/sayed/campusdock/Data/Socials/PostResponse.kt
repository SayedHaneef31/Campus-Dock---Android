package com.sayed.campusdock.Data.Socials

import java.time.LocalDateTime
import java.util.UUID

data class PostResponse(
    val id: UUID,
    val title: String,
    val content: String?,
    val imageUrl: String?,
    val authorName: String,
    val authorAnonymousName: String?,
    val authorId: UUID,
    val topicName: String,
    val isAnonymous: Boolean,
    val createdAt: LocalDateTime,
    val upvoteCount: Int,
    val downvoteCount: Int,
    val commentCount: Int
)