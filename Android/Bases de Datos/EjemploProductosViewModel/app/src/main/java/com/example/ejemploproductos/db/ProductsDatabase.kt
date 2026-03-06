package com.example.ejemploproductos.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ejemploproductos.db.products.Product
import com.example.ejemploproductos.db.products.ProductDao

@Database(entities = [Product::class], version = 1)
abstract class ProductsDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
}
