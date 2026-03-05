package com.example.ejemplolistacompra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val descripcion: String,
)
