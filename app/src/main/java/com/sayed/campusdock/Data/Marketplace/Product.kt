package com.sayed.campusdock.Data.Marketplace

data class Product(
    val id: String? = null,
    val name: String,
    val price: String,
    val imageUrl: String?, // Remote image URL from API, or null for placeholder
    val sellerName: String,
    val description: String? = null
)