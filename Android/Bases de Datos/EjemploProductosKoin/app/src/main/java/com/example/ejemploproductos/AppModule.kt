package com.example.ejemploproductos

import android.content.Context
import androidx.room.Room
import com.example.ejemploproductos.db.ProductsDatabase
import com.example.ejemploproductos.db.products.ProductDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module // Indicamos a Koin que es un módulo
@ComponentScan("com.example.ejemploproductos") // Paquete principal de la aplicación para que busque todo
@Configuration // Permite la búsqueda automática y registro de otros módulos
class AppModule {
  // Enseñamos a construir la Base de Datos
  // Koin inyectará el 'context' aquí automáticamente
  @Single
  fun provideDatabase(context: Context): ProductsDatabase {
    return Room.databaseBuilder(
      context,
      ProductsDatabase::class.java,
      "products_db"
    ).build()
  }

  // Enseñamos a sacar el DAO de la base de datos
  // El objeto de la conexión a la base de datos lo inyecta automáticamente
  @Single
  fun provideProductDao(database: ProductsDatabase): ProductDao {
    return database.productDao()
  }
}
