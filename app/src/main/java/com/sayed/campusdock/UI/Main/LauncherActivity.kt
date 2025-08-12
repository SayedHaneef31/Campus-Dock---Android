package com.sayed.campusdock.UI.Main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sayed.campusdock.UI.Main.MainActivity
import com.sayed.campusdock.UI.Auth.LoginActivity
import org.json.JSONObject
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

//This is the new launcher that decides:
// 1. If user should go to login screen, or
// 2. Directly open MainActivity
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val prefs = getSharedPreferences("CampusDockPrefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        Log.d("TOKEN_DEBUG", "Token is: $token")
        val intent = if (token.isNullOrEmpty() || isJwtExpired(token)) {
            Intent(this, LoginActivity::class.java) // You'll create this
        } else {
            Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    fun isJwtExpired(token: String): Boolean {
        Log.d("JWT_DEBUG", "Inside isJwtExpired method with Token: $token")
        return try {
            // ... same code as before
            val parts = token.split(".")
            if (parts.size != 3) return true

            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val payload = String(payloadBytes, Charsets.UTF_8)

            val json = JSONObject(payload)
            val exp = json.getLong("exp") // This could fail if 'exp' is not a long

            Log.d("JWT_DEBUG", "Payload: $payload")

            val now = System.currentTimeMillis() / 1000
            Log.d("JWT_DEBUG", "exp: $exp, now: $now, expired: ${now > exp}")

            now > exp
        } catch (e: Exception) {
            // THIS IS THE IMPORTANT CHANGE
            Log.e("JWT_ERROR", "Failed to parse JWT, treating as expired.", e)
            true // Treat as expired if anything fails
        }
    }



}