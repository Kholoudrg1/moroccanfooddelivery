package com.example.moroccanfooddelivery.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moroccanfooddelivery.database.entities.OrderItem

@Dao
interface OrderItemDao {
    @Insert
    fun insertOrderItem(orderItem: OrderItem): Long

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsByOrderId(orderId: Int): List<OrderItem>
}