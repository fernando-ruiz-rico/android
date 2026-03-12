package com.example.ejemploequiposjugadores.db.jugador

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JugadorDao {
  @Query("SELECT * FROM Jugador")
  fun getAll(): Flow<List<Jugador>>

  @Query("SELECT * FROM Jugador WHERE equipoId = :equipoId")
  fun getByEquipoId(equipoId: Int): Flow<List<Jugador>>

  @Query("SELECT * FROM Jugador WHERE id = :id")
  fun getById(id: Int): Flow<Jugador?>

  @Insert
  suspend fun insertJugador(jugador: Jugador)

  @Delete
  suspend fun deleteJugador(jugador: Jugador)

  @Query("DELETE FROM Jugador WHERE id = :id")
  suspend fun deleteJugadorById(id: Int)
}
