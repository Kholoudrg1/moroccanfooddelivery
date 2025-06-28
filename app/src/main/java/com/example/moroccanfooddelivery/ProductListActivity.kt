package com.example.moroccanfooddelivery

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moroccanfooddelivery.adapters.ProductAdapter
import com.example.moroccanfooddelivery.database.AppDatabase
import com.example.moroccanfooddelivery.database.entities.Product
import kotlin.concurrent.thread

class ProductListActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var tvNoProducts: TextView
    private var productsList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        try {
            // Initialize views
            rvProducts = findViewById(R.id.rvProducts)
            tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
            tvNoProducts = findViewById(R.id.tvNoProducts)

            // Get category from intent
            val category = intent.getStringExtra("CATEGORY") ?: "bestseller"

            // Set up toolbar
            val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            if (toolbar != null) {
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = getCategoryDisplayName(category)
            }

            // Set category title
            tvCategoryTitle.text = getCategoryDisplayName(category)

            // Set up RecyclerView
            rvProducts.layoutManager = GridLayoutManager(this, 2)

            // Load products for the selected category
            loadProductsByCategory(category)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getCategoryDisplayName(category: String): String {
        return when (category) {
            "bestseller" -> "Best Sellers"
            "salty" -> "Salty Dishes"
            "sweet" -> "Sweet Treats"
            "baked" -> "Baked Goods"
            else -> category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    private fun loadProductsByCategory(category: String) {
        try {
            thread {
                try {
                    val db = AppDatabase.getDatabase(this)
                    val products = db.productDao().getProductsByCategory(category)

                    runOnUiThread {
                        if (products.isEmpty()) {
                            tvNoProducts.visibility = View.VISIBLE
                            rvProducts.visibility = View.GONE
                        } else {
                            tvNoProducts.visibility = View.GONE
                            rvProducts.visibility = View.VISIBLE

                            productsList.clear()
                            productsList.addAll(products)
                            val adapter = ProductAdapter(this, productsList)
                            rvProducts.adapter = adapter
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this, "Database error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading products: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}