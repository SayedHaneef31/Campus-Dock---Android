package com.sayed.campusdock.UI.MarketPlace

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.sayed.campusdock.Adaptor.MarketplaceAdapter
import com.sayed.campusdock.ConfigManager.TokenManager
import com.sayed.campusdock.Data.Marketplace.Product
import com.sayed.campusdock.R
import com.sayed.campusdock.ViewModel.MarketplaceUiState
import com.sayed.campusdock.ViewModel.MarketplaceViewModel
import com.sayed.campusdock.databinding.MarketplaceFragmentBinding
import kotlinx.coroutines.launch

class MarketplaceFragment : Fragment() {

    private var _binding: MarketplaceFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MarketplaceViewModel by viewModels()
    private lateinit var adapter: MarketplaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MarketplaceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        val collegeId = TokenManager.getCollegeId()
        viewModel.loadProducts(collegeId)
    }

    private fun setupRecyclerView() {
        adapter = MarketplaceAdapter { product ->
            showProductDetailDialog(product)
        }
        binding.marketplaceRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.marketplaceRecyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.imgProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.btnMenu.setOnClickListener {
            (activity as? com.sayed.campusdock.UI.Main.MainActivity)?.openDrawer()
        }

        binding.btnRetry.setOnClickListener {
            val collegeId = TokenManager.getCollegeId()
            viewModel.refresh(collegeId)
        }

        binding.fabAdd.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun showAddProductDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_product)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        val etProductName = dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etProductName)
        val etDescription = dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDescription)
        val etPrice = dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPrice)
        val switchIsService = dialog.findViewById<com.google.android.material.materialswitch.MaterialSwitch>(R.id.switchIsService)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)
        val btnPostListing = dialog.findViewById<MaterialButton>(R.id.btnPostListing)
        val progressBar = dialog.findViewById<android.widget.ProgressBar>(R.id.progressBar)

        btnClose.setOnClickListener { dialog.dismiss() }

        btnPostListing.setOnClickListener {
            val name = etProductName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val isService = switchIsService.isChecked

            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter product name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (price.isEmpty()) {
                Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnPostListing.isEnabled = false
            btnPostListing.alpha = 0.5f
            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    val nameBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), name)
                    val descBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), description)
                    val priceBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), price)
                    val serviceBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), isService.toString())

                    val collegeId = TokenManager.getCollegeId()
                    val response = com.sayed.campusdock.API.RetrofitClient.instance.createProduct(
                        name = nameBody,
                        description = descBody,
                        price = priceBody,
                        service = serviceBody,
                        media = null, // TODO: Add image picker later
                        collegeId = collegeId
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(context, "Product listed successfully!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        // Refresh the product list
                        viewModel.refresh(collegeId)
                    } else {
                        Toast.makeText(context, "Failed to list product: ${response.message()}", Toast.LENGTH_LONG).show()
                        btnPostListing.isEnabled = true
                        btnPostListing.alpha = 1f
                    }
                } catch (e: Exception) {
                    Log.e("MARKETPLACE", "Failed to create product: ${e.message}")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    btnPostListing.isEnabled = true
                    btnPostListing.alpha = 1f
                } finally {
                    progressBar.visibility = View.GONE
                }
            }
        }

        dialog.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.products.collect { products ->
                        adapter.submitList(products)
                    }
                }
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }
            }
        }
    }

    private fun handleUiState(state: MarketplaceUiState) {
        binding.progressBar.isVisible = state is MarketplaceUiState.Loading
        binding.emptyState.isVisible = state is MarketplaceUiState.Empty
        binding.errorState.isVisible = state is MarketplaceUiState.Error
        binding.marketplaceRecyclerView.isVisible = state is MarketplaceUiState.Success

        if (state is MarketplaceUiState.Error) {
            binding.tvErrorMessage.text = state.message
        }
    }

    private fun showProductDetailDialog(product: Product) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_product_detail)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.attributes?.windowAnimations = R.style.ProductDetailDialogAnimation

        val imgProduct = dialog.findViewById<ImageView>(R.id.dialogProductImage)
        val txtName = dialog.findViewById<TextView>(R.id.dialogProductName)
        val txtPrice = dialog.findViewById<TextView>(R.id.dialogProductPrice)
        val txtDescription = dialog.findViewById<TextView>(R.id.dialogProductDescription)
        val txtSeller = dialog.findViewById<TextView>(R.id.dialogSellerName)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)
        val btnMessage = dialog.findViewById<MaterialButton>(R.id.btnMessage)

        // Load image with Glide
        if (!product.imageUrl.isNullOrBlank()) {
            Glide.with(requireContext())
                .load(product.imageUrl)
                .placeholder(R.drawable.keyboard)
                .error(R.drawable.keyboard)
                .into(imgProduct)
        } else {
            imgProduct.setImageResource(R.drawable.keyboard)
        }

        txtName.text = product.name
        txtPrice.text = product.price
        txtDescription?.text = product.description ?: "No description available."
        txtSeller.text = product.sellerName

        btnClose.setOnClickListener { dialog.dismiss() }
        btnMessage.setOnClickListener {
            Toast.makeText(context, "Messaging ${product.sellerName}...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}