package com.sayed.campusdock.UI.Auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.UI.Main.MainActivity
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.databinding.AuthOtpActivityBinding
import kotlinx.coroutines.launch

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: AuthOtpActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthOtpActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")

        binding.btnVerifyOtp.setOnClickListener {
            val enteredOtp = binding.otpView.value
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