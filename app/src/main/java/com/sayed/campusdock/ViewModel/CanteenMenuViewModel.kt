package com.sayed.campusdock.ViewModel
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.MenuItem
import com.sayed.campusdock.Data.Room.AppDatabaseBuilder
import com.sayed.campusdock.Data.Room.CartItem
import com.sayed.campusdock.WorkManager.CartSyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CanteenMenuViewModel (application: Application) : AndroidViewModel(application) {

    // Get an instance of the DAO from your singleton database builder
    private val cartDao = AppDatabaseBuilder.getInstance(application).cartDao()
    private val apiService = RetrofitClient.instance

    // --- State for Menu Items ---
    //Private state for the list of menu items
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    //Public, read-only state for the Fragment to observe
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    // --- State for Cart ---
    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems

    // --- State for Errors ---
    // Private state for holding error messages
    private val _error = MutableStateFlow("")
    // Public, read-only state for the Fragment to observe for errors
    val error: StateFlow<String> = _error

    /**
     * The init block is called when the ViewModel is first created.
     * It starts a coroutine to continuously observe the database for any changes.
     */
    init {
        viewModelScope.launch {
            // Whenever the cart_items table changes in Room, this flow will emit the new list
            cartDao.getAllCartItemsAsFlow().collect { items ->
                // We convert the list to a map for easy lookup of items by their ID
                _cartItems.value = items.associateBy { it.menuItemId }
            }
        }
    }

    // Function to fetch menu items for a specific canteen
    fun fetchMenuItems(canteenId: String) {
        viewModelScope.launch {
            try {
                val result = apiService.getMenuItems(canteenId)
                _menuItems.value = result // Update the menu items state on success
            } catch (e: Exception) {
                Log.e("CanteenMenuViewModel", "Failed to load menu", e)
                _error.value = "Failed to load menu. Please check your connection." // Update the error state on failure
            }
        }
    }

    /**
     * Called by the Fragment when the user clicks the initial "Add" button.
     */
    fun onAddItemClicked(item: MenuItem) {
        viewModelScope.launch {
            // 1. Instantly update the local database
            cartDao.insertOrUpdate(
                CartItem(
                    id = item.id,
                    menuItemId = item.id, // Assuming menuItemId is the same as the item's primary ID
                    foodName = item.foodName,
                    price = item.price,
                    quantity = 1, // Start with quantity 1
                    status = "ACTIVE", // Default status
                    url = item.url
                )
            )
        }
    }

    /**
     * Called by the Fragment when the user clicks the "+" or "-" buttons.
     * @param itemId The ID of the menu item to update.
     * @param change The amount to change the quantity by (+1 or -1).
     */
    fun onUpdateQuantity(itemId: String, change: Int) {
        viewModelScope.launch {
            val existingItem = _cartItems.value[itemId] ?: return@launch
            val newQuantity = existingItem.quantity + change

            if (newQuantity > 0) {
                // If the new quantity is positive, update the item's quantity in the database
                cartDao.updateQuantity(itemId, newQuantity)
            } else {
                // If the quantity drops to 0 or less, remove the item from the database
                cartDao.deleteCartItemById(itemId)
            }

        }
    }

    /**
     * Configures and enqueues a unique WorkManager job to sync the cart with the backend.
     */
    fun scheduleSync() {
        // IMPORTANT: You must have the user's ID saved somewhere accessible, like SharedPreferences.
        val prefs = getApplication<Application>().getSharedPreferences("CampusDockPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        val userId = token?.let { getUserIdFromToken(it) }

        if (userId.isNullOrEmpty()) {
            Log.e("CanteenMenuViewModel", "Cannot schedule sync, userId is null.")
            return
        }

        // Pass the userId to the worker
        val workData = workDataOf(CartSyncWorker.KEY_USER_ID to userId)

        val workRequest = OneTimeWorkRequestBuilder<CartSyncWorker>()
            .setInputData(workData)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()

        // Enqueue the work as a unique job, replacing any pending syncs.
        // This prevents spamming the sync worker if the user makes many changes quickly.
        WorkManager.getInstance(getApplication()).enqueueUniqueWork(
            "cart_sync_work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Log.d("CanteenMenuViewModel", "Cart sync scheduled for user: $userId")
    }

    /**
     * A helper function to parse the user ID from the JWT token.
     * This should match the claim name you set in your backend.
     */
    private fun getUserIdFromToken(token: String): String? {
        // TODO----- more robust JWT parsing library in a real app,
        // but this manual parsing works for this case.
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payloadJson = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
            val payload = org.json.JSONObject(payloadJson)
            payload.getString("userId") // Ensure the claim is named "userId" in your backend
        } catch (e: Exception) {
            Log.e("CanteenMenuViewModel", "Failed to parse token", e)
            null
        }
    }
}