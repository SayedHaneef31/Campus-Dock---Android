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
import com.sayed.campusdock.Data.Canteen.MenuItem
import com.sayed.campusdock.Data.Room.AppDatabaseBuilder
import com.sayed.campusdock.Data.Room.CartItem
import com.sayed.campusdock.WorkManager.CartSyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**  Will be getting thw menu items of a particular canteen
 *
 * This defines the CanteenMenuViewModel class.
 * It inherits from AndroidViewModel instead of the regular ViewModel.
 * This is a deliberate choice because AndroidViewModel gives you access
 * to the application Context, which is necessary for creating the database instance,
 * accessing SharedPreferences, and initializing WorkManager.
 *
 */
class CanteenMenuViewModel (application: Application) : AndroidViewModel(application) {

    // Get an instance of the DAO from your singleton database builder
    private val cartDao = AppDatabaseBuilder.getInstance(application).cartDao()
    private val apiService = RetrofitClient.instance

    // --- State for Menu Items ---
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())       //Private state for the list of menu items
    val menuItems: StateFlow<List<MenuItem>> = _menuItems     //Public, exposes the menu items list to the Fragment. The Fragment can read and observe this,
                                                              // but it cannot change it, ensuring a clean, one-way data flow.

    // --- State for Cart ---
    // It holds the cart data as a Map, where the key is the menuItemId and the value is the CartItem
    // object. This makes it very fast to look up the quantity of a specific item in the UI.
    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems

    // --- State for Errors ---
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error    //The Fragment observes this to show Toast messages to the user.

    /**
     * This block of code runs once automatically when the CanteenMenuViewModel is first created.
     * It starts a coroutine to continuously observe the database for any changes.
     */
    init {
        viewModelScope.launch {
            // This is the heart of the reactive cart. It "collects" or listens to the Flow from the Room database.
            // Whenever the cart data changes in the database,
            // this block of code automatically runs again with the fresh list of items.
            cartDao.getAllCartItemsAsFlow().collect { items ->
                _cartItems.value = items.associateBy { it.menuItemId }
                // It takes the new list of items from the database and updates the _cartItems state,
                // which in turn automatically updates the UI.
                // The associateBy function efficiently converts the list to a map for fast lookups.
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
     * This is called when the user taps the "Add" button.
     * Its only job is to create a CartItem object and tell the cartDao
     * to immediately save it to the local Room database.
     * This action is what triggers the init block's collector to run again,
     * updating the UI instantly.
     */
    fun onAddItemClicked(item: MenuItem) {
        viewModelScope.launch {
            // 1. Instantly update the local database
            cartDao.insertOrUpdate(
                CartItem(
                    id = item.id,
                    menuItemId = item.id,
                    foodName = item.foodName,
                    price = item.price,
                    quantity = 1,
                    status = "ACTIVE",
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