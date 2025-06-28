package com.example.moroccanfooddelivery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moroccanfooddelivery.R
import com.example.moroccanfooddelivery.database.AppDatabase
import com.example.moroccanfooddelivery.database.entities.CartItem
import kotlin.concurrent.thread

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCartItemImage: ImageView = itemView.findViewById(R.id.ivCartItemImage)
        val tvCartItemName: TextView = itemView.findViewById(R.id.tvCartItemName)
        val tvCartItemPrice: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.ivCartItemImage.setImageResource(cartItem.imageResourceId)
        holder.tvCartItemName.text = cartItem.productName
        holder.tvCartItemPrice.text = "${cartItem.price} DH"
        holder.tvQuantity.text = cartItem.quantity.toString()

        holder.btnDecrease.setOnClickListener {
            if (cartItem.quantity > 1) {
                updateQuantity(cartItem, cartItem.quantity - 1, position)
            }
        }

        holder.btnIncrease.setOnClickListener {
            updateQuantity(cartItem, cartItem.quantity + 1, position)
        }

        holder.btnRemove.setOnClickListener {
            removeItem(cartItem, position)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    private fun updateQuantity(cartItem: CartItem, newQuantity: Int, position: Int) {
        thread {
            val db = AppDatabase.getDatabase(context)
            val updatedItem = cartItem.copy(quantity = newQuantity)
            db.cartDao().updateCartItem(updatedItem)

            cartItems[position] = updatedItem

            (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                notifyItemChanged(position)
                onCartUpdated()
            }
        }
    }

    private fun removeItem(cartItem: CartItem, position: Int) {
        thread {
            val db = AppDatabase.getDatabase(context)
            db.cartDao().removeCartItem(cartItem.id)

            cartItems.removeAt(position)

            (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)
                onCartUpdated()
            }
        }
    }
}