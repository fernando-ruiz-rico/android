package com.example.ejemploequiposjugadores.db.equipo

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class EquipoRepository(private val equipoDao: EquipoDao) {
    fun getAll(): Flow<List<Equipo>> = equipoDao.getAll()
    fun getById(id: Int): Flow<EquipoConJugadores?> = equipoDao.getById(id)
    suspend fun insertEquipo(equipo: Equipo) = equipoDao.insertEquipo(equipo)
    suspend fun deleteEquipo(equipo: Equipo) = equipoDao.deleteEquipo(equipo)
    suspend fun deleteEquipoById(id: Int) = equipoDao.deleteEquipoById(id)
}
