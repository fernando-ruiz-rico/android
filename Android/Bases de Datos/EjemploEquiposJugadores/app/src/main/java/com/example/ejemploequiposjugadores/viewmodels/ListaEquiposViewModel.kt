package com.example.ejemploequiposjugadores.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ejemploequiposjugadores.db.equipo.Equipo
import com.example.ejemploequiposjugadores.db.equipo.EquipoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ListaEquiposViewModel(private val equipoRepository: EquipoRepository) : ViewModel() {
  val listaEquipos: StateFlow<List<Equipo>> = equipoRepository.getAll().stateIn(
    viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
  )

  // Resto de métodos para insertar y borrar equipos
}
