package com.example.ejemploproductos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.ejemploproductos.ui.screens.AddProductScreen
import com.example.ejemploproductos.ui.screens.HomeScreen
import com.example.ejemploproductos.ui.screens.ProductDetailScreen
import com.example.ejemploproductos.ui.theme.EjemploProductosTheme
import com.example.ejemploproductos.viewmodels.DetalleProductoViewModel
import com.example.ejemploproductos.viewmodels.ListaProductosViewModel
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
  val context = LocalContext.current
  val application = context.applicationContext as ProductsApplication
  // Este ViewModel se crea de forma global y no se destruye al navegar a otra pantalla
  val listaProductosViewModel: ListaProductosViewModel =
    viewModel(factory = ListaProductosViewModel.Factory(application))

  NavDisplay(
    modifier = modifier,
    backStack = backStack,
    entryDecorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator(), // Para que funcione rememberSaveable
      rememberViewModelStoreNavEntryDecorator()       // Para que los ViewModels nazcan y mueran
    ),
    entryProvider = entryProvider {
      entry<Link.Home> {
        HomeScreen(
          listaProductosViewModel,
          goToProductDetail = { id ->
            backStack.add(Link.ProductDetail(id))
            destinationChanged(null)
          })
      }
      entry<Link.AddProduct> {
        AddProductScreen(
          listaProductosViewModel,
          productInserted = {
            backStack.removeLastOrNull()
            backStack.add(Link.Home)
            destinationChanged(AppLink.HOME)
          }
        )
      }
      entry<Link.ProductDetail> { link ->
        val detalleProductoViewModel: DetalleProductoViewModel =
          viewModel(factory = DetalleProductoViewModel.Factory(application, link.id))

        ProductDetailScreen(
          detalleProductoViewModel,
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
