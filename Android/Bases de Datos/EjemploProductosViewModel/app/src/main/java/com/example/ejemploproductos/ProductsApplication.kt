package com.example.ejemploproductos

import android.app.Application
import androidx.room.Room
import com.example.ejemploproductos.db.ProductsDatabase
import com.example.ejemploproductos.db.products.ProductRepository
import kotlin.getValue

class ProductsApplication: Application() {
  // lazy: Solo se ejecuta la primera vez que alguien llama a "database"
  private val database by lazy {
    Room.databaseBuilder(
      this, // El contexto de la aplicación
      ProductsDatabase::class.java,
      "tareas_db"
    ).build()
  }

  // El repositorio global que usará toda la app
  val productRepository by lazy {
    ProductRepository(database.productDao())
  }
}
