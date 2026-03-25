package com.example.ejemploproductos.db.products

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Product(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val name: String,
  val price: Double,
  val description: String
)

