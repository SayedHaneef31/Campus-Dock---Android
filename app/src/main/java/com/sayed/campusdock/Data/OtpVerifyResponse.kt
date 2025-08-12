package com.sayed.campusdock.Data

data class OtpVerifyResponse(
    val success: Boolean,
    val message: String,
    val token: String
)
