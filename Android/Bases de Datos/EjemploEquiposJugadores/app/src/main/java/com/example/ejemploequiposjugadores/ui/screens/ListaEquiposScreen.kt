package com.example.ejemploequiposjugadores.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.example.ejemploequiposjugadores.db.equipo.Equipo
import com.example.ejemploequiposjugadores.viewmodels.ListaEquiposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEquiposScreen(viewModel: ListaEquiposViewModel, goDetail: (Int) -> Unit = {}) {
  val listaEquipos by viewModel.listaEquipos.collectAsStateWithLifecycle()

  val textFieldState = rememberTextFieldState()

  Scaffold(topBar = {
    TopAppBar(
      title = { Text("Lista de Equipos") },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
  }) { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(state = textFieldState, modifier = Modifier.weight(1f), label = { Text("Nombre del equipo") })
        Spacer(Modifier.width(12.dp))
        Button(onClick = {
          viewModel.insertEquipo(
            Equipo(nombre = textFieldState.text.toString()),
            onSuccess = { textFieldState.clearText() }
          )
        }) {
          Text("Añadir")
        }
      }
      HorizontalDivider(Modifier.fillMaxWidth())
      LazyColumn() {
        items(listaEquipos) { equipo ->
          ListItem(
            modifier = Modifier.clickable(onClick = dropUnlessResumed { goDetail(equipo.id) }),
            headlineContent = { Text(equipo.nombre) },
            trailingContent = {
              IconButton(onClick = { viewModel.deleteEquipo(equipo.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
              }
            }
          )
        }
      }
    }
  }
}
