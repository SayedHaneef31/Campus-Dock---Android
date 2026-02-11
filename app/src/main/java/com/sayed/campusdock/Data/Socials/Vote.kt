package com.sayed.campusdock.Data.Socials

import java.util.UUID

data class Vote(
    val id: UUID,
    val postId: UUID,
    val userId: UUID,
    val voteType: VoteType
)
