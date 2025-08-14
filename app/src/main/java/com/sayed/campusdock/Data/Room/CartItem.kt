package com.sayed.campusdock.Data.Room

import androidx.room.Entity
import androidx.room.PrimaryKey


//Step 1
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: String,
    val menuItemId: String,
    val foodName: String,
    val price: Double,
    val quantity: Int,
    val status: String,
    val url: String?    // Made nullable if it can be missing.
)