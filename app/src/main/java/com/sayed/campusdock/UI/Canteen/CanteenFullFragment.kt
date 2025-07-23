package com.sayed.campusdock.UI.Canteen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.MenuItem
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.CanteenFullFragmentBinding
import kotlinx.coroutines.launch

class CanteenFullFragment : Fragment() {

    private var _binding: CanteenFullFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CanteenFullFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun fetchAndDisplayMenuItems(canteenId: String) {
        lifecycleScope.launch {
            try {
                val menuItems = RetrofitClient.instance.getMenuItems(canteenId)
                Log.d("CanteenFullFragment", "Fetched ${menuItems.size} menu items")
                Toast.makeText(requireContext(), "Fetched the menu", Toast.LENGTH_SHORT).show()
                displayMenuItems(menuItems)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load menu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayMenuItems(menuItems: List<MenuItem>) {
        Log.d("CanteenFullFragment", "Displaying ${menuItems.size} menu items")
        val container = binding.canteenContainer
        container.removeAllViews()

        for (item in menuItems) {
            val card = layoutInflater.inflate(R.layout.canteen_menu_item, container, false)

            val nameView = card.findViewById<TextView>(R.id.foodNameIddddd)
            val priceView = card.findViewById<TextView>(R.id.foodPriceIddddd)
            val imageView = card.findViewById<ImageView>(R.id.foodImageIdddd)

            nameView.text = item.name
            priceView.text = "₹ ${item.price}"

            // Load image using Glide
            Glide.with(this)
                .load(item.url)
                .placeholder(R.drawable.burger)
                .into(imageView)


            // You can add click listeners for add buttons here
            container.addView(card)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // ✅ Use Safe Args to get the passed data
        val args = CanteenFullFragmentArgs.fromBundle(requireArguments())

        val name = args.canteenName
        val url = args.canteenUrl
        val isOpen = args.canteenOpen
        val id = args.canteenId    // This came a s string
        Log.d("CanteenFullFragment", "Canteen ID: $id")
        Log.d("CanteenFullFragment", "Canteen name: $name")
        Log.d("CanteenFullFragment", "Canteen URL: $url")
        Log.d("CanteenFullFragment", "Canteen Open: $isOpen")

        // Example: set name to a TextView
        view.findViewById<TextView>(R.id.cafeTitle)?.text = name
        view.findViewById<TextView>(R.id.cafeTiming)?.text = if (isOpen) "Open Now" else "Closed"

        Glide.with(requireContext())
            .load(url)
            .into(view.findViewById(R.id.cafeImage))


        fetchAndDisplayMenuItems(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}