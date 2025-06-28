package com.example.moroccanfooddelivery.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moroccanfooddelivery.database.entities.User

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): User

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>
}