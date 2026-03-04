package com.example.ejemplolistacompra

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {
  @Query("SELECT * FROM item")
  suspend fun getAll(): List<Item>

  @Query("SELECT * FROM item WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): Item?

  @Insert
  suspend fun insert(item: Item): Long // Devuelve la id generada

  @Update
  suspend fun update(item: Item)

  @Delete
  suspend fun delete(item: Item)

  @Query("DELETE FROM item WHERE id = :id")
  suspend fun deleteById(id: Int)

  @Query("DELETE FROM item")
  suspend fun clear()
}
