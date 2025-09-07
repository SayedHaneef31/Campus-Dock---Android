package com.sayed.campusdock.Data.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

//Step 2

@Dao
interface CartDao {

    // Insert or update item (replace if productId exists)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cartItem: CartItem)

    // Insert multiple items at once
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(cartItems: List<CartItem>)

    // Get all items in the cart
    @Query("SELECT * FROM cart_items")
    suspend fun getAllCartItems(): List<CartItem>

    // Get single cart item by productId
    @Query("SELECT * FROM cart_items WHERE id = :id LIMIT 1")
    suspend fun getCartItemById(id: String): CartItem?

    // Update quantity for a specific product
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: String, quantity: Int)

    // Delete specific item
    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    // Delete item by productId
    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: String)

    // Clear entire cart
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Get total price of the cart
    @Query("SELECT SUM(price * quantity) FROM cart_items")
    suspend fun getCartTotal(): Double?

    // FIX: Change your getter functions to return Flow
    @Query("SELECT * FROM cart_items")
    fun getAllCartItemsAsFlow(): Flow<List<CartItem>>

    // A Flow is a modern Kotlin feature that automatically emits the latest list
    // of cart items whenever the data changes in the table. This is the magic that
    // makes your UI update reactively.
    @Query("SELECT COALESCE(SUM(price * quantity), 0.0) FROM cart_items")
    fun getCartTotalAsFlow(): Flow<Double>
}