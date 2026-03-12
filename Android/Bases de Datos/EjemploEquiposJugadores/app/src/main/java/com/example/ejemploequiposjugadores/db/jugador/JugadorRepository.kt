package com.example.ejemploequiposjugadores.db.jugador

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class JugadorRepository(private val jugadorDao: JugadorDao) {
  fun getAll(): Flow<List<Jugador>> = jugadorDao.getAll()
  fun getByEquipoId(equipoId: Int): Flow<List<Jugador>> = jugadorDao.getByEquipoId(equipoId)
  fun getById(id: Int): Flow<Jugador?> = jugadorDao.getById(id)

  suspend fun insertJugador(jugador: Jugador) = jugadorDao.insertJugador(jugador)
  suspend fun deleteJugador(jugador: Jugador) = jugadorDao.deleteJugador(jugador)
  suspend fun deleteJugadorById(id: Int) = jugadorDao.deleteJugadorById(id)
}
