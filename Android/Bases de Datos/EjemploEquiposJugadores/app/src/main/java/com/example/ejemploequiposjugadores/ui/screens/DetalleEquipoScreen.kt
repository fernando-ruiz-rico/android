package com.example.ejemploequiposjugadores.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.example.ejemploequiposjugadores.db.equipo.Equipo
import com.example.ejemploequiposjugadores.db.equipo.EquipoConJugadores
import com.example.ejemploequiposjugadores.db.jugador.Jugador
import com.example.ejemploequiposjugadores.viewmodels.DetalleEquipoUiState
import com.example.ejemploequiposjugadores.viewmodels.DetalleEquipoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEquipoScreen(
  viewModel: DetalleEquipoViewModel,
  goBack: () -> Unit = {}
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = uiState) {
    if (uiState is DetalleEquipoUiState.NoEncontrado) {
      goBack()
    }
  }

  when (val state = uiState) {
    is DetalleEquipoUiState.Cargando -> {
      Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Text("Cargando datos del equipo")
          CircularProgressIndicator(
            modifier = Modifier.width(64.dp).padding(12.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
          )
        }
      }
    }
    is DetalleEquipoUiState.Exito -> {
      Scaffold(topBar = {
        TopAppBar(
          title = { Text(state.data.equipo.nombre) },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
          navigationIcon = {
            IconButton(onClick = goBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          }
        )
      }) { paddingValues ->
        ListaJugadores(viewModel,state.data, Modifier.padding(paddingValues))
      }
    }
    else -> {}
  }
}

@Composable
fun ListaJugadores(viewModel: DetalleEquipoViewModel, equipo: EquipoConJugadores, modifier: Modifier = Modifier) {
  val textFieldState = rememberTextFieldState()

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(state = textFieldState, modifier = Modifier.weight(1f), label = { Text("Nombre del jugador") })
      Spacer(Modifier.width(12.dp))
      Button(onClick = {
        viewModel.insertJugador(
          Jugador(nombre = textFieldState.text.toString(), equipoId = equipo.equipo.id),
          onSuccess = { textFieldState.clearText() }
        )
      }) {
        Text("Añadir")
      }
    }
    HorizontalDivider(Modifier.fillMaxWidth())
    LazyColumn() {
      items(equipo.jugadores) { jugador ->
        ListItem(
          headlineContent = { Text(jugador.nombre) },
          trailingContent = {
            IconButton(onClick = { viewModel.deleteJugador(jugador.id) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
          }
        )
      }
    }
  }
}
