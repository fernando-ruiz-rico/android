package com.example.ejemploproductos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ejemploproductos.viewmodels.ListaProductosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: ListaProductosViewModel,
  goToProductDetail: (Int) -> Unit,
) {
  val listaProductos by viewModel.listaProductos.collectAsState()
  val textoBusqueda by viewModel.textoBusqueda.collectAsState()

  val productsFiltered = listaProductos.filter { product ->
    product.name.uppercase().contains(textoBusqueda.uppercase())
  }

  Scaffold(topBar = {
    TopAppBar(
      title = { Text("Productos") },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
  }) { padding ->
    Column(Modifier.padding(padding)) {
      TextField(value = textoBusqueda, onValueChange = { viewModel.actualizarFiltro(it) },modifier = Modifier.fillMaxWidth(), placeholder = { Text("Filtrar productos")})
      LazyColumn() {
        items(productsFiltered) { product ->
          ListItem(
            headlineContent = { Text(product.name) },
            supportingContent = { Text("${product.price.toString()}€") },
            trailingContent = {
              IconButton(onClick = { viewModel.removeProduct(product.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
              }
            },
            modifier = Modifier.clickable { goToProductDetail(product.id) }
          )
        }
      }
    }

  }

}
