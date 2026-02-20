package com.example.componenteslistas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.componenteslistas.ui.theme.ComponentesListasTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ComponentesListasTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          EjemploLista(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun EjemploLista(modifier: Modifier = Modifier) {
  val itemList = (1..30).map { "Item $it" }

  LazyColumn(modifier = modifier) {
    item {
      ListItem(
        headlineContent = {
          Text(
            "Lista de elementos",
            style = MaterialTheme.typography.titleLarge,
          )
        },
        colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
      )
      HorizontalDivider()
    }

    items(itemList) { item ->
      ListItem(headlineContent = { Text(item) })
      HorizontalDivider()
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesListasTheme {
    EjemploLista()
  }
}
