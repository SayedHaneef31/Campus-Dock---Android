package com.sayed.campusdock.UI.MarketPlace


import com.sayed.campusdock.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.Adaptor.MarketplaceAdapter
import com.sayed.campusdock.Data.Marketplace.Product

class MarketplaceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.market_place_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.marketplaceRecyclerView)
        val imgProfile = view.findViewById<View>(R.id.imgProfile)
        val btnMenu = view.findViewById<View>(R.id.btnMenu)

        // Use GridLayoutManager with a span count of 2 for a grid layout
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Generate mock data
        val productList = generateMockData()

        // Set up the adapter
        val adapter = MarketplaceAdapter(productList)
        recyclerView.adapter = adapter

        imgProfile?.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
        
        btnMenu?.setOnClickListener {
            (activity as? com.sayed.campusdock.UI.Main.MainActivity)?.openDrawer()
        }

        return view
    }

    private fun generateMockData(): List<Product> {
        return listOf(
            // You'll need to add mock images to your res/drawable folder
            Product("Calculus Textbook", "₹80", R.drawable.m1, "Sanidhya"),
            Product("Laptop Charger", "₹250", R.drawable.m2, "Ritik Kumar"),
            Product("Mini Fridge", "₹2000", R.drawable.m3, "Shourya Jaiswal"),
            Product("Study Table", "₹1750", R.drawable.m4, "Stuti Sharma"),
            Product("Basketball", "₹435", R.drawable.m5, "Shivam Gupta"),
            Product("Headphones", "₹890", R.drawable.m6, "Sayed Haneef"),
            Product("Java Programming Book", "₹180", R.drawable.m7, "Sayed Haneef")
        )
    }
}