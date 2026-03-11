package com.example.ejemploproductos.db.products

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class ProductRepository(private val productDao: ProductDao) { // ProductDao lo inyecta automáticamente
  fun getAll(): Flow<List<Product>> = productDao.getAll()
  fun getById(id: Int): Flow<Product> = productDao.getById(id)
  suspend fun delete(product: Product) = productDao.deleteById(product.id)
  suspend fun deleteById(id: Int) = productDao.deleteById(id)
  suspend fun insert(product: Product) = productDao.insert(product)
}
