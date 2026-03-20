package com.example.ejemploproductos.model

class ProductInsert(
  val description: String,
  val price: String,
  val available: String?,
  val imageUrl: String,
)

data class Product(
  val id: Int,
  val description: String,
  val price: String,
  val available: String,
  val rating: Double,
  val imageUrl: String,
)


data class ProductListResponse(
  val products: List<Product>
)

data class SingleProductResponse(
  val product: Product
)

