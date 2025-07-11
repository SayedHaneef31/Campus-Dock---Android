package com.sayed.campusdock.UI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sayed.campusdock.MainActivity
import com.sayed.campusdock.R


//This is the new launcher that decides:
// 1. If user should go to login screen, or
// 2. Directly open MainActivity
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val prefs = getSharedPreferences("CampusDockPrefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        val intent = if (token.isNullOrEmpty()) {
            Intent(this, LoginActivity::class.java) // You'll create this
        } else {
            Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}