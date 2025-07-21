package com.sayed.campusdock.UI.Main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.sayed.campusdock.R
import com.sayed.campusdock.UI.Canteen.CartFragment
import com.sayed.campusdock.UI.Canteen.OrdersFragment
import com.sayed.campusdock.UI.Home.HomeFragment
import com.sayed.campusdock.UI.Profile.ProfileFragment

import com.sayed.campusdock.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the NavController from the NavHostFragment
        //supportFragmentManager → Gives access to the fragment manager of this activity.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //as NavHostFragment → We are casting the generic Fragment we found into a NavHostFragment,
        // which is a special type of fragment provided by the Navigation Component.

        //navController is the central API in Navigation Component.
        //Manages app navigation within a NavHost.
        //We'll use this navController to tell Android which destinations (fragments) to show when the user taps an item on the bottom nav bar.
        val navController = navHostFragment.navController

        // Setup BottomNavigationView with NavController
        //This is the glue that connects your UI (BottomNavigationView) with your Navigation logic (NavGraph + NavController).
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
    }

}