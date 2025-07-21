package com.sayed.campusdock.UI.Canteen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sayed.campusdock.R
import com.sayed.campusdock.UI.Home.HomeFragment
import com.sayed.campusdock.UI.Profile.ProfileFragment
import com.sayed.campusdock.databinding.CanteenMenuActivityBinding

class CanteenMenuActivity : AppCompatActivity() {


    lateinit var binding: CanteenMenuActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = CanteenMenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)





//        val tabLayout = findViewById<TabLayout>(R.id.menuTabLayout)
//        val categories = listOf("All", "Snacks", "Meals", "Drinks")
//
//        categories.forEach { label ->
//            tabLayout.addTab(tabLayout.newTab().setText(label))
//        }

        val categories = listOf("All", "Snacks", "Meals", "Drinks")
        categories.forEach { label ->
            binding.menuTabLayout.addTab(
                binding.menuTabLayout.newTab().setText(label)
            )

        }




    }





}