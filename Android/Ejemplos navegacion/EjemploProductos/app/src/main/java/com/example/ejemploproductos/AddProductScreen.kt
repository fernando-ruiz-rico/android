package com.example.ejemploproductos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(addProduct: (Product) -> Unit, nextId: Int) {
  val nameState = rememberTextFieldState("")
  val priceState = rememberTextFieldState("")
  val descriptionState = rememberTextFieldState("")

  Scaffold(topBar = {
    TopAppBar(
      title = { Text("Añadir Producto") },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
  }) { padding ->
    Column(
      Modifier
        .fillMaxWidth()
        .padding(padding)
        .padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        state = nameState,
        label = { Text("Product Name") })
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        state = descriptionState,
        label = { Text("Product Description") })
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        state = priceState,
        label = { Text("Product Price") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
      )
      Button(modifier = Modifier.fillMaxWidth(), onClick = {
        addProduct(
          Product(
            nextId,
            nameState.text.toString(),
            priceState.text.toString().toDouble(),
            descriptionState.text.toString()
          )
        )
      }) {
        Text("Añadir")
      }
    }
  }
}

