package com.example.ejemploproductos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  listaProductos: MutableList<Product>,
  goToProductDetail: (Int) -> Unit,
  removeProduct: (Int) -> Unit
) {
  Scaffold(topBar = {
    TopAppBar(
      title = { Text("Productos") },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
  }) { padding ->
    LazyColumn(Modifier.padding(padding)) {
      items(listaProductos) { producto ->
        ListItem(
          headlineContent = { Text(producto.name) },
          supportingContent = { Text("${producto.price.toString()}€") },
          trailingContent = {
            IconButton(onClick = { removeProduct(producto.id) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
          },
          modifier = Modifier.clickable { goToProductDetail(producto.id) }
        )
      }
    }
  }

}
