package com.example.ejemploproductos.repositories

import com.example.ejemploproductos.model.ProductInsert
import com.example.ejemploproductos.services.ProductService
import org.koin.core.annotation.Single

@Single
class ProductRepository(private  val productService: ProductService) {
  suspend fun getProducts() = productService.getProducts()
  suspend fun getProduct(id: Int) = productService.getProduct(id)
  suspend fun createProduct(request: ProductInsert) = productService.createProduct(request)
  suspend fun updateProduct(request: ProductInsert, id: Int) = productService.updateProduct(request, id)
  suspend fun deleteProduct(id: Int) = productService.deleteProduct(id)
}
