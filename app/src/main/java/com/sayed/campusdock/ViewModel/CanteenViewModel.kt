package com.sayed.campusdock.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Canteen
import kotlinx.coroutines.launch

class CanteenViewModel: ViewModel() {
    private val _canteens = MutableLiveData<List<Canteen>>() // Internal mutable data
    val canteens: LiveData<List<Canteen>> = _canteens  // Public read-only version

//    MutableLiveData	Holds the list of canteens — and can change.
//    LiveData	Public read-only access to the canteen list.

    private var isLoaded = false

    fun fetchCanteens() {
        if (isLoaded) return // Avoid re-fetching

        viewModelScope.launch {
            try {
                val result = RetrofitClient.instance.getCanteens()
                _canteens.value = result  // Push data to observers
                isLoaded = true           // Mark it as loaded
            } catch (e: Exception) {
                Log.e("CANTEEN_VM", "Failed to load: ${e.message}")
            }
        }
    }
}