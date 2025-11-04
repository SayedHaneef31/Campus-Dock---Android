package com.sayed.campusdock.UI.Main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.sayed.campusdock.R
import com.sayed.campusdock.ViewModel.CanteenCartViewModel
import com.sayed.campusdock.UI.Canteen.CartFragment
import com.sayed.campusdock.UI.Canteen.OrdersFragment
import com.sayed.campusdock.UI.Home.HomeFragment
import com.sayed.campusdock.UI.Profile.ProfileFragment

import com.sayed.campusdock.databinding.MainActivityBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val cartViewModel: CanteenCartViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

        // Attach/update badge on the Cart tab
        val cartBadge = binding.bottomNav.getOrCreateBadge(R.id.cartFragment)
        cartBadge.isVisible = false

        // Handle system bars insets so content doesn't overlap status/navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Push content below status bar and above bottom nav/gesture bar
            binding.navHostFragment.setPadding(0, sysBars.top, 0, sysBars.bottom)
            // Lift bottom nav above gesture nav area
            binding.bottomNav.setPadding(
                binding.bottomNav.paddingLeft,
                binding.bottomNav.paddingTop,
                binding.bottomNav.paddingRight,
                sysBars.bottom
            )
            insets
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartItems.collect { items ->
                    val count = items.sumOf { it.quantity }
                    if (count > 0) {
                        cartBadge.isVisible = true
                        cartBadge.number = count
                    } else {
                        cartBadge.clearNumber()
                        cartBadge.isVisible = false
                    }
                }
            }
        }
    }

}