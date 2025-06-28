package com.example.moroccanfooddelivery

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class PaymentActivity : AppCompatActivity() {

    private lateinit var tvAmountToPay: TextView
    private lateinit var etCardNumber: EditText
    private lateinit var etCardHolderName: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var etCvv: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnPay: Button
    private lateinit var btnCancel: Button

    private var orderTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_payment)

            Log.d("PaymentActivity", "Activity created")

            // Set up toolbar
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Payment"

            // Initialize views
            tvAmountToPay = findViewById(R.id.tvAmountToPay)
            etCardNumber = findViewById(R.id.etCardNumber)
            etCardHolderName = findViewById(R.id.etCardHolderName)
            etExpiryDate = findViewById(R.id.etExpiryDate)
            etCvv = findViewById(R.id.etCvv)
            cbTerms = findViewById(R.id.cbTerms)
            btnPay = findViewById(R.id.btnPay)
            btnCancel = findViewById(R.id.btnCancel)

            // Get order total from intent
            orderTotal = intent.getDoubleExtra("ORDER_TOTAL", 0.0)
            tvAmountToPay.text = String.format("%.2f DH", orderTotal)

            // Format card number as user types (add spaces)
            etCardNumber.addTextChangedListener(object : TextWatcher {
                var isFormatting = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting) {
                        return
                    }

                    isFormatting = true

                    // Remove all spaces first
                    val digits = s.toString().replace(" ", "")

                    // Add spaces after every 4 characters
                    val formatted = StringBuilder()
                    for (i in digits.indices) {
                        if (i > 0 && i % 4 == 0) {
                            formatted.append(" ")
                        }
                        formatted.append(digits[i])
                    }

                    // Set the formatted text
                    if (formatted.toString() != s.toString()) {
                        etCardNumber.setText(formatted.toString())
                        etCardNumber.setSelection(formatted.length)
                    }

                    isFormatting = false
                }
            })

            // Format expiry date as MM/YY
            etExpiryDate.addTextChangedListener(object : TextWatcher {
                var isFormatting = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting) {
                        return
                    }

                    isFormatting = true

                    val input = s.toString().replace("/", "")

                    if (input.length > 2) {
                        val month = input.substring(0, 2)
                        val year = input.substring(2)
                        etExpiryDate.setText("$month/$year")
                        etExpiryDate.setSelection(etExpiryDate.text.length)
                    }

                    isFormatting = false
                }
            })

            // Set up Pay button
            btnPay.setOnClickListener {
                processPayment()
            }

            // Set up Cancel button
            btnCancel.setOnClickListener {
                finish() // Go back to previous activity
            }

        } catch (e: Exception) {
            Log.e("PaymentActivity", "Error in onCreate: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Error initializing payment", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun processPayment() {
        // Validate card details
        if (!validateCardDetails()) {
            return
        }

        // In a real app, you would send these details to a payment gateway
        // For this demo app, we'll simulate a successful payment

        Log.d("PaymentActivity", "Processing payment...")

        // Show loading indicator (in a real app)
        // showLoadingIndicator()

        // Simulate network delay
        btnPay.isEnabled = false
        btnPay.text = "Processing..."

        // Simulate payment processing
        btnPay.postDelayed({
            Log.d("PaymentActivity", "Payment successful")

            // Navigate to order confirmation
            val intent = Intent(this, OrderConfirmationActivity::class.java)
            intent.putExtra("ORDER_TOTAL", orderTotal)
            intent.putExtra("PAYMENT_METHOD", "Credit/Debit Card")
            startActivity(intent)

            // Close payment activity
            finish()
        }, 2000) // 2 second delay to simulate processing
    }

    private fun validateCardDetails(): Boolean {
        // Check if card number is valid (simple validation)
        val cardNumber = etCardNumber.text.toString().replace(" ", "")
        if (cardNumber.length < 16) {
            Toast.makeText(this, "Please enter a valid card number", Toast.LENGTH_SHORT).show()
            etCardNumber.requestFocus()
            return false
        }

        // Check if cardholder name is provided
        if (etCardHolderName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter cardholder name", Toast.LENGTH_SHORT).show()
            etCardHolderName.requestFocus()
            return false
        }

        // Check if expiry date is valid
        val expiryDate = etExpiryDate.text.toString()
        if (!expiryDate.matches(Regex("^(0[1-9]|1[0-2])/[0-9]{2}$"))) {
            Toast.makeText(this, "Please enter a valid expiry date (MM/YY)", Toast.LENGTH_SHORT).show()
            etExpiryDate.requestFocus()
            return false
        }

        // Check if CVV is valid
        if (etCvv.text.toString().length < 3) {
            Toast.makeText(this, "Please enter a valid CVV", Toast.LENGTH_SHORT).show()
            etCvv.requestFocus()
            return false
        }

        // Check if terms are accepted
        if (!cbTerms.isChecked) {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}