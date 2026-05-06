package com.sayed.campusdock.UI.MarketPlace


import com.sayed.campusdock.R

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sayed.campusdock.Adaptor.MarketplaceAdapter
import com.sayed.campusdock.Data.Marketplace.Product

class MarketplaceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.marketplace_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.marketplaceRecyclerView)
        val imgProfile = view.findViewById<View>(R.id.imgProfile)
        val btnMenu = view.findViewById<View>(R.id.btnMenu)

        // Use GridLayoutManager with a span count of 2 for a grid layout
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Generate mock data
        val productList = generateMockData()

        // Set up the adapter with click callback
        val adapter = MarketplaceAdapter(productList) { product ->
            showProductDetailDialog(product)
        }
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

    private fun showProductDetailDialog(product: Product) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_product_detail)

        // Make dialog background transparent for rounded corners
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set dialog width to match parent with margins
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Allow dismissing by tapping outside
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog animation
        dialog.window?.attributes?.windowAnimations = R.style.ProductDetailDialogAnimation

        // Bind views
        val imgProduct = dialog.findViewById<ImageView>(R.id.dialogProductImage)
        val txtName = dialog.findViewById<TextView>(R.id.dialogProductName)
        val txtPrice = dialog.findViewById<TextView>(R.id.dialogProductPrice)
        val imgAvatar = dialog.findViewById<ImageView>(R.id.dialogSellerAvatar)
        val txtSeller = dialog.findViewById<TextView>(R.id.dialogSellerName)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)
        val btnMessage = dialog.findViewById<MaterialButton>(R.id.btnMessage)

        // Set product data
        imgProduct.setImageResource(product.imageUrl)
        txtName.text = product.name
        txtPrice.text = product.price
        txtSeller.text = product.sellerName

        // Close button
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // Message seller button
        btnMessage.setOnClickListener {
            Toast.makeText(context, "Messaging ${product.sellerName}...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}