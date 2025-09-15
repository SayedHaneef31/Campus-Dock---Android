package com.sayed.campusdock.Data.Auth

data class OtpVerifyResponse(
    val success: Boolean,
    val message: String,
    val token: String
)