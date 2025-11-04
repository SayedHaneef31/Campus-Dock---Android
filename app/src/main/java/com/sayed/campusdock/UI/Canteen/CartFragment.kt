package com.sayed.campusdock.UI.Canteen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sayed.campusdock.Adaptor.CartAdapter
import androidx.navigation.fragment.findNavController
import com.sayed.campusdock.ViewModel.CanteenCartViewModel
import com.sayed.campusdock.ViewModel.PlaceOrderStatus
import com.sayed.campusdock.databinding.CanteenCartFragmentBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale



class CartFragment : Fragment() {

    private var _binding: CanteenCartFragmentBinding? = null
    private val binding get() = _binding!!

    //Getting an instance of the view model
    private val viewModel: CanteenCartViewModel by viewModels()

    // Declare the adapter
    private lateinit var cartAdapter: CartAdapter

    private val TAG = "CartFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CanteenCartFragmentBinding.inflate(inflater, container, false)

        binding.btnPlaceOrder.setOnClickListener {
            viewModel.placeOrder()
        }
        binding.imgProfile.setOnClickListener {
            findNavController().navigate(com.sayed.campusdock.R.id.profileFragment)
        }
        binding.btnMenu.setOnClickListener {
            (activity as? com.sayed.campusdock.UI.Main.MainActivity)?.openDrawer()
        }
         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        setupRecyclerView()
        observeCartItems()
        observeCartTotal()
        observePlaceOrderStatus()
    }
    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        // Initialize the adapter and pass it the ViewModel function to call on clicks
        cartAdapter = CartAdapter { itemId, change ->
            Log.d(TAG, "CartAdapter click: itemId=$itemId, change=$change")
            viewModel.onUpdateQuantity(itemId, change)
        }
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun observeCartItems() {
        Log.d(TAG, "observeCartItems started")
        lifecycleScope.launch {
            // This will automatically update the adapter's list whenever the cart changes
            viewModel.cartItems.collect { items ->
                Log.i(TAG, "Cart items updated: size=${items.size}, items=$items")
                cartAdapter.submitList(items)
            }
        }
    }

    private fun observeCartTotal() {
        Log.d(TAG, "observeCartTotal started")
        lifecycleScope.launch {
            // This will automatically update the price summary whenever the total changes
            viewModel.cartTotal.collect { total ->
                Log.i(TAG, "Cart total updated: $total")
                updatePriceSummary(total)
            }
        }
    }

    private fun updatePriceSummary(subTotal: Double) {
        Log.d(TAG, "Updating price summary for subtotal=$subTotal")
        val tax = subTotal * 0.05 // Example: 5% tax
        val grandTotal = subTotal + tax

        // Format currency for display
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        binding.tvNetTotal.text = currencyFormat.format(subTotal)
        binding.tvCGST.text = currencyFormat.format(tax)
        binding.tvGrandTotal.text = currencyFormat.format(grandTotal)

        Log.i(TAG, "Price summary -> Subtotal=$subTotal, Tax=$tax, GrandTotal=$grandTotal")
    }

    private fun observePlaceOrderStatus() {
        lifecycleScope.launch {
            viewModel.placeOrderStatus.collect { status ->
                when (status) {
                    is PlaceOrderStatus.Loading -> {
                        // Show a progress bar or disable the button
                        binding.btnPlaceOrder.isEnabled = false
                        binding.btnPlaceOrder.text = "Placing Order..."
                    }
                    is PlaceOrderStatus.Success -> {
                        // The sync was successful! Now you can navigate to the next screen.
                        Toast.makeText(requireContext(), "Order Placed! Proceeding to payment...", Toast.LENGTH_SHORT).show()

                        // TODO: Navigate to your payment or order confirmation screen
                        // findNavController().navigate(R.id.action_cartFragment_to_paymentFragment)

                        // Reset the state in the ViewModel so this doesn't trigger again on screen rotation
                        viewModel.onOrderPlacedHandled()
                    }
                    is PlaceOrderStatus.Error -> {
                        // Show an error message
                        Toast.makeText(requireContext(), status.message, Toast.LENGTH_LONG).show()

                        // Re-enable the button
                        binding.btnPlaceOrder.isEnabled = true
                        binding.btnPlaceOrder.text = "Place Order"
                        viewModel.onOrderPlacedHandled()
                    }
                    is PlaceOrderStatus.Idle -> {
                        // Re-enable the button and reset text
                        binding.btnPlaceOrder.isEnabled = true
                        binding.btnPlaceOrder.text = "Place Order"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView called - binding cleared")
        _binding = null
    }

}

