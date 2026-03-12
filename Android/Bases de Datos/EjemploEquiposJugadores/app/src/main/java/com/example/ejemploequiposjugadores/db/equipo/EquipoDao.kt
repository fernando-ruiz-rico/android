package com.example.ejemploequiposjugadores.db.equipo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
  @Query("SELECT * FROM Equipo")
  fun getAll(): Flow<List<Equipo>>

  @Transaction // Cuando hace 2 o más consultas
  @Query("SELECT * FROM Equipo WHERE id = :id")
  fun getById(id: Int): Flow<EquipoConJugadores?>

  @Insert
  suspend fun insertEquipo(equipo: Equipo)

  @Delete
  suspend fun deleteEquipo(equipo: Equipo)

  @Query("DELETE FROM Equipo WHERE id = :id")
  suspend fun deleteEquipoById(id: Int)
}
