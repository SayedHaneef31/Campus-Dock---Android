package com.sayed.campusdock.Data.Marketplace

import java.util.UUID

data class ProductOwnerProfileDto(
    val id: UUID,
    val name: String,
    val anonymousName: String,
    val profilePicUrl: String?
)

data class Product(
    val id: String? = null,
    val name: String,
    val price: String,
    val imageUrl: String?, // Remote image URL from API, or null for placeholder
    val sellerName: String,
    val ownerProfile: ProductOwnerProfileDto? = null,
    val description: String? = null
)