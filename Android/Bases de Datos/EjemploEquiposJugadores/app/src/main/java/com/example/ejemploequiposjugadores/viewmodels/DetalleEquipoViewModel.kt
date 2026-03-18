package com.example.ejemploequiposjugadores.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejemploequiposjugadores.Routes
import com.example.ejemploequiposjugadores.db.equipo.Equipo
import com.example.ejemploequiposjugadores.db.equipo.EquipoConJugadores
import com.example.ejemploequiposjugadores.db.equipo.EquipoRepository
import com.example.ejemploequiposjugadores.db.jugador.Jugador
import com.example.ejemploequiposjugadores.db.jugador.JugadorRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed interface DetalleEquipoUiState {
  data object Cargando : DetalleEquipoUiState
  data class Exito(val data: EquipoConJugadores) : DetalleEquipoUiState
  data object NoEncontrado : DetalleEquipoUiState
}

@KoinViewModel
class DetalleEquipoViewModel(
  private val ruta: Routes.DetalleEquipo,
  private val equipoRepository: EquipoRepository,
  private val jugadorRepository: JugadorRepository,
): ViewModel() {
  @OptIn(FlowPreview::class)
  val uiState: StateFlow<DetalleEquipoUiState> = equipoRepository.getById(ruta.equipoId)
    .map { equipo ->
      // Cuando la BD responde, evaluamos:
      if (equipo != null) {
        DetalleEquipoUiState.Exito(equipo)
      } else {
        DetalleEquipoUiState.NoEncontrado
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = DetalleEquipoUiState.Cargando // ¡Magia! Ahora empezamos en 'Cargando'
    )

  fun insertJugador(jugador: Jugador, onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
    viewModelScope.launch {
      try {
        jugadorRepository.insertJugador(jugador.copy(equipoId = ruta.equipoId))
        onSuccess()
      } catch (e: Exception) {
        onError()
      }
    }
  }

  fun deleteJugador(id: Int) {
    viewModelScope.launch { jugadorRepository.deleteJugadorById(id) }
  }
}
