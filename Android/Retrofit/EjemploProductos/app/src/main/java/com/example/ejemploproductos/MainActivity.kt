package com.example.ejemploproductos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ejemploproductos.ui.theme.EjemploProductosTheme
import com.example.ejemploproductos.viewmodels.ProductUiState
import com.example.ejemploproductos.viewmodels.ProductsViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploProductosTheme {
        ProductsScreen()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen() {
  val viewModel: ProductsViewModel = koinViewModel()
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(topBar = {
    TopAppBar(
      title = { Text("Lista de Productos") },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
  }) { paddingValues ->
    when (uiState) {
      is ProductUiState.Loading -> Text("Cargando...", modifier = Modifier.padding(paddingValues))
      is ProductUiState.Success -> {
        val products = (uiState as ProductUiState.Success).products
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
          items(products) {
            ListItem(
              headlineContent = {
                Column() {
                  Text(it.description)
                  Text(it.price + "€", style = MaterialTheme.typography.labelSmall)
                }
              }, leadingContent = {
                AsyncImage(
                  model = ImageRequest.Builder(LocalContext.current)
                    .data(it.imageUrl.replace("http", "https"))
                    .crossfade(true) // Animación suave al aparecer
                    .build(),
                  contentDescription = "Foto del producto ${it.description}",
                  contentScale = ContentScale.Crop, // Recorta la imagen para que llene el espacio
                  modifier = Modifier
                    .size(64.dp) // Tamaño de la imagen
                    .clip(RectangleShape)
                )
              },
              trailingContent = {
                IconButton(onClick = { viewModel.deleteEquipo(it.id) }) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
              })
          }
        }
      }

      is ProductUiState.Error -> Text(
        (uiState as ProductUiState.Error).message,
        modifier = Modifier.padding(paddingValues)
      )
    }

  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploProductosTheme {
    ProductsScreen()
  }
}
