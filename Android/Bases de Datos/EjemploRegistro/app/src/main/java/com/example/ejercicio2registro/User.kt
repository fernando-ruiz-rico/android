package com.example.ejercicio2registro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  var username: String,
  var password: String,
  var sex: String
)

