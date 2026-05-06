package com.sayed.campusdock.Data.Marketplace

import com.google.gson.annotations.SerializedName

data class ProductDto(
    val name: String,
    val description: String,
    val price: Double,
    val listedOn: String,
    val userName: String,
    val userId: String,
    val urls: List<String>?
)

data class ProductDetailDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val listedOn: String,
    @SerializedName("isServie") val isService: Boolean,
    val userName: String,
    val mediaFiles: List<MediaDetailsDto>?
)

data class MediaDetailsDto(
    val mediaId: String,
    val url: String
)

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val empty: Boolean,
    val last: Boolean,
    val first: Boolean
)
