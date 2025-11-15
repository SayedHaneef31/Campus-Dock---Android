package com.sayed.campusdock.UI.Main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sayed.campusdock.Adapter.DeveloperAdapter
import com.sayed.campusdock.Data.Developer
import com.sayed.campusdock.R
import com.sayed.campusdock.ViewModel.CanteenCartViewModel
import com.sayed.campusdock.ConfigManager.TokenManager

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

        // Ensure TokenManager has the token ready in case user arrived directly here
        TokenManager.init(applicationContext)

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

        // Ensure reselecting a tab pops back to that section's root screen
        binding.bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> popToRoot(navController, R.id.homeFragment)
                R.id.social_nav_graph -> popToRoot(navController, R.id.socialFragment2)
                R.id.canteen_nav_graph -> popToRoot(navController, R.id.canteenFragment)
                R.id.marketPlaceFragment -> popToRoot(navController, R.id.marketPlaceFragment)
                R.id.cartFragment -> popToRoot(navController, R.id.cartFragment)
            }
        }

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
        
        // Setup Navigation Drawer
        setupNavigationDrawer()
        
        // Handle back button for drawer
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
    
    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_about_app -> {
                    showAboutAppDialog()
                }
                R.id.nav_website -> {
                    openWebsite("https://campusdock.live")
                }
                R.id.nav_developers -> {
                    showDevelopersDialog()
                }
                R.id.nav_feedback -> {
                    sendFeedback()
                }
                R.id.nav_share -> {
                    shareApp()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    
    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }
    
    private fun showAboutAppDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("About Campus Dock")
            .setMessage("Campus Dock is your all-in-one campus companion app that brings together essential campus services including canteen ordering, marketplace, social networking, and more.\n\nVersion: 1.0\n\nMade with ❤️ for students")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    
    private fun showDevelopersDialog() {
        // Create developer data - Customize this with your actual team info
        val developers = listOf(
            Developer(
                name = "Sayed Haneef",
                designation = "Backend Development | Android Development",
                email = "haneefatwork01@gmail.com",
                social = "https://www.linkedin.com/in/sayed-mohd-haneef-b12155217/",
                photoResId = R.drawable.haneef_photo
            ),
            Developer(
                name = "Ritik Kumar",
                designation = "Backend Development | Devops",
                email = "ritik352alc@gmail.com",
                social = "https://www.linkedin.com/in/ritikkumar352/",
                photoResId = R.drawable.ritik_photo
            ),
            Developer(
                name = "Sania Khan",
                designation = "Web Development | Project Management",
                email = "saniaakhan76@gmail.con",
                social = "https://www.linkedin.com/in/sania-khan-180673275/",
                photoResId = R.drawable.sania_photo
            )
        )
        
        // Inflate custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.drawer_dialog_developers, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.developersRecyclerView)
        
        // Setup RecyclerView with horizontal layout
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        
        // Create and set adapter for infinite scrolling
        val adapter = DeveloperAdapter(developers)
        recyclerView.adapter = adapter
        
        // Scroll to a middle position to allow scrolling in both directions
        val middlePosition = Int.MAX_VALUE / 2
        val offset = middlePosition % developers.size
        recyclerView.scrollToPosition(middlePosition - offset)
        
        // Create and show dialog
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        dialogView.findViewById<MaterialButton>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun openWebsite(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open website", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:haneefatwork01@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Campus Dock Feedback")
        }
        try {
            startActivity(Intent.createChooser(intent, "Send Feedback"))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Campus Dock App")
            putExtra(Intent.EXTRA_TEXT, "Check out Campus Dock - Your Campus Companion! Download now: https://play.google.com/store/apps/details?id=$packageName")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Campus Dock"))
    }

    private fun popToRoot(navController: NavController, destinationId: Int) {
        val wasPopped = navController.popBackStack(destinationId, false)
        if (!wasPopped) {
            navController.navigate(destinationId)
        }
    }

}