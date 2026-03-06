package com.example.ejemploproductos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ejemploproductos.db.products.Product
import com.example.ejemploproductos.viewmodels.ListaProductosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
  viewModel: ListaProductosViewModel,
  productInserted: () -> Unit,
) {
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
        val product = Product(
          name = nameState.text.toString(),
          price = priceState.text.toString().toDouble(),
          description = descriptionState.text.toString()
        )
        viewModel.addProduct(product)
        productInserted()
      }) {
        Text("Añadir")
      }
    }
  }
}

