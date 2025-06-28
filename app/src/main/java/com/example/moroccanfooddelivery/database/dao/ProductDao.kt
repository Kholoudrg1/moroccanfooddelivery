package com.example.moroccanfooddelivery.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moroccanfooddelivery.database.entities.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): List<Product>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): Product

    @Insert
    fun insertAll(products: List<Product>)

    @Query("SELECT * FROM products")
    fun getAllProducts(): List<Product>
}