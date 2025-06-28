package com.example.moroccanfooddelivery.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moroccanfooddelivery.database.entities.CartItem

@Dao
interface CartDao {
    @Insert
    fun insertCartItem(cartItem: CartItem): Long

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    fun getCartItemByProductId(productId: Int): CartItem?

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    fun updateQuantity(id: Int, quantity: Int)

    @Update
    fun updateCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): List<CartItem>

    @Query("DELETE FROM cart_items WHERE id = :id")
    fun deleteCartItem(id: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    fun removeCartItem(id: Int)

    @Query("DELETE FROM cart_items")
    fun clearCart()
}