package com.example.componentesaccion

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.componentesaccion.ui.theme.ComponentesAccionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComponentesAccionTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                  floatingActionButton = {
                    FloatingActionButton(onClick = { /* acción click */ }) {
                      Icon(Icons.Filled.Add, "Localized description")
                    }
                  }) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    BotonesNormales()
    Row() {
      BotonesIcono()
    }
    BotonesSegmentadosSingle()
    BotonesSegmentadosMultiple()
  }
}

@Composable
fun BotonesNormales() {
  Button(onClick = {}) {
    Icon(Icons.Filled.Add, "Add")
    Spacer(Modifier.width(8.dp))
    Text("Botón sólido")
  }
  Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
    Text("Botón sólido")
  }
  FilledTonalButton(onClick = { /* acción click */ }) {
    Text("Botón Tonal")
  }
  OutlinedButton(onClick = { /* acción click */ }) {
    Text("Botón con borde")
  }
  ElevatedButton(onClick = { /* acción click */ }) {
    Text("Botón elevado")
  }
  TextButton(onClick = { /* acción click */ }) {
    Text("Botón de texto")
  }
}

@Composable
fun BotonesIcono() {
  IconButton(onClick = { /*...*/ }) {
    Icon(Icons.Filled.Add, "Botón 1")
  }
  FilledIconButton(onClick = { /*...*/ }) {
    Icon(Icons.Filled.Add, "Botón 2")
  }
  FilledTonalIconButton(onClick = { /*...*/ }) {
    Icon(Icons.Filled.Add, "Botón 3")
  }
  OutlinedIconButton(onClick = { /*...*/ }) {
    Icon(Icons.Filled.Add, "Botón 4")
  }
}

@Composable
fun BotonesSegmentadosSingle() {
  var selectedIndex by remember { mutableIntStateOf(0) }
  val options = listOf("Pizza", "Hamburguesa", "Ensalada")

  SingleChoiceSegmentedButtonRow() {
    for (index in options.indices) {
      SegmentedButton(
        shape = SegmentedButtonDefaults.itemShape(
          index = index,
          count = options.size
        ),
        onClick = { selectedIndex = index },
        selected = index == selectedIndex,
        label = { Text(options[index]) }
      )
    }
  }
}

@Composable
fun BotonesSegmentadosMultiple() {
  val selectedOptions = remember {
    mutableStateListOf(true, false, true)
  }
  val options = listOf("Pizza", "Hamburguesa", "Ensalada")

  MultiChoiceSegmentedButtonRow {
    options.forEachIndexed { index, label ->
      SegmentedButton(
        shape = SegmentedButtonDefaults.itemShape(
          index = index,
          count = options.size
        ),
        checked = selectedOptions[index],
        onCheckedChange = { // Click en el botón
          selectedOptions[index] = !selectedOptions[index]
        },
        label = { Text(label) }
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesAccionTheme {
    Scaffold(modifier = Modifier.fillMaxSize(),
      floatingActionButton = {
        FloatingActionButton(onClick = { /* acción click */ }, shape = CircleShape) {
          Icon(Icons.Filled.Add, "Localized description")
        }
      }) { innerPadding ->
      Greeting(
        modifier = Modifier.padding(innerPadding)
      )
    }
  }
}
