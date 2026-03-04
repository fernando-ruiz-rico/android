package com.example.ejercicio2registro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
  @Query("SELECT * FROM user")
  suspend fun getAll(): List<User>

  @Insert
  suspend fun insert(user: User): Long
}
