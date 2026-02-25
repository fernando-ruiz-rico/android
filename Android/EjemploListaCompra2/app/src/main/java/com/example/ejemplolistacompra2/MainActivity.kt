package com.example.ejemplolistacompra2

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ejemplolistacompra2.ui.theme.EjemploListaCompra2Theme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var showDialog by remember { mutableStateOf(false) }
      val products = remember { mutableStateListOf("Manzanas", "Peras") }

      EjemploListaCompra2Theme {
        Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
          FloatingActionButton(onClick = { showDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Añadir")
          }
        }) { innerPadding ->
          ListaCompra(modifier = Modifier.padding(innerPadding), products = products)

          if (showDialog) {
            AddProductDialog(
              onDismissRequest = { showDialog = false },
              addClick = { product ->
                products.add(product)
                showDialog = false
              })
          }
        }
      }
    }
  }
}

@Composable
fun ListaCompra(modifier: Modifier = Modifier, products: MutableList<String> = mutableListOf()) {
  var showConfirmDialog by remember { mutableStateOf(false) }
  var selectedProduct by remember { mutableStateOf("") }

  LazyColumn(modifier = modifier.fillMaxSize()) {
    items(products) { product ->
      ListItem(headlineContent = { Text(product) }, trailingContent = {
        IconButton(onClick = {
          selectedProduct = product
          showConfirmDialog = true
        }) {
          Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
      })
    }
  }

  if (showConfirmDialog) {
    ConfirmDialog(
      "¿Desea borrar el producto?",
      "Si borra el producto se perderá",
      onDismissRequest = { showConfirmDialog = false },
      confirmClick = {
        if(selectedProduct.isNotEmpty()) {
          products.remove(selectedProduct)
          selectedProduct = ""
        }
        showConfirmDialog = false
      })
  }

}

@Composable
fun AddProductDialog(onDismissRequest: () -> Unit = {}, addClick: (product: String) -> Unit = {}) {
  val textState = rememberTextFieldState()

  Dialog(onDismissRequest = onDismissRequest) {
    Column(
      modifier = Modifier
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp)
    ) {
      Text(
        "Añadir producto",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.secondary
      )
      Spacer(Modifier.height(16.dp))
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        state = textState,
        label = { Text("Producto") })
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = { addClick(textState.text.toString()) }) { Text("Añadir") }
        TextButton(onClick = onDismissRequest) { Text("Cancelar") }
      }
    }

  }
}

@Composable
fun ConfirmDialog(
  title: String,
  message: String,
  onDismissRequest: () -> Unit = {},
  confirmClick: () -> Unit = {}
) {
  AlertDialog(
    title = { Text(title) },
    text = { Text(message) },
    onDismissRequest = onDismissRequest,
    confirmButton = { TextButton(onClick = confirmClick) { Text("Sí") } },
    dismissButton = { TextButton(onClick = onDismissRequest) { Text("No") } }
  )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploListaCompra2Theme {
    ListaCompra()
  }
}
