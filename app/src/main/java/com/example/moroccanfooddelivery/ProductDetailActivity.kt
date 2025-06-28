package com.example.moroccanfooddelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moroccanfooddelivery.database.AppDatabase
import com.example.moroccanfooddelivery.database.entities.CartItem
import com.example.moroccanfooddelivery.database.entities.Product
import kotlin.concurrent.thread

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var ivProductDetail: ImageView
    private lateinit var tvProductDetailName: TextView
    private lateinit var tvProductDetailChef: TextView
    private lateinit var tvProductDetailPrice: TextView
    private lateinit var tvProductDetailDescription: TextView
    private lateinit var btnAddToCartDetail: Button

    private var productId: Int = 0
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        try {
            // Get product ID from intent
            productId = intent.getIntExtra("PRODUCT_ID", 0)

            if (productId == 0) {
                Toast.makeText(this, "Error: Product ID not found", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            // Initialize views
            ivProductDetail = findViewById(R.id.ivProductDetail)
            tvProductDetailName = findViewById(R.id.tvProductDetailName)
            tvProductDetailChef = findViewById(R.id.tvProductDetailChef)
            tvProductDetailPrice = findViewById(R.id.tvProductDetailPrice)
            tvProductDetailDescription = findViewById(R.id.tvProductDetailDescription)
            btnAddToCartDetail = findViewById(R.id.btnAddToCartDetail)

            // Load product details
            loadProductDetails()

            // Set up add to cart button
            btnAddToCartDetail.setOnClickListener {
                addToCart()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadProductDetails() {
        thread {
            try {
                val db = AppDatabase.getDatabase(this)
                product = db.productDao().getProductById(productId)

                runOnUiThread {
                    if (product != null) {
                        ivProductDetail.setImageResource(product.imageResourceId)
                        tvProductDetailName.text = product.name
                        tvProductDetailChef.text = "Chef: ${product.chefName}"
                        tvProductDetailPrice.text = "${product.price} DH"
                        tvProductDetailDescription.text = product.description
                    } else {
                        Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error loading product: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun addToCart() {
        thread {
            try {
                val db = AppDatabase.getDatabase(this)

                // Check if item already exists in cart
                val existingCartItem = db.cartDao().getCartItemByProductId(product.id)

                if (existingCartItem != null) {
                    // Update quantity
                    db.cartDao().updateQuantity(existingCartItem.id, existingCartItem.quantity + 1)
                    runOnUiThread {
                        Toast.makeText(this, "Added one more ${product.name} to cart", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Add new item
                    val cartItem = CartItem(
                        productId = product.id,
                        productName = product.name,
                        quantity = 1,
                        price = product.price,
                        imageResourceId = product.imageResourceId
                    )

                    db.cartDao().insertCartItem(cartItem)

                    runOnUiThread {
                        Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                    }
                }

                // Navigate to cart after adding
                runOnUiThread {
                    startActivity(Intent(this, CartActivity::class.java))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error adding to cart: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}