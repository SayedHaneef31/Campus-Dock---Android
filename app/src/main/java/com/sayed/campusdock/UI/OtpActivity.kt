package com.sayed.campusdock.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.MainActivity
import com.sayed.campusdock.R
import com.sayed.campusdock.RetrofitClient
import com.sayed.campusdock.databinding.ActivityOtpBinding
import kotlinx.coroutines.launch

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")

        binding.btnVerifyOtp.setOnClickListener {
            val enteredOtp = binding.otpInput.text.toString().trim()
            val email = intent.getStringExtra("email") ?: ""

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.verifyOTP(email, enteredOtp)
                    if (response.isSuccessful && response.body()?.success == true) {
                        val prefs = getSharedPreferences("CampusDockPrefs", MODE_PRIVATE)
                        prefs.edit().putString("jwt_token", "dummy_jwt_token_here").apply()
                        Toast.makeText(this@OtpActivity, "OTP Verified!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@OtpActivity, MainActivity::class.java))
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Invalid OTP"
                        Toast.makeText(this@OtpActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@OtpActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}