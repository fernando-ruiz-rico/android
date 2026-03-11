package com.example.ejemploequiposjugadores.db.equipo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Equipo(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val nombre: String
)
