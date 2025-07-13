package com.sayed.campusdock.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.Data.CollegeSpinner
import com.sayed.campusdock.Data.CreateUser
import com.sayed.campusdock.R
import com.sayed.campusdock.RetrofitClient
import com.sayed.campusdock.databinding.ActivityLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var collegeList: List<CollegeSpinner>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchCollegesAndPopulateSpinner()

        binding.btnSendOtp.setOnClickListener {
            val selectedIndex = binding.collegeSpinner.selectedItemPosition
            val selectedCollege = collegeList[selectedIndex]
            val selectedCollegeDomain = selectedCollege.domain
            val email = binding.emailInput.text.toString().trim()

            if (email.endsWith("m")) {
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

            } else {
                Toast.makeText(this, "Please select the college from the spinner", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun fetchCollegesAndPopulateSpinner() {
//       lifecycleScope.launch {
//           try {
//               collegeList = RetrofitClient.instance.getCollegesName()
//               val collegeNames = collegeList.map { it.name }
//
//               val adapter = ArrayAdapter(
//                   this@LoginActivity, // or requireContext() in Fragment
//                   android.R.layout.simple_spinner_item,
//                   collegeNames
//               )
//               adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//               binding.collegeSpinner.adapter = adapter
//           }catch (e: Exception){
//               Toast.makeText(this@LoginActivity, "Failed to load colleges", Toast.LENGTH_SHORT).show()
//           }
//
//       }
//
//
//
//    }
private fun fetchCollegesAndPopulateSpinner() {
    lifecycleScope.launch {
        try {
            Log.d("COLLEGE_FETCH", "Fetching college list from backend...")

            collegeList = RetrofitClient.instance.getCollegesName()
            Log.d("COLLEGE_FETCH", "College list fetched: $collegeList")

            val collegeNames = collegeList.map { it.name }
            Log.d("COLLEGE_FETCH", "Extracted college names: $collegeNames")

            val adapter = ArrayAdapter(
                this@LoginActivity,
                android.R.layout.simple_spinner_item,
                collegeNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.collegeSpinner.adapter = adapter

            Log.d("COLLEGE_FETCH", "Spinner populated successfully")

        } catch (e: Exception) {
            Log.e("COLLEGE_FETCH_ERROR", "Failed to fetch colleges: ${e.message}", e)
            Toast.makeText(this@LoginActivity, "Failed to load colleges", Toast.LENGTH_SHORT).show()
        }
    }
}

}