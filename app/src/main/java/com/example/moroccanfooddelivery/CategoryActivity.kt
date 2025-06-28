package com.example.moroccanfooddelivery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moroccanfooddelivery.adapters.CategoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryActivity : AppCompatActivity(), CategoryAdapter.OnCategoryClickListener {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.rvCategories)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns

        // Initialize categories list
        val categories = listOf(
            Category("Best Sellers", "bestseller", R.drawable.bestsellers),
            Category("Salty", "salty", R.drawable.salty),
            Category("Sweet", "sweet", R.drawable.sweets),
            Category("Baked", "baked", R.drawable.baked)
        )

        // Set adapter
        val adapter = CategoryAdapter(this, categories, this)
        recyclerView.adapter = adapter

        // Set up cart button
        findViewById<FloatingActionButton>(R.id.fabCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    override fun onCategoryClick(categoryType: String) {
        navigateToProductList(categoryType)
    }

    private fun navigateToProductList(category: String) {
        val intent = Intent(this, ProductListActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }
}