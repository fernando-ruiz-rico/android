package com.example.ejemplotabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ejemplotabs.ui.theme.EjemploTabsTheme
import kotlinx.serialization.Serializable

@Serializable
data object Tasks : NavKey

@Serializable
data object Songs : NavKey

@Serializable
data object Photos : NavKey

enum class Tabs(
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
            EjemploTabsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationExample(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun NavigationExample(modifier: Modifier = Modifier) {
  val backStack = rememberNavBackStack(Tasks)

  AppTabs(modifier) { route ->
    backStack.clear()
    backStack.add(route)
  }

  NavigationRoot(backStack)
}

@Composable
fun AppTabs(modifier: Modifier, selectRoute: (NavKey) -> Unit) {
  // rememberSaveable también mantiene estado cuando la app entera se reconsutruye (girar pantalla)
  var selectedDestination by rememberSaveable { mutableIntStateOf(Tabs.TASKS.ordinal) }

  PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = modifier) {
    Tabs.entries.forEachIndexed { index, destination ->
      Tab(
        selected = selectedDestination == index,
        onClick = {
          selectRoute(destination.route)
          selectedDestination = index
        },
        icon = {
          Icon(
            destination.icon,
            contentDescription = destination.contentDescription
          )
        },
        text = {
          Text(
            text = destination.label,
          )
        }
      )
    }
  }
}

@Composable
fun NavigationRoot(backStack: NavBackStack<NavKey>) {
  NavDisplay(
    backStack = backStack,
    entryProvider = entryProvider {
      entry<Tasks> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Tasks")
        }
      }
      entry<Songs> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Songs")
        }
      }
      entry<Photos> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("Photos")
        }
      }
    })
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EjemploTabsTheme {

    }
}
