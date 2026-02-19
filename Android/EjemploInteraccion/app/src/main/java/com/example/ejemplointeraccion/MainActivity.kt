package com.example.ejemplointeraccion

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejemplointeraccion.ui.theme.EjemploInteraccionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjemploInteraccionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {

  Column(modifier = modifier.fillMaxSize().padding(horizontal = 12.dp)) {
    Contador()
    HorizontalDivider()
    ListaCompra()
  }
}

@Composable
fun Contador() {
  var contador by remember { mutableIntStateOf(0) }

  Text(
    text = "$contador",
    modifier = Modifier
  )
  Button(onClick = { contador++ }) {
    Text(text = "Incrementar contador")
  }
}

@Composable
fun ListaCompra() {
  // 1. Estado de la lista (Observable)
  val productos = remember { mutableStateListOf<String>() }
  // 2. Estado del campo de texto (lo que el usuario escribe)
  var nombreProducto by remember { mutableStateOf("") }

  Text(text = "Lista de la Compra", style = MaterialTheme.typography.headlineMedium)
  Spacer(modifier = Modifier.height(16.dp))

  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    OutlinedTextField(
      value = nombreProducto,
      onValueChange = { nombreProducto = it },
      label = { Text("Nuevo producto") },
      modifier = Modifier.weight(1f)
    )

    Button(
      onClick = {
        if (nombreProducto.isNotBlank()) {
          productos.add(nombreProducto) // 3. Al añadir, la UI se actualiza sola
          nombreProducto = "" // Limpiamos el campo
        }
      },
      modifier = Modifier.padding(start = 8.dp)
    ) {
      Text("Añadir")
    }
  }

  Spacer(modifier = Modifier.height(16.dp))

  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(productos.size) {
      ItemProducto(productos[it]) { productos.removeAt(it) }
    }
  }
}

@Composable
fun ItemProducto(nombre: String, onDelete: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = nombre)
      IconButton(onClick = onDelete) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Borrar")
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EjemploInteraccionTheme {
        Greeting()
    }
}
