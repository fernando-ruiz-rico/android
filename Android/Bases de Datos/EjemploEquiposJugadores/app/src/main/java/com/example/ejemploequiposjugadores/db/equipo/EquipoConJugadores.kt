package com.example.ejemploequiposjugadores.db.equipo

import androidx.room.Embedded
import androidx.room.Relation
import com.example.ejemploequiposjugadores.db.jugador.Jugador

data class EquipoConJugadores(
  @Embedded val equipo: Equipo, // Incluye las propiedades de Equipo
  @Relation(
    parentColumn = "id", // Clave primaria (Equipo)
    entityColumn = "equipoId" // Clave ajena (Jugador)
  )
  val jugadores: List<Jugador>
)
