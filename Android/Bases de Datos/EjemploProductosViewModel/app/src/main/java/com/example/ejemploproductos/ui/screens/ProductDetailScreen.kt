package com.example.ejemploproductos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejemploproductos.ui.theme.EjemploProductosTheme
import com.example.ejemploproductos.viewmodels.DetalleProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
  viewModel: DetalleProductoViewModel, goBack: () -> Unit = {}
) {
  val product by viewModel.product.collectAsState()

  Scaffold(topBar = {
    TopAppBar(
      title = { Text(product?.name ?: "Cargando producto...") },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      navigationIcon = {
        IconButton(onClick = goBack) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
      }
    )
  }) { padding ->
    Box(modifier = Modifier
      .padding(padding)
      .fillMaxSize(), contentAlignment = Alignment.Center) {
      Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          product?.let { product ->
            Text(product.name, style = MaterialTheme.typography.headlineMedium)
            Text(product.description)
            Text(
              "${product.price}€",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.tertiary
            )
          } ?: run {
            Text("Cargando producto...")
          }
        }
      }
    }
  }
}

