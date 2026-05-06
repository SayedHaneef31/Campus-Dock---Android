package com.sayed.campusdock.Adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sayed.campusdock.Data.Marketplace.Product
import com.sayed.campusdock.R

class MarketplaceAdapter(
    private var productList: List<Product> = emptyList(),
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<MarketplaceAdapter.ProductViewHolder>() {

    fun submitList(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.marketplace_item_card, parent, false)
        return ProductViewHolder(view, onProductClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    class ProductViewHolder(
        itemView: View,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.imgProduct)
        private val productName: TextView = itemView.findViewById(R.id.txtProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.txtProductPrice)
        private val sellerName: TextView = itemView.findViewById(R.id.txtSellerName)

        fun bind(product: Product) {
            if (!product.imageUrl.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.keyboard)
                    .error(R.drawable.keyboard)
                    .into(productImage)
            } else {
                productImage.setImageResource(R.drawable.keyboard)
            }
            productName.text = product.name
            productPrice.text = product.price
            sellerName.text = product.sellerName
            itemView.setOnClickListener { onProductClick(product) }
        }
    }
}