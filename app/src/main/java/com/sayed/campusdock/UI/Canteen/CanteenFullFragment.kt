package com.sayed.campusdock.UI.Canteen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sayed.campusdock.Data.MenuItem
import com.sayed.campusdock.R
import com.sayed.campusdock.ViewModel.CanteenMenuViewModel
import com.sayed.campusdock.databinding.CanteenFullFragmentBinding
import kotlinx.coroutines.launch

class CanteenFullFragment : Fragment() {

    private var _binding: CanteenFullFragmentBinding? = null
    private val binding get() = _binding!!

    // Getting a reference to the ViewModel, scoped to this Fragment.
    private val viewModel: CanteenMenuViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CanteenFullFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // ✅ Use Safe Args to get the passed data
        val args = CanteenFullFragmentArgs.fromBundle(requireArguments())
        val canteenId = args.canteenId
        val canteenName = args.canteenName

        // Filling top canteen card info
        binding.cafeTitle.text = canteenName
        Glide.with(requireContext()).load(args.canteenUrl).placeholder(R.drawable.canteen_img).into(binding.cafeImage)
        binding.cafeTiming.text = if (args.canteenOpen) "Open Now" else "Closed"

        //Observe the menu items from the ViewModel
        observeMenuItems()
        observeErrors()

        viewModel.fetchMenuItems(canteenId)
    }
    private fun observeMenuItems() {
        lifecycleScope.launch {
            viewModel.menuItems.collect { menuItems ->
                if (menuItems.isNotEmpty()) {
                    displayMenuItems(menuItems)
                }
            }
        }
    }

    private fun observeErrors() {
        lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                if (errorMessage.isNotEmpty()) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayMenuItems(menuItems: List<MenuItem>) {
        Log.d("CanteenFullFragment", "Displaying ${menuItems.size} menu items")
        val container = binding.canteenContainer
        container.removeAllViews() // Clear previous views

        // Loop through each menu item and create a card for it
        for (item in menuItems) {
            val card = layoutInflater.inflate(R.layout.canteen_cart_list_item, container, false)
            val nameView = card.findViewById<TextView>(R.id.foodNameIddddd)
            val priceView = card.findViewById<TextView>(R.id.foodPriceIddddd)
            val imageView = card.findViewById<ImageView>(R.id.foodImageIdddd)
            val btnAdd = card.findViewById<Button>(R.id.btnAddIddddddd)
            val quantitySelector = card.findViewById<LinearLayout>(R.id.quantitySelector)
            val quantityText = card.findViewById<TextView>(R.id.quantityIddddd)
            val btnPlus = card.findViewById<Button>(R.id.btnPlusIddddd)
            val btnMinus = card.findViewById<Button>(R.id.btnMinusIdddd)

            // Setting the static data for menu item card
            Log.d("CanteenFullFragment", "Setting data for item: ${item.foodName}")
            nameView.text = item.foodName
            priceView.text = "₹ ${item.price}"
            Glide.with(this).load(item.url).placeholder(R.drawable.burger).into(imageView)

            // 5. OBSERVE the cart state from the ViewModel for THIS SPECIFIC item
            // This replaces all the manual UI updates and if/else blocks
            lifecycleScope.launch {
                viewModel.cartItems.collect { cartMap ->
                    val quantityInCart = cartMap[item.id]?.quantity ?: 0
                    if (quantityInCart > 0) {
                        quantitySelector.visibility = View.VISIBLE
                        btnAdd.visibility = View.GONE
                        quantityText.text = quantityInCart.toString()
                    } else {
                        quantitySelector.visibility = View.GONE
                        btnAdd.visibility = View.VISIBLE
                    }
                }
            }

            // 6. DELEGATE all button clicks to the ViewModel
            btnAdd.setOnClickListener {
                //Toast.makeText(requireContext(), "Add buttton clicked", Toast.LENGTH_SHORT).show()
                viewModel.onAddItemClicked(item) }
            btnPlus.setOnClickListener {
                //Toast.makeText(requireContext(), "Plus buttton clicked", Toast.LENGTH_SHORT).show()
                viewModel.onUpdateQuantity(item.id, 1) }
            btnMinus.setOnClickListener {
                //Toast.makeText(requireContext(), "Minus buttton clicked", Toast.LENGTH_SHORT).show()
                viewModel.onUpdateQuantity(item.id, -1) }

            container.addView(card)
        }
    }

        override fun onStop() {
            super.onStop()
            viewModel.scheduleSync()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


