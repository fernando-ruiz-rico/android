package com.example.ejemplonavegacion2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.ejemplonavegacion2.ui.theme.EjemploNavegacion2Theme
import kotlinx.serialization.Serializable
import java.time.LocalDate

data class Tarea(val description: String, val date: LocalDate, val finished: Boolean)

@Serializable
data object Home : NavKey

@Serializable
data object AddTask : NavKey

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploNavegacion2Theme {
        NavigationExample()
      }
    }
  }
}

@Composable
fun NavigationExample(modifier: Modifier = Modifier) {
  // Inicializamos la pila de navegación
  val backStack = rememberNavBackStack(Home)
  val listaTareas = remember {
    mutableStateListOf<Tarea>(
      Tarea("Sacar al perro", LocalDate.of(2026, 2, 28), false)
    )
  }

  NavDisplay(
    backStack = backStack,
    modifier = modifier,
    entryProvider = entryProvider {
      entry<Home> { HomeScreen(listaTareas, navigateAddTask = { backStack.add(AddTask) }) }
      entry<AddTask> { AddTaskScreen(listaTareas, goBack = { backStack.removeLastOrNull() }) }
    }
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploNavegacion2Theme {
    NavigationExample()
  }
}
