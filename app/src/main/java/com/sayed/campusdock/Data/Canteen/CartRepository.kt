package com.sayed.campusdock.Data.Canteen

import com.sayed.campusdock.API.ApiService
import com.sayed.campusdock.Data.Room.CartDao
import com.sayed.campusdock.Data.Room.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(
    private val apiService: ApiService,
    private val cartDao: CartDao
){
    // This Flow will always hold the latest cart data from the local database.
    // The UI will observe this to get real-time updates.
    val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItemsAsFlow()
    val cartTotal: Flow<Double> = cartDao.getCartTotalAsFlow()

    // Function to refresh the local cache from the network
    suspend fun refreshCart(userId: String) {
        try {
            val response = apiService.getFullCart(userId)
            if (response.isSuccessful && response.body() != null) {
                // If network call is successful, clear the local cache
                cartDao.clearCart()
                // And insert the fresh data from the server
                val networkCartItems = response.body()!!.items
                cartDao.insertOrUpdateAll(networkCartItems)
            }
        } catch (e: Exception) {
            // Handle network error, maybe log it
            // The app will continue to function with stale data from Room
        }
    }

    // All actions (add, update, delete) will follow the same pattern:
    // 1. Call the API.
    // 2. If successful, use the response to update the local Room database.
    suspend fun addItem(userId: String, menuItemId: String, quantity: Int) {
        try {
            val response = apiService.addItemToCart(userId, menuItemId, quantity)
            if (response.isSuccessful && response.body() != null) {
                // Server responded with the updated cart, so update our local cache
                cartDao.clearCart()
                cartDao.insertOrUpdateAll(response.body()!!.items)
            } else {
                // Handle API error (e.g., item out of stock)
            }
        } catch (e: Exception) {
            // Handle network error
        }
    }
    suspend fun updateItemQuantity(userId: String, menuItemId: String, quantity: Int) {
        try {
            val response = apiService.updateItemQuantity(userId, menuItemId, quantity)
            if (response.isSuccessful && response.body() != null) {
                // Server responded with the updated cart, so update our local cache
                cartDao.clearCart()
                cartDao.insertOrUpdateAll(response.body()!!.items)
            } else {
                // Handle API error (e.g., item out of stock)
            }
        } catch (e: Exception) {
            // Handle network error
        }
    }

    suspend fun removeItem(userId: String, menuItemId: String) {
        try {
            val response = apiService.removeItemFromCart(userId, menuItemId)
            if (response.isSuccessful && response.body() != null) {
                // Server responded with the updated cart, so update our local cache
                cartDao.clearCart()
                cartDao.insertOrUpdateAll(response.body()!!.items)
            } else {
                // Handle API error (e.g., item out of stock)
            }
        } catch (e: Exception) {
            // Handle network error
        }
    }

}