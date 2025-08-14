package com.sayed.campusdock.UI.Canteen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.sayed.campusdock.Data.Canteen
import com.sayed.campusdock.R
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.UI.Canteen.CanteenMenuActivity
import com.sayed.campusdock.databinding.CanteenFragmentBinding
import androidx.navigation.fragment.findNavController
import com.sayed.campusdock.ViewModel.CanteenViewModel


import kotlinx.coroutines.launch

class CanteenFragment : Fragment() {

    private var _binding: CanteenFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CanteenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = CanteenFragmentBinding.inflate(inflater, container, false)

        binding.cartFab.setOnClickListener {

        }

        //Toast.makeText(requireContext(), "Inside canteen fragement", Toast.LENGTH_SHORT).show()
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 Associate ViewModel with this Fragment
        viewModel = ViewModelProvider(this)[CanteenViewModel::class.java]

        // 🔹 Observe LiveData so that it updates UI when data arrives or changes
        viewModel.canteens.observe(viewLifecycleOwner) { canteens ->
            displayCanteens(canteens)
        }

        // Get token from SharedPreferences
        val prefs = requireContext().getSharedPreferences("CampusDockPrefs", android.content.Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        // Load if not already loaded
        if (token != null) {
            viewModel.fetchCanteens(token) // ✅ Pass token here
        } else {
            Log.e("CANTEEN_UI", "No JWT token found in SharedPreferences")
        }
    }

//    private fun fetchCanteens() {
//        lifecycleScope.launch {
//            try {
//                val canteens = RetrofitClient.instance.getCanteens()
//                displayCanteens(canteens)
//            } catch (e: Exception){
//                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
//                Log.e("HomeFragment", "Error: ${e.message}")
//            }
//
//        }
//    }

//    private fun displayCanteens(canteens: List<Canteen>) {
//        val container = binding.canteenContainer
//        container.removeAllViews()
//
//        for (canteen in canteens) {
//            val card = layoutInflater.inflate(R.layout.canteen_card, container, false)
//
//            val nameView = card.findViewById<TextView>(R.id.canteenName)
//            val statusView = card.findViewById<TextView>(R.id.canteenStatus)
//            val imageView = card.findViewById<ImageView>(R.id.canteenImage)
//
//            nameView.text = canteen.name
//
//            statusView.text = if (canteen.open) "Open" else "Closed"
//            statusView.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    if (canteen.open) R.color.green else R.color.red
//                )
//            )
//
//            Glide.with(requireContext())
//                .load(canteen.url)
//                .placeholder(R.drawable.canteen_img) // optional
//                .into(imageView)
//
//            // Set click to navigate
//            card.setOnClickListener {
//                val action = CanteenFragmentDirections.actionCanteenFragmentToCanteenFullFragment(
//                    canteenName = canteen.name,
//                    canteenUrl = canteen.url,
//                    canteenOpen = canteen.open,
//                    canteenId = canteen.id.toString()
//                )
//                findNavController().navigate(action)
//
//            }
//
//            container.addView(card)
//        }
//    }
//

    private fun displayCanteens(canteens: List<Canteen>) {
        Log.d("CANTEEN_UI", "Displaying ${canteens.size} canteens")

        val container = binding.canteenContainer
        container.removeAllViews()
        Log.d("CANTEEN_UI", "Cleared previous views from container")

        for ((index, canteen) in canteens.withIndex()) {
            Log.d("CANTEEN_UI", "Rendering canteen #$index: ${canteen.name}, Open: ${canteen.open}, ID: ${canteen.id}")

            val card = layoutInflater.inflate(R.layout.canteen_card, container, false)

            val nameView = card.findViewById<TextView>(R.id.canteenName)
            val statusView = card.findViewById<TextView>(R.id.canteenStatus)
            val imageView = card.findViewById<ImageView>(R.id.canteenImage)

            nameView.text = canteen.name

            statusView.text = if (canteen.open) "Open" else "Closed"
            statusView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (canteen.open) R.color.green else R.color.red
                )
            )

            Glide.with(requireContext())
                .load(canteen.url)
                .placeholder(R.drawable.canteen_img)
                .into(imageView)

            Log.d("CANTEEN_UI", "Image loaded from URL: ${canteen.url}")

            card.setOnClickListener {
                Log.d("CANTEEN_UI", "Card clicked: ${canteen.name} (ID: ${canteen.id})")

                val action = CanteenFragmentDirections.actionCanteenFragmentToCanteenFullFragment(
                    canteenName = canteen.name,
                    canteenUrl = canteen.url,
                    canteenOpen = canteen.open,
                    canteenId = canteen.id.toString()
                )
                findNavController().navigate(action)
            }

            container.addView(card)
            Log.d("CANTEEN_UI", "Added card for ${canteen.name} to container")
        }

        Log.d("CANTEEN_UI", "Finished rendering all canteens")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }
}