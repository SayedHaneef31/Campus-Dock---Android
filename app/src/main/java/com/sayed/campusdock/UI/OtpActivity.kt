package com.sayed.campusdock.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sayed.campusdock.MainActivity
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")

        binding.btnVerifyOtp.setOnClickListener {
            val enteredOtp = binding.otpInput.text.toString().trim()

            // TODO: Verify OTP via API call. Simulating success here
            if (enteredOtp == "1234") { // Simulated valid OTP
                // Save token
                val prefs = getSharedPreferences("CampusDockPrefs", MODE_PRIVATE)
                prefs.edit().putString("jwt_token", "dummy_jwt_token_here").apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}