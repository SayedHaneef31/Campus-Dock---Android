package com.sayed.campusdock.WorkManager

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Room.AppDatabase
import com.sayed.campusdock.Data.Room.AppDatabaseBuilder

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
            // 3. Fetch all cart items currently stored in the local Room database.
            val cartItems = db.cartDao().getAllCartItems()

            // 4. (Optional but good practice) If the cart is empty, there's nothing to sync.
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

//    override suspend fun doWork(): Result {
//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "app_db"
//        ).build()
//
//        val cartItems = db.cartDao().getAllCartItems()
//
//        // Call backend API here with cartItems
//        val api = RetrofitClient.instance
//        try {
//            api.updateCart(cartItems) // Your API endpoint
//            return Result.success()
//        } catch (e: Exception) {
//            return Result.retry()
//        }
//    }

}