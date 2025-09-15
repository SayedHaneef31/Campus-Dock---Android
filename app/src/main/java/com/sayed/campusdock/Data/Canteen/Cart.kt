package com.sayed.campusdock.Data.Canteen

import com.sayed.campusdock.Data.Room.CartItem
import java.util.UUID

data class Cart(
    val cartId: UUID,
    val canteenId: UUID,
    val canteenName: String,
    val items: List<CartItem>,
    val totalAmount: Double
)