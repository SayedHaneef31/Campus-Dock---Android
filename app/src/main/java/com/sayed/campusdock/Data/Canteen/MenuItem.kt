package com.sayed.campusdock.Data.Canteen

data class MenuItem(
    val id: String,
    val foodName: String,
    val price: Double,
    val url: String,
    val isAvailable : Boolean
)