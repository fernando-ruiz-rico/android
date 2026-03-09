package com.example.ejemploproductos.db.products

import kotlinx.coroutines.flow.Flow

class ProductRepository(val productDao: ProductDao) {
  fun getAll(): Flow<List<Product>> = productDao.getAll()
  fun getById(id: Int): Flow<Product> = productDao.getById(id)
  suspend fun delete(product: Product) = productDao.deleteById(product.id)
  suspend fun deleteById(id: Int) = productDao.deleteById(id)
  suspend fun insert(product: Product) = productDao.insert(product)
}
