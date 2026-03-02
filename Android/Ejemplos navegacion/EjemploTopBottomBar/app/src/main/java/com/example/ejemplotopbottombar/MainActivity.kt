package com.example.ejemplotopbottombar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.example.ejemplotopbottombar.ui.theme.EjemploTopBottomBarTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploTopBottomBarTheme {
        MainScreen()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
  val scrollBehaviorTop = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
  val scrollBehaviorBottom = BottomAppBarDefaults.exitAlwaysScrollBehavior(rememberBottomAppBarState());

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .nestedScroll(scrollBehaviorTop.nestedScrollConnection)
      .nestedScroll(scrollBehaviorBottom.nestedScrollConnection),
    topBar = {
      TopAppBar(
        title = { Text("Medium Bar") },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        scrollBehavior = scrollBehaviorTop,
        navigationIcon = {
          IconButton(onClick = {}) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
          }
        },
        actions = {
          IconButton(onClick = {}) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
          }
          IconButton(onClick = {}) {
            Icon(Icons.Default.Person, contentDescription = "Profile")
          }
        }
      )
    },
    bottomBar = {
      BottomAppBar(
        scrollBehavior = scrollBehaviorBottom,
        actions = {
          IconButton(onClick = { /* do something */ }) {
            Icon(Icons.Filled.Check, contentDescription = "Localized description")
          }
          IconButton(onClick = { /* do something */ }) {
            Icon(
              Icons.Filled.Edit,
              contentDescription = "Localized description",
            )
          }
          IconButton(onClick = { /* do something */ }) {
            Icon(
              Icons.Filled.Mic,
              contentDescription = "Localized description",
            )
          }
          IconButton(onClick = { /* do something */ }) {
            Icon(
              Icons.Filled.Image,
              contentDescription = "Localized description",
            )
          }
        },
        floatingActionButton = {
          FloatingActionButton(
            onClick = { /* do something */ },
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
          ) {
            Icon(Icons.Filled.Add, "Localized description")
          }
        }
      )
    }
    ) { innerPadding ->
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
      items(40) {
        ListItem(headlineContent = { Text("Item $it") })
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploTopBottomBarTheme {
    MainScreen()
  }
}
