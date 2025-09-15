package com.sayed.campusdock.Data.Marketplace

data class Product(
    val name: String,
    val price: String,
    val imageUrl: Int, // Using a drawable resource ID for mock data
    val sellerName: String
)