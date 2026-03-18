package com.example.ejemploequiposjugadores.db.jugador

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ejemploequiposjugadores.db.equipo.Equipo

@Entity(
  foreignKeys = [
      ForeignKey(
        entity = Equipo::class,
        parentColumns = ["id"], // Clave primaria del equipo
        childColumns = ["equipoId"], // Clave ajena del jugador
        onDelete = ForeignKey.CASCADE, // Cuando borramos el equipo, se borran los jugadores
      ),
  ],
  indices = [Index(value = ["equipoId"])] // Se recomienda crear un índice para cada clave ajena
)
data class Jugador(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val nombre: String,
  val equipoId: Int
)
