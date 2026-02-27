package com.example.componentescomunicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.componentescomunicacion.ui.theme.ComponentesComunicacionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ComponentesComunicacionTheme {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          snackbarHost = { SnackbarHost(snackbarHostState) },
          floatingActionButton = {
            ExtendedFloatingActionButton(
              text = { Text("Show snackbar") },
              icon = { Icon(Icons.Filled.Info, contentDescription = "") },
              onClick = {
                scope.launch {
                  snackbarHostState.showSnackbar("Snackbar", duration = SnackbarDuration.Short)
                }
              }
            )
          }) { innerPadding ->
          Greeting(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxSize()) {
    ShowBadges()
    ShowProgress()
  }
}

@Composable
fun ShowBadges() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Icon(
      Icons.Default.Email,
      contentDescription = "Mensajes",
      tint = MaterialTheme.colorScheme.secondary
    )
    Text("Mensajes", color = MaterialTheme.colorScheme.secondary)
    Badge { Text("28") }
  }

  ListItem(
    leadingContent = { Icon(Icons.Default.Email, contentDescription = "Mensajes") },
    headlineContent = { Text("Mensajes") },
    trailingContent = { Badge { Text("28") } })

  ListItem(
    leadingContent = {
      BadgedBox(
        badge = { Badge { Text("28") } },
        content = {
          Icon(Icons.Default.Email, contentDescription = "Mensajes")
        })
    },
    headlineContent = { Text("Mensajes") })
}

@Composable
fun ShowProgress() {
  var showProgress by remember { mutableStateOf(false) }

  LinearProgressIndicator(
    progress = { 0.5f },
    modifier = Modifier.fillMaxWidth().padding(12.dp),
  )
  LinearProgressIndicator(
    modifier = Modifier.fillMaxWidth().padding(12.dp),
  )
  CircularProgressIndicator(
    progress = { 0.5f },
    modifier = Modifier.width(64.dp).padding(12.dp)
  )
  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(64.dp)) {
    Button(onClick = { showProgress = !showProgress }) {
      Text(text = if (showProgress) "Ocultar" else "Mostrar")
    }
    if(showProgress) {
      CircularProgressIndicator(
        modifier = Modifier.width(64.dp).padding(12.dp),
      )
    }
  }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesComunicacionTheme {
    Greeting()
  }
}
