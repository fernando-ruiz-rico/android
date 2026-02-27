package com.example.ejemplomenulateral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.ejemplomenulateral.ui.theme.EjemploMenuLateralTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object Tasks : NavKey

@Serializable
data object Songs : NavKey

@Serializable
data object Photos : NavKey

enum class Links(
  val route: NavKey,
  val label: String,
  val icon: ImageVector,
  val contentDescription: String
) {
  TASKS(Tasks, "Tasks", Icons.Default.Task, "Home"),
  SONGS(Songs, "Songs", Icons.Default.Album, "Songs"),
  PHOTOS(Photos, "Photos", Icons.Default.Photo, "Photos")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjemploMenuLateralTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val backStack = rememberNavBackStack(Tasks)
  val scope = rememberCoroutineScope()

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      NavigationDrawerContent(drawerState, scope) { route ->
        backStack.clear()
        backStack.add(route)
      }
    }
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
          title = { Text("Ejemplo Menú Lateral") },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
          ),
          navigationIcon = {
            IconButton(onClick = {
              scope.launch { drawerState.open() }
            }) {
              Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
          }
        )
      }
    ) { padding ->
      NavigationRoot(backStack, Modifier.padding(padding))
    }
  }
}

@Composable
fun NavigationDrawerContent(state: DrawerState, scope: CoroutineScope, openPage: (NavKey) -> Unit) {
  var selectedDestination by rememberSaveable { mutableIntStateOf(Links.TASKS.ordinal) }

  ModalDrawerSheet {
    Column(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Spacer(Modifier.height(12.dp))
      Text(
        "Menú lateral",
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.titleLarge
      )
      HorizontalDivider()
      Spacer(Modifier.height(12.dp))
      Links.entries.forEachIndexed { index, destination ->
        NavigationDrawerItem(
          label = { Text(destination.label) },
          selected = selectedDestination == index,
          icon = {
            Icon(
              destination.icon,
              contentDescription = destination.contentDescription
            )
          },
          onClick = {
            openPage(destination.route)
            selectedDestination = index
            scope.launch { state.close() }
          }
        )
      }
    }
  }
}

@Composable
fun NavigationRoot(backStack: NavBackStack<NavKey>, modifier: Modifier) {
  NavDisplay(
    backStack = backStack,
    entryProvider = entryProvider {
      entry<Tasks> {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Tasks")
        }
      }
      entry<Songs> {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Songs")
        }
      }
      entry<Photos> {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Photos")
        }
      }
    })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EjemploMenuLateralTheme {
      MainScreen()
    }
}
