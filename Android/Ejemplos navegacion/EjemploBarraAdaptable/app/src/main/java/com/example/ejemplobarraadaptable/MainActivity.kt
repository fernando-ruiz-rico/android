package com.example.ejemplobarraadaptable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.ejemplobarraadaptable.ui.theme.EjemploBarraAdaptableTheme
import kotlinx.serialization.Serializable

sealed interface Ruta: NavKey {
  @Serializable
  data object Home : Ruta
  @Serializable
  data object Favorites : Ruta
  @Serializable
  data object Shopping : Ruta
  @Serializable
  data object Profile : Ruta
}

enum class Rutas(
  val route: Ruta,
  val label: String,
  val icon: ImageVector,
  val contentDescription: String
) {
  HOME(Ruta.Home,"Home", Icons.Default.Home, "Home"),
  FAVORITES(Ruta.Favorites, "Favorites", Icons.Default.Favorite, "Favorites"),
  SHOPPING(Ruta.Shopping, "Shopping", Icons.Default.ShoppingCart, "Shopping"),
  PROFILE(Ruta.Profile, "Profile", Icons.Default.AccountBox, "Profile"),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjemploBarraAdaptableTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
  var currentDestination by rememberSaveable { mutableStateOf(Rutas.HOME) }
  val backStack = rememberNavBackStack(Ruta.Home)

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      Rutas.entries.forEach {
        item(
          icon = {
            Icon(
              it.icon,
              contentDescription = it.contentDescription
            )
          },
          label = { Text(it.label) },
          selected = it == currentDestination,
          onClick = {
            backStack.clear()
            backStack.add(it.route)
            currentDestination = it
          }
        )
      }
    }
  ) {
    Scaffold() { padding ->
      NavigationRoot(backStack, modifier = Modifier.padding(padding))
    }

  }
}

@Composable
fun NavigationRoot(backStack: NavBackStack<NavKey>, modifier: Modifier = Modifier) {
  NavDisplay(
    backStack = backStack,
    entryProvider = entryProvider {
      entry<Ruta.Home> {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Home")
        }
      }
      entry<Ruta.Favorites> {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Favorites")
        }
      }
      entry<Ruta.Shopping> {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Shopping")
        }
      }
      entry<Ruta.Profile> {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Profile")
        }
      }
    })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EjemploBarraAdaptableTheme {
      MainScreen()
    }
}
