package com.example.moroccanfooddelivery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moroccanfooddelivery.R
import com.example.moroccanfooddelivery.database.entities.CartItem

class CheckoutItemAdapter(
    private val context: Context,
    private val items: List<CartItem>
) : RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder>() {

    inner class CheckoutItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivItemImage: ImageView = itemView.findViewById(R.id.ivItemImage)
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvItemQuantity: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val tvItemPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_checkout, parent, false)
        return CheckoutItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutItemViewHolder, position: Int) {
        val item = items[position]

        holder.ivItemImage.setImageResource(item.imageResourceId)
        holder.tvItemName.text = item.productName
        holder.tvItemQuantity.text = "x${item.quantity}"

        // Calculate total for this item
        val itemTotal = item.price * item.quantity
        holder.tvItemPrice.text = "${itemTotal.toInt()} DH"
    }

    override fun getItemCount(): Int = items.size
}