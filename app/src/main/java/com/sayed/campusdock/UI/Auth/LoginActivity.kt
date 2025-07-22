package com.sayed.campusdock.UI.Auth

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.Data.CollegeSpinner
import com.sayed.campusdock.Data.CreateUser
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.databinding.AuthLoginActivityBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: AuthLoginActivityBinding
    private lateinit var collegeList: List<CollegeSpinner>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AuthLoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchCollegesAndPopulateSpinner()

        binding.btnSendOtp.setOnClickListener {
            sendOTP()
        }

    }

    private fun fetchCollegesAndPopulateSpinner() {
        lifecycleScope.launch {
            try {
                collegeList = RetrofitClient.instance.getCollegesName()

                val collegeNames = mutableListOf("Select your college") + collegeList.map { it.name }

                val adapter = object : ArrayAdapter<String>(
                    this@LoginActivity,
                    android.R.layout.simple_spinner_item,
                    collegeNames
                ) {
                    override fun isEnabled(position: Int): Boolean {
                        return position != 0 // disable the hint item
                    }

                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getDropDownView(position, convertView, parent) as TextView
                        view.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                        return view
                    }
                }
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.collegeSpinner.adapter = adapter

                binding.emailInput.isEnabled = false

                binding.collegeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        // Enable email input only if valid college is selected
                        binding.emailInput.isEnabled = position != 0
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

            } catch (e: Exception) {
                Log.e("COLLEGE_FETCH_ERROR", "Failed to fetch colleges: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "Failed to load colleges", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun sendOTP() {
        val selectedIndex = binding.collegeSpinner.selectedItemPosition
        val selectedCollege = collegeList[selectedIndex-1]
        //since the hint item shifts all indexes by 1.
        val selectedCollegeDomain = selectedCollege.domain
        val email = binding.emailInput.text.toString().trim()

        if (selectedIndex == 0) {
            Toast.makeText(this, "Please select a valid college", Toast.LENGTH_SHORT).show()
        }
        if(email.isEmpty())
            Toast.makeText(this@LoginActivity, "Please enter the valid email", Toast.LENGTH_SHORT).show()

        if (email.endsWith(selectedCollegeDomain))
        {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.sendOTP(CreateUser(email))
                    Log.d("SEND_OTP", "Response code: ${response.code()}, body: ${response.body()}")

                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "OTP sent successfully", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@LoginActivity, OtpActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Failed to send OTP, Retry in 10 seconds", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("SEND_OTP_ERROR", "Exception: ${e.message}", e)
                    Toast.makeText(this@LoginActivity, "Error in sending OTP: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

        }
        else {
            Toast.makeText(this, "Please fill in the correct college email", Toast.LENGTH_SHORT).show()
        }
    }
}




