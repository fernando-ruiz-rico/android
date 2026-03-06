package com.example.ejemploproductos.db.products

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Query("SELECT * FROM product")
  fun getAll(): Flow<List<Product>>

  @Query("SELECT * FROM product WHERE id = :id")
  fun getById(id: Int): Flow<Product>

  @Query("DELETE FROM product WHERE id = :id")
  suspend fun deleteById(id: Int)

  @Insert
  suspend fun insert(product: Product)
}
