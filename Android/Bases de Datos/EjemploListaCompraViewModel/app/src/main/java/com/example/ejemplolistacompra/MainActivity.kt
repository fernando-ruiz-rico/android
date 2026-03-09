package com.example.ejemplolistacompra

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejemplolistacompra.ui.theme.EjemploListaCompraTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploListaCompraTheme {
        Scaffold(
          topBar = {
            TopAppBar(
              colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
              ),
              title = {
                Text("Lista de la compra")
              }
            )
          },
          modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
          ListaCompra(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun ListaCompra(
  modifier: Modifier = Modifier,
  viewModel: ListaCompraViewModel = viewModel(factory = ListaCompraViewModel.Factory)
) {
  val listaItems by viewModel.listaItems.collectAsState()

  Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    FormularioCompra {
      viewModel.insert(Item(descripcion = it))
    }
    HorizontalDivider()
    ListaItems(listaItems) {
      viewModel.delete(it)
    }
  }
}

@Composable
fun FormularioCompra(onInsert: (String) -> Unit) {
  val textState = rememberTextFieldState("");

  Row(
    Modifier.padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    OutlinedTextField(
      state = textState,
      label = { Text("Producto") },
      modifier = Modifier.weight(1f)
    )
    Button(onClick = {
      onInsert(textState.text.toString())
      textState.clearText()
    }) {
      Text("Añadir")
    }
  }
}

@Composable
fun ListaItems(listItems: List<Item>, onDelete: (Item) -> Unit = {}) {
  LazyColumn {
    items(listItems) { item ->
      ListItem(headlineContent = { Text(item.descripcion) }, trailingContent = {
        IconButton(onClick = { onDelete(item) }) {
          Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
      })
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploListaCompraTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
          ),
          title = {
            Text("Lista de la compra")
          }
        )
      },
      modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
      ListaCompra(
        modifier = Modifier.padding(innerPadding)
      )
    }
  }
}
