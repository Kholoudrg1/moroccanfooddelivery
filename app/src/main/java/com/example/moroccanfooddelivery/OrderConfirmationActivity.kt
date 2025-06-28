package com.example.moroccanfooddelivery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var tvOrderTotal: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var btnContinueShopping: Button
    private lateinit var btnTrackOrder: Button

    private var orderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d("OrderConfirmation", "Starting activity creation")
            setContentView(R.layout.activity_order_confirmation)

            Log.d("OrderConfirmation", "Content view set successfully")

            // Initialize views
            tvOrderTotal = findViewById(R.id.tvOrderTotal)
            tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
            btnContinueShopping = findViewById(R.id.btnContinueShopping)
            btnTrackOrder = findViewById(R.id.btnTrackOrder)

            // Get the order total and payment method from the intent
            val orderTotal = intent.getDoubleExtra("ORDER_TOTAL", 0.0)
            val paymentMethod = intent.getStringExtra("PAYMENT_METHOD") ?: "Cash on Delivery" // Default if not provided

            Log.d("OrderConfirmation", "Order total received: $orderTotal")
            Log.d("OrderConfirmation", "Payment method: $paymentMethod")

            // Generate a random order ID (in a real app, this would come from the database)
            orderId = Random().nextInt(10000) + 1000
            Log.d("OrderConfirmation", "Generated order ID: $orderId")

            // Set the order total and payment method text
            tvOrderTotal.text = String.format("%.2f DH", orderTotal)
            tvPaymentMethod.text = paymentMethod

            // Set up track order button
            btnTrackOrder.setOnClickListener {
                try {
                    Log.d("OrderConfirmation", "Track button clicked, preparing to navigate to TrackingActivity")
                    val intent = Intent(this, TrackingActivity::class.java)
                    intent.putExtra("ORDER_ID", orderId)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("OrderConfirmation", "Error navigating to tracking: ${e.message}", e)
                    Toast.makeText(this, "Tracking feature coming soon!", Toast.LENGTH_SHORT).show()
                }
            }

            // Set up continue shopping button
            btnContinueShopping.setOnClickListener {
                try {
                    Log.d("OrderConfirmation", "Continue shopping clicked, navigating to CategoryActivity")
                    // Navigate to CategoryActivity instead of MainActivity
                    val intent = Intent(this, CategoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("OrderConfirmation", "Error navigating to categories: ${e.message}", e)
                    Toast.makeText(this, "Error returning to categories screen", Toast.LENGTH_SHORT).show()
                }
            }

            Log.d("OrderConfirmation", "Activity setup complete")
        } catch (e: Exception) {
            Log.e("OrderConfirmation", "Fatal error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error confirming your order. Please try again.", Toast.LENGTH_SHORT).show()
            finish() // Go back to previous activity
        }
    }
}