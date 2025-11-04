package com.sayed.campusdock.Data.Auth

import java.util.UUID

data class User(
    val id: UUID,
    val name: String,
    val email: String,
    val anonymousName: String?
)


