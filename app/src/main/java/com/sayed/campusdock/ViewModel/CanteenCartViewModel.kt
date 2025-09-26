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
import com.sayed.campusdock.Data.Room.AppDatabaseBuilder
import com.sayed.campusdock.Data.Room.CartItem
import com.sayed.campusdock.ConfigManager.CartSyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class CanteenCartViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "CanteenCartVM"

    private val cartDao = AppDatabaseBuilder.getInstance(application).cartDao()

    val cartItems: StateFlow<List<CartItem>> = cartDao.getAllCartItemsAsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val cartTotal: StateFlow<Double> = cartDao.getCartTotalAsFlow()
        .stateIn(
            scope = viewModelScope,
            // Keep the data stream alive for 5s after the UI stops listening
            started = SharingStarted.WhileSubscribed(5000),
            // The initial value before the first database emission
            initialValue = 0.0
        )


        // A StateFlow to communicate the status of the place order action to the Fragment
        private val _placeOrderStatus = MutableStateFlow<PlaceOrderStatus>(PlaceOrderStatus.Idle)
        val placeOrderStatus: StateFlow<PlaceOrderStatus> = _placeOrderStatus


    fun placeOrder() {
        // Setting the state to Loading so the UI can show a progress bar
        _placeOrderStatus.value = PlaceOrderStatus.Loading

        viewModelScope.launch {
            try {
                val prefs = getApplication<Application>().getSharedPreferences("CampusDockPrefs", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt_token", null)
                val userId = token?.let { getUserIdFromToken(it) }

                if (userId.isNullOrEmpty()) {
                    _placeOrderStatus.value = PlaceOrderStatus.Error("User not found. Please log in again.")
                    return@launch
                }

                // Get the most current list of items directly from the database
                val cartItems = cartDao.getAllCartItems()

                if (cartItems.isEmpty()) {
                    _placeOrderStatus.value = PlaceOrderStatus.Error("Your cart is empty.")
                    return@launch
                }

                // Call the API directly for an immediate sync
                val response = RetrofitClient.instance.syncCart(userId, cartItems)

                if (response.isSuccessful) {
                    Log.d(TAG, "Final sync successful before placing order.")
                    _placeOrderStatus.value = PlaceOrderStatus.Success
                } else {
                    Log.w(TAG, "Final sync failed with server error: ${response.code()}")
                    _placeOrderStatus.value = PlaceOrderStatus.Error("Failed to confirm order with server.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during final sync", e)
                _placeOrderStatus.value = PlaceOrderStatus.Error("An error occurred. Please check your connection.")
            }
        }
    }

    //  A function for the Fragment to call after it has handled the result
    fun onOrderPlacedHandled() {
        _placeOrderStatus.value = PlaceOrderStatus.Idle
    }
    /**
     * Called when the user changes the quantity of an item from the cart screen.
     */
    fun onUpdateQuantity(itemId: String, change: Int) {
        Log.d(TAG, "onUpdateQuantity called for itemId=$itemId, change=$change")
        viewModelScope.launch {
            // Find the item first to get its current quantity
            val existingItem = cartDao.getCartItemById(itemId)
            if (existingItem == null) {
                Log.w(TAG, "Item with id=$itemId not found in DB")
                return@launch
            }
            val newQuantity = existingItem.quantity + change
            Log.d(TAG, "Existing quantity=${existingItem.quantity}, newQuantity=$newQuantity")

            if (newQuantity > 0) {
                cartDao.updateQuantity(itemId, newQuantity)
                Log.i(TAG, "Updated quantity for itemId=$itemId to $newQuantity")
            } else {
                cartDao.deleteCartItemById(itemId)
                Log.i(TAG, "Deleted itemId=$itemId from cart (quantity dropped to 0)")
            }
            // You can optionally schedule a sync here as well
            scheduleSync()
        }
    }

    fun onRemoveItem(itemId: String) {
        Log.d(TAG, "onRemoveItem called for itemId=$itemId")
        viewModelScope.launch {
            cartDao.deleteCartItemById(itemId)
            Log.i(TAG, "Deleted itemId=$itemId from cart")
            // You can optionally schedule a sync here
            scheduleSync()
        }
    }

    /**
     * This function, called by the Fragment's onStop() method, handles the background sync.
     * Configures and enqueues a unique WorkManager job to sync the cart with the backend.
     */
    fun scheduleSync() {

        val prefs = getApplication<Application>().getSharedPreferences("CampusDockPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        val userId = token?.let { getUserIdFromToken(it) }

        if (userId.isNullOrEmpty()) {
            Log.e("CanteenMenuViewModel", "Cannot schedule sync, userId is null.")
            return
        }

        // It packs the userId into a data bundle to pass to the background worker.
        val workData = workDataOf(CartSyncWorker.KEY_USER_ID to userId)

        /**    It builds a request to run the CartSyncWorker job once.
         *     CartSyncWorker is a custom Worker Class that defines what the task does....like syncing cart data to server here.
         *   1. .setInitialDelay(5, TimeUnit.SECONDS) ==== It tells WorkManager to wait 5 seconds after the last change before starting the sync.
         *       This is a "debounce" mechanism.
         *   2.  .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS) ====  - If the work fails, WorkManager will retry it.
         *   Each retry will wait 10 sec longer than the previos one's
         */
        val workRequest = OneTimeWorkRequestBuilder<CartSyncWorker>()
            .setInputData(workData)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()

        // It hands the job to the Android system.
        // "cart_sync_work": A unique name for the job.
        //ExistingWorkPolicy.REPLACE: This tells WorkManager that if a sync job with this name is already scheduled and waiting,
        // cancel it and replace it with this new one.
        // This is extremely efficient, preventing the app from queuing up multiple syncs
        // if the user taps buttons many times in a row.
        WorkManager.getInstance(getApplication()).enqueueUniqueWork(
            "cart_sync_work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Log.d("CanteenMenuViewModel", "Cart sync scheduled for user: $userId")
    }

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

sealed class PlaceOrderStatus {
    object Idle : PlaceOrderStatus() // The initial state
    object Loading : PlaceOrderStatus() // When the sync is in progress
    object Success : PlaceOrderStatus() // When the sync is successful
    data class Error(val message: String) : PlaceOrderStatus() // When something goes wrong
}