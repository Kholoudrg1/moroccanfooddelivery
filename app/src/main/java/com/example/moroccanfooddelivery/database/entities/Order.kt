package com.example.moroccanfooddelivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val totalAmount: Double,
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "Pending" // Pending, Confirmed, Delivered
)