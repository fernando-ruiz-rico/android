package com.example.ejemplolistacompra

import android.app.Application
import androidx.room.Room
import kotlin.getValue

class MyApplication: Application() {
  // lazy: Solo se ejecuta la primera vez que alguien llama a "database"
  private val database by lazy {
    Room.databaseBuilder(
      this, // El contexto de la aplicación
      AppDatabase::class.java,
      "lista_compra_db"
    ).build()
  }

  // El repositorio global que usará toda la app
  val itemRepository by lazy {
    ItemRepository(database.itemDao())
  }
}
