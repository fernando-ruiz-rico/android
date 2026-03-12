package com.example.ejemploequiposjugadores.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ejemploequiposjugadores.db.equipo.Equipo
import com.example.ejemploequiposjugadores.db.equipo.EquipoDao
import com.example.ejemploequiposjugadores.db.jugador.Jugador
import com.example.ejemploequiposjugadores.db.jugador.JugadorDao

@Database( entities = [Equipo::class, Jugador::class ], version = 1 )
abstract class AppDatabase: RoomDatabase() {
    abstract val equipoDao: EquipoDao
    abstract val jugadorDao: JugadorDao
}
