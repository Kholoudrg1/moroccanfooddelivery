package com.example.moroccanfooddelivery

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.moroccanfooddelivery.database.AppDatabase
import com.example.moroccanfooddelivery.database.entities.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database with sample data on first run
        initializeDatabaseIfNeeded()

        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            try {
                startActivity(Intent(this, CategoryActivity::class.java))
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "Error starting CategoryActivity: ${e.message}")
            }
        }
    }

    private fun initializeDatabaseIfNeeded() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstRun = sharedPref.getBoolean("is_first_run", true)

        if (isFirstRun) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(this@MainActivity)

                    // Sample products
                    val products = listOf(
                        // Best Sellers
                        Product(
                            name = "Couscous Royal",
                            description = "Traditional Friday dish with vegetables and meat",
                            chefName = "Fatima",
                            price = 90.0,
                            imageResourceId = R.drawable.couscous,
                            category = "bestseller"
                        ),
                        Product(
                            name = "Chicken Tajine",
                            description = "Slow-cooked chicken with preserved lemons and olives",
                            chefName = "Aicha",
                            price = 75.0,
                            imageResourceId = R.drawable.chicken_tajine,
                            category = "bestseller"
                        ),

                        // Salty
                        Product(
                            name = "Pastilla",
                            description = "Sweet and savory pie with chicken and almonds",
                            chefName = "Khadija",
                            price = 85.0,
                            imageResourceId = R.drawable.pastilla,
                            category = "salty"
                        ),
                        Product(
                            name = "Harira Soup",
                            description = "Traditional soup with tomatoes, lentils and chickpeas",
                            chefName = "Fatima",
                            price = 30.0,
                            imageResourceId = R.drawable.harira,
                            category = "salty"
                        ),

                        // Sweet
                        Product(
                            name = "Chebakia",
                            description = "Honey-coated flower-shaped cookies",
                            chefName = "Nadia",
                            price = 50.0,
                            imageResourceId = R.drawable.chebakia,
                            category = "sweet"
                        ),
                        Product(
                            name = "Moroccan Mint Tea",
                            description = "Traditional sweet mint tea",
                            chefName = "Samira",
                            price = 15.0,
                            imageResourceId = R.drawable.mint_tea,
                            category = "sweet"
                        ),

                        // Baked
                        Product(
                            name = "Msemen",
                            description = "Square-shaped Moroccan pancakes",
                            chefName = "Nadia",
                            price = 20.0,
                            imageResourceId = R.drawable.msemen,
                            category = "baked"
                        ),
                        Product(
                            name = "Moroccan Bread",
                            description = "Traditional round flatbread",
                            chefName = "Khadija",
                            price = 10.0,
                            imageResourceId = R.drawable.bread,
                            category = "baked"
                        )
                    )

                    db.productDao().insertAll(products)

                    // Mark as no longer first run
                    sharedPref.edit().putBoolean("is_first_run", false).apply()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error initializing database: ${e.message}")
                }
            }
        }
    }
}