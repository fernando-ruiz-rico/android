package com.example.ejemploproductos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejemploproductos.ui.theme.EjemploProductosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: Product, goBack: () -> Unit = {}) {
  Scaffold(topBar = {
    TopAppBar(
      title = { Text(product.name) },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      navigationIcon = {
        IconButton(onClick = goBack ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
      }
    )
  }) { padding ->
    Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
      Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(product.name, style = MaterialTheme.typography.headlineMedium)
          Text(product.description)
          Text("${product.price}€", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
  EjemploProductosTheme {
    ProductDetailScreen(Product(1, "Silla", 50.0, "Silla ergonómica apta para exterior"))
  }
}
