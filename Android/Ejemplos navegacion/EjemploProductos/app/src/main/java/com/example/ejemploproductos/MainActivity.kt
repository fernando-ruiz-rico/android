package com.example.ejemploproductos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.ejemploproductos.ui.theme.EjemploProductosTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploProductosTheme {
        MainScreen()
      }
    }
  }
}

sealed interface Link : NavKey {
  @Serializable
  data object Home : Link

  @Serializable
  data object AddProduct : Link

  @Serializable
  data class ProductDetail(val id: Int) : Link
}

enum class AppLink(
  val route: Link,
  val label: String,
  val icon: ImageVector,
  val contentDescription: String
) {
  HOME(Link.Home, "Home", Icons.Default.Home, "Home"),
  FAVORITES(Link.AddProduct, "Add Product", Icons.Default.AddCircle, "Add Product"),
}

data class Product(val id: Int, val name: String, val price: Double, val description: String)

@Composable
fun MainScreen() {
  var currentDestination by rememberSaveable { mutableStateOf<AppLink?>(AppLink.HOME) }
  val backStack = rememberNavBackStack(Link.Home)

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      AppLink.entries.forEach { appLink ->
        item(
          icon = {
            Icon(
              appLink.icon,
              contentDescription = appLink.contentDescription
            )
          },
          label = { Text(appLink.label) },
          selected = appLink == currentDestination,
          onClick = {
            backStack.clear()
            backStack.add(appLink.route)
            currentDestination = appLink
          }
        )
      }
    }
  ) {
    NavigationRoot(
      backStack,
      destinationChanged = { currentDestination = it })
  }
}

@Composable
fun NavigationRoot(
  backStack: NavBackStack<NavKey>,
  modifier: Modifier = Modifier,
  destinationChanged: (AppLink?) -> Unit = {}
) {
  val listaProductos = remember {
    mutableStateListOf<Product>(
      Product(1, "Silla", 50.0, "Silla ergonómica apta para exterior")
    )
  }
  var nextId by remember { mutableIntStateOf(2) }

  NavDisplay(
    modifier = modifier,
    backStack = backStack,
    entryProvider = entryProvider {
      entry<Link.Home> {
        HomeScreen(
          listaProductos,
          goToProductDetail = { id ->
            backStack.add(Link.ProductDetail(id))
            destinationChanged(null)
          },
          removeProduct = { id -> listaProductos.removeIf { it.id == id } })
      }
      entry<Link.AddProduct> {
        AddProductScreen(
          addProduct = { product ->
            listaProductos.add(product)
            nextId++
            backStack.removeLastOrNull()
            backStack.add(Link.Home)
            destinationChanged(AppLink.HOME)
          },
          nextId = nextId
        )
      }
      entry<Link.ProductDetail> { link ->
        ProductDetailScreen(
          listaProductos.first { it.id == link.id },
          {
            backStack.removeLastOrNull()
            destinationChanged(AppLink.HOME)
          })
      }
    })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploProductosTheme {
    MainScreen()
  }
}
