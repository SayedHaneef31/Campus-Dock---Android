package com.sayed.campusdock.ConfigManager

import android.content.Context
import android.util.Log
import org.json.JSONObject
import android.util.Base64

import kotlin.io.encoding.ExperimentalEncodingApi

object TokenManager {

    private var cachedPayload: JSONObject? = null
    private var rawToken: String? = null

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("CampusDockPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (token != null) {
            rawToken = token
            cachedPayload = decodeToken(token)
        }
    }

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("CampusDockPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("jwt_token", token).apply()
        rawToken = token
        cachedPayload = decodeToken(token)
    }

    fun getToken(): String? = rawToken

    fun getCollegeId(): String? = cachedPayload?.optString("collegeId")
    fun getUserId(): String? = cachedPayload?.optString("userId")
    fun getName(): String? = cachedPayload?.optString("name")
    fun getEmail(): String? = cachedPayload?.optString("email")

    private fun decodeToken(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE))
            JSONObject(payloadJson)
        } catch (e: Exception) {
            null
        }
    }

}