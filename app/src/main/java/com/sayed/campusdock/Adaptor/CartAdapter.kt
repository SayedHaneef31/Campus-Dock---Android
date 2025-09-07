package com.sayed.campusdock.Adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sayed.campusdock.Data.Room.CartItem
import com.sayed.campusdock.R
import com.sayed.campusdock.databinding.CanteenCartListItemBinding // You will create this layout

class CartAdapter(
    private val onQuantityChanged: (String, Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CanteenCartListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: CanteenCartListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.foodNameIddddd.text = item.foodName
            binding.foodPriceIddddd.text = "₹ ${item.price}"
            binding.quantityIddddd.text = item.quantity.toString()

            Glide.with(itemView.context)
                .load(item.url)
                .placeholder(R.drawable.burger)
                .into(binding.foodImageIdddd)

            // Hide the "Add" button, as we are in the cart
            binding.btnAddIddddddd.visibility = View.GONE
            binding.quantitySelector.visibility = View.VISIBLE

            binding.btnPlusIddddd.setOnClickListener { onQuantityChanged(item.id, 1) }
            binding.btnMinusIdddd.setOnClickListener { onQuantityChanged(item.id, -1) }
        }
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}