package com.example.moroccanfooddelivery.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moroccanfooddelivery.database.entities.Order

@Dao
interface OrderDao {
    @Insert
    fun insertOrder(order: Order): Long

    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun getOrdersByUserId(userId: Int): List<Order>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Int): Order

    @Update
    fun updateOrder(order: Order)

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): List<Order>

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    fun updateOrderStatus(orderId: Int, status: String)
}