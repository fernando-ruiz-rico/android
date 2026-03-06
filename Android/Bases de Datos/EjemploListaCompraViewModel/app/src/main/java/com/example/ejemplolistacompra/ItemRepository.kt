package com.example.ejemplolistacompra

import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
  fun getAll(): Flow<List<Item>> = itemDao.getAll()
  suspend fun insert(item: Item) = itemDao.insert(item)
  suspend fun update(item: Item) = itemDao.update(item)
  suspend fun delete(item: Item) = itemDao.delete(item)
}
