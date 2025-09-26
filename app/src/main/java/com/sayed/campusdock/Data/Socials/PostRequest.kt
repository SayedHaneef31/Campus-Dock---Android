package com.sayed.campusdock.Data.Socials

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class PostRequest(
    val title: String,
    val content: String?,
    val imageUrl: String?,
    val authorId: UUID,
    val topicId: UUID,
    val collegeId: UUID,
    @get:JsonProperty("isAnonymous")
    val isAnonymous: Boolean,
    @get:JsonProperty("isPoll")
    val isPoll: Boolean
)

