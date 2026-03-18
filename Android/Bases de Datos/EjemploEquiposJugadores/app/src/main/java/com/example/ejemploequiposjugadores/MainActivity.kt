package com.example.ejemploequiposjugadores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.ejemploequiposjugadores.ui.screens.DetalleEquipoScreen
import com.example.ejemploequiposjugadores.ui.screens.ListaEquiposScreen
import com.example.ejemploequiposjugadores.ui.theme.EjemploEquiposJugadoresTheme
import com.example.ejemploequiposjugadores.viewmodels.DetalleEquipoViewModel
import com.example.ejemploequiposjugadores.viewmodels.ListaEquiposViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjemploEquiposJugadoresTheme {
                NavigationRoot()
            }
        }
    }
}

sealed interface Routes: NavKey {
  @Serializable data object ListaEquipos: Routes
  @Serializable data class DetalleEquipo(val equipoId: Int): Routes
}

@Composable
fun NavigationRoot() {
  val backStack = rememberNavBackStack(Routes.ListaEquipos)

  NavDisplay(
    backStack = backStack,
    entryDecorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator(), // Para que funcione rememberSaveable
      rememberViewModelStoreNavEntryDecorator()       // Para que los ViewModels nazcan y mueran
    ),
    entryProvider = entryProvider {
      entry<Routes.ListaEquipos> { route ->
        val viewModel: ListaEquiposViewModel = koinViewModel()
        ListaEquiposScreen(viewModel, goDetail = { backStack.add(Routes.DetalleEquipo(it)) })
      }
      entry<Routes.DetalleEquipo> { route ->
        val viewModel: DetalleEquipoViewModel = koinViewModel { parametersOf(route) }
        DetalleEquipoScreen(viewModel, goBack = { backStack.removeLastOrNull() })
      }
    }
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EjemploEquiposJugadoresTheme {
      NavigationRoot()
    }
}
