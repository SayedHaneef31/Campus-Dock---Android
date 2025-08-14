package com.sayed.campusdock.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Canteen
import kotlinx.coroutines.launch
import org.json.JSONObject
import android.util.Base64

class CanteenViewModel: ViewModel() {
    private val _canteens = MutableLiveData<List<Canteen>>() // Internal mutable data
    val canteens: LiveData<List<Canteen>> = _canteens  // Public read-only version

//    MutableLiveData	Holds the list of canteens — and can change.
//    LiveData	Public read-only access to the canteen list.

    private var isLoaded = false

    fun getCollegeIdFromToken(token: String): String? {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return null // invalid token

            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val payload = JSONObject(payloadJson)

            return payload.getString("collegeId")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun fetchCanteens(token: String) {
        if (isLoaded) return // Avoid re-fetching

        val collegeId = getCollegeIdFromToken(token)
        if (collegeId.isNullOrEmpty()) {
            Log.e("CANTEEN_VM", "No collegeId found in token")
            return
        }

        viewModelScope.launch {
            try {
                val result = RetrofitClient.instance.getCanteens(collegeId) // pass collegeId
                _canteens.value = result
                isLoaded = true
                Log.d("CANTEEN_VM", "Loaded ${result.size} canteens for $collegeId")
            } catch (e: Exception) {
                Log.e("CANTEEN_VM", "Failed to load canteens: ${e.message}")
            }
        }
    }
}