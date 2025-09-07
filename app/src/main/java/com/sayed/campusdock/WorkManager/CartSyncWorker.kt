package com.sayed.campusdock.WorkManager

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Room.AppDatabase
import com.sayed.campusdock.Data.Room.AppDatabaseBuilder

// Its main job is to reliably synchronize the user's local shopping cart, stored on the phone, with the main database on your server.
class CartSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        // This is the public, constant key we will use everywhere.
        const val KEY_USER_ID = "KEY_USER_ID"
    }


    override suspend fun doWork(): Result {

        // 1. Get the userId passed from the ViewModel. If it's null or empty, we can't proceed.
        val userId = inputData.getString(KEY_USER_ID)
        if (userId.isNullOrEmpty()) {
            Log.e("CartSyncWorker", "Cannot sync cart, userId is missing.")
            return Result.failure() // Fail permanently if there's no user.
        }

        // 2. Get the singleton instances for the database and API client.
        val db = AppDatabaseBuilder.getInstance(applicationContext)
        val api = RetrofitClient.instance

        return try {
            // Fetching Local Data: The worker reads all the items currently saved in the local cart_items table from the phone's database.
            val cartItems = db.cartDao().getAllCartItems()

            // 4.  If the cart is empty, there's nothing to sync.
            if (cartItems.isEmpty()) {
                Log.d("CartSyncWorker", "Local cart is empty. Sync not needed.")
                return Result.success()
            }

            Log.d("CartSyncWorker", "Attempting to sync ${cartItems.size} items for user: $userId")

            // 5. Call the correct API endpoint with both the userId and the list of cart items.
            val response = api.syncCart(userId, cartItems)

            // 6. Check the server's response.
            if (response.isSuccessful) {
                Log.d("CartSyncWorker", "Sync successful!")
                Result.success() // The job finished successfully.
            } else {
                Log.w("CartSyncWorker", "Sync failed with server error: ${response.code()}. Retrying...")
                Result.retry() // The server had an issue, so we should try again later.
            }
        } catch (e: Exception) {
            // This catches network errors (like no internet) or other exceptions.
            Log.e("CartSyncWorker", "Error during sync", e)
            Result.retry() // An external issue occurred, so we should try again later.
        }
    }

}