package com.example.moroccanfooddelivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val chefName: String,
    val price: Double,
    val imageResourceId: Int,
    val category: String // "bestseller", "salty", "sweet", "baked"
)