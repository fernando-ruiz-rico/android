package com.example.ejercicio2registro

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

  // 2. Definimos las funciones abstractas para obtener los DAOs
  abstract fun userDao(): UserDao

  // 3. El "compañero" que guarda la instancia única (Singleton)
  companion object {
    // @Volatile asegura que los cambios en esta variable sean visibles
    // inmediatamente para otros hilos (evita problemas de concurrencia)
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      // Si la instancia ya existe, la devuelve.
      // Si es null, entra al bloque synchronized para crearla.
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext, // Usamos el contexto de la App, no el de la Activity
          AppDatabase::class.java,
          "myapp_database" // El nombre físico del archivo de la base de datos
        ).build()

        INSTANCE = instance
        instance
      }
    }
  }
}
