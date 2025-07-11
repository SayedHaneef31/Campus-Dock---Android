package com.sayed.campusdock.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSendOtp.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()

            if (email.endsWith("@abes.ac.in")) {
                // Send OTP using Retrofit/Volley or your logic
                // simulate successful OTP send
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a valid college email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}