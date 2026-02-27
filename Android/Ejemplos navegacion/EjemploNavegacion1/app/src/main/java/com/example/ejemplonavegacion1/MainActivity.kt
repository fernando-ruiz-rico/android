package com.example.ejemplonavegacion1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.ejemplonavegacion1.ui.theme.EjemploNavegacion1Theme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploNavegacion1Theme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          NavigationExample(Modifier.padding(innerPadding))
        }
      }
    }
  }
}

@Serializable
data object Page1 : NavKey

@Serializable
data class Page2(val nombre: String) : NavKey

@Composable
fun NavigationExample(modifier: Modifier = Modifier) {
  // Inicializamos la pila de navegación
  val backStack = rememberNavBackStack(Page1)

  NavDisplay(
    backStack = backStack,
    modifier = modifier,
    entryProvider = entryProvider {
      entry<Page1> { Pantalla1(goPage2 = { nombre -> backStack.add(Page2(nombre)) }) }
      entry<Page2> { key ->
        Pantalla2(name = key.nombre ,goBack = { backStack.removeLastOrNull() })
      }
    }
  )
}

@Composable
fun Pantalla1(modifier: Modifier = Modifier, goPage2: (nombre: String) -> Unit = {}) {
  val textState = rememberTextFieldState()

  Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(text = "Pantalla 1", style = MaterialTheme.typography.headlineMedium)
      OutlinedTextField(state = textState, label = { Text("Nombre") })
      Button(onClick = {
        goPage2(textState.text.toString())
        textState.clearText()
      }) {
        Text(text = "Ir a pantalla 2")
      }
    }
  }
}

@Composable
fun Pantalla2(modifier: Modifier = Modifier, name: String = "", goBack: () -> Unit = {}) {
  Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(text = "Pantalla 2", style = MaterialTheme.typography.headlineMedium)
      Text("Hola $name")
      Button(onClick = goBack) {
        Text(text = "Volver a pantalla 1")
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploNavegacion1Theme {
    NavigationExample()
  }
}
