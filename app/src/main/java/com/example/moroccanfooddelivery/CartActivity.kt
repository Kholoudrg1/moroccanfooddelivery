package com.example.moroccanfooddelivery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moroccanfooddelivery.adapters.CartAdapter
import com.example.moroccanfooddelivery.database.AppDatabase
import com.example.moroccanfooddelivery.database.entities.CartItem
import kotlin.concurrent.thread

class CartActivity : AppCompatActivity() {

    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvEmptyCart: TextView
    private lateinit var btnCheckout: Button

    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize views
        rvCartItems = findViewById(R.id.rvCartItems)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        tvEmptyCart = findViewById(R.id.tvEmptyCart)
        btnCheckout = findViewById(R.id.btnCheckout)

        // Set up RecyclerView
        rvCartItems.layoutManager = LinearLayoutManager(this)

        // Load cart items
        loadCartItems()

        // Set up checkout button
        btnCheckout.setOnClickListener {
            Log.d("CartActivity", "Checkout button clicked")
            if (cartItems.isNotEmpty()) {
                try {
                    val intent = Intent(this, CheckoutActivity::class.java)
                    val total = cartItems.sumOf { it.price * it.quantity }
                    intent.putExtra("SUBTOTAL", total.toDouble())
                    Log.d("CartActivity", "Starting CheckoutActivity with subtotal: $total")
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("CartActivity", "Error starting CheckoutActivity: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                Log.d("CartActivity", "Cart is empty, not starting CheckoutActivity")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload cart items when returning to this activity
        loadCartItems()
    }

    private fun loadCartItems() {
        thread {
            try {
                val db = AppDatabase.getDatabase(this)
                val items = db.cartDao().getAllCartItems()

                Log.d("CartActivity", "Loaded ${items.size} cart items")

                cartItems.clear()
                cartItems.addAll(items)

                runOnUiThread {
                    rvCartItems.adapter = CartAdapter(this, cartItems) {
                        // This callback is triggered when cart is updated
                        updateTotalPrice()
                    }

                    updateTotalPrice()
                    updateEmptyCartVisibility()
                }
            } catch (e: Exception) {
                Log.e("CartActivity", "Error loading cart items: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf { it.price * it.quantity }
        tvTotalPrice.text = "${total.toInt()} DH"
    }

    private fun updateEmptyCartVisibility() {
        if (cartItems.isEmpty()) {
            tvEmptyCart.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
            btnCheckout.isEnabled = false
            btnCheckout.alpha = 0.5f
        } else {
            tvEmptyCart.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
            btnCheckout.isEnabled = true
            btnCheckout.alpha = 1.0f
        }
    }
}