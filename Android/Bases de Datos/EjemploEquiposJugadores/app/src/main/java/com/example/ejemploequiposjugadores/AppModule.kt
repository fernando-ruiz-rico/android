package com.example.ejemploequiposjugadores

import android.content.Context
import androidx.room.Room
import com.example.ejemploequiposjugadores.db.AppDatabase
import com.example.ejemploequiposjugadores.db.equipo.EquipoDao
import com.example.ejemploequiposjugadores.db.jugador.JugadorDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module // Indicamos a Koin que es un módulo
@ComponentScan("com.example.ejemploequiposjugadores") // Paquete principal de la aplicación para que busque todo
@Configuration // Permite la búsqueda automática y registro de otros módulos
class AppModule {
  @Single
  fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      "products_db"
    ).build()
  }

  @Single
  fun provideEquipoDao(database: AppDatabase): EquipoDao {
    return database.equipoDao
  }

  @Single
  fun provideJugadorDao(database: AppDatabase): JugadorDao {
    return database.jugadorDao
  }


}
