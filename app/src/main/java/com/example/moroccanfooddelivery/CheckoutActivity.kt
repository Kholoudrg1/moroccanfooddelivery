package com.example.moroccanfooddelivery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.moroccanfooddelivery.database.AppDatabase
import com.example.moroccanfooddelivery.database.entities.User
import kotlin.concurrent.thread

class CheckoutActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotal: TextView
    private lateinit var rgPaymentMethod: RadioGroup
    private lateinit var btnConfirmOrder: Button

    private var subtotal: Double = 0.0
    private val deliveryFee: Double = 10.0 // Example fixed fee

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_checkout)

            Log.d("CheckoutActivity", "Activity created")

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Checkout"

            etName = findViewById(R.id.etName)
            etPhone = findViewById(R.id.etPhone)
            etAddress = findViewById(R.id.etAddress)
            tvSubtotal = findViewById(R.id.tvSubtotal)
            tvDeliveryFee = findViewById(R.id.tvDeliveryFee)
            tvTotal = findViewById(R.id.tvTotal)
            rgPaymentMethod = findViewById(R.id.rgPaymentMethod)
            btnConfirmOrder = findViewById(R.id.btnOrder)

            loadUserInfo()
            calculateTotals()

            btnConfirmOrder.setOnClickListener {
                confirmOrder()
            }
        } catch (e: Exception) {
            Log.e("CheckoutActivity", "Error in onCreate: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Error initializing checkout", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadUserInfo() {
        thread {
            try {
                val db = AppDatabase.getDatabase(this)
                val user: User? = db.userDao().getUserById(1) // Replace with actual user ID retrieval

                runOnUiThread {
                    user?.let {
                        etName.setText(it.name)
                        etPhone.setText(it.phone)
                        etAddress.setText(it.address)
                    }
                }
            } catch (e: Exception) {
                Log.e("CheckoutActivity", "Error loading user info: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun calculateTotals() {
        try {
            subtotal = intent.getDoubleExtra("SUBTOTAL", 0.0)
            Log.d("CheckoutActivity", "Received subtotal: $subtotal")

            tvSubtotal.text = String.format("%.2f DH", subtotal)
            tvDeliveryFee.text = String.format("%.2f DH", deliveryFee)
            tvTotal.text = String.format("%.2f DH", (subtotal + deliveryFee))
        } catch (e: Exception) {
            Log.e("CheckoutActivity", "Error calculating totals: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun confirmOrder() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            Log.e("CheckoutActivity", "Please fill in all fields")
            return
        }

        Log.d("CheckoutActivity", "Order details confirmed for $name")

        // Get selected payment method
        val selectedPaymentMethodId = rgPaymentMethod.checkedRadioButtonId
        val paymentMethod = when (selectedPaymentMethodId) {
            R.id.rbCardPayment -> "Credit/Debit Card"
            else -> "Cash on Delivery"
        }

        Log.d("CheckoutActivity", "Payment method selected: $paymentMethod")

        // Direct to appropriate activity based on payment method
        if (paymentMethod == "Credit/Debit Card") {
            // Navigate to payment activity for online payment
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("ORDER_TOTAL", (subtotal + deliveryFee))
            intent.putExtra("PAYMENT_METHOD", paymentMethod)
            startActivity(intent)
        } else {
            // Navigate directly to order confirmation for cash on delivery
            val intent = Intent(this, OrderConfirmationActivity::class.java)
            intent.putExtra("ORDER_TOTAL", (subtotal + deliveryFee))
            intent.putExtra("PAYMENT_METHOD", paymentMethod)
            startActivity(intent)
        }
        // Don't call finish() here to allow the user to go back to the checkout screen if needed
    }
}