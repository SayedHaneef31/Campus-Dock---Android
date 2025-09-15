package com.sayed.campusdock.Data.Socials

import java.time.LocalDateTime
import java.util.UUID

data class Post(
    val id: UUID,
    val title: String,
    val content: String?,
    val imageUrl: String?,
    val authorUsername: String,
    val authorId: UUID,
    val topicName: String,
    val isAnonymous: Boolean,
    val upvoteCount: Int,
    val downvoteCount: Int,
    val commentCount: Int,
    val createdAt: LocalDateTime
)