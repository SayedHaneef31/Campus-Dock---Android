package com.sayed.campusdock.Adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sayed.campusdock.Data.Marketplace.Product
import com.sayed.campusdock.R

class MarketplaceAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<MarketplaceAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.marketplace_item_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.imgProduct)
        private val productName: TextView = itemView.findViewById(R.id.txtProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.txtProductPrice)
        private val sellerName: TextView = itemView.findViewById(R.id.txtSellerName)

        fun bind(product: Product) {
            productImage.setImageResource(product.imageUrl)
            productName.text = product.name
            productPrice.text = product.price
            sellerName.text = product.sellerName
        }
    }
}