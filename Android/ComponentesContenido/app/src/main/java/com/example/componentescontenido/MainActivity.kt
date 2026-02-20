package com.example.componentescontenido

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.componentescontenido.ui.theme.ComponentesContenidoTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ComponentesContenidoTheme {
        PantallaPrincipal()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier) {
  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
          Text("Aplicación")
        },
        actions = {
          MenuDesplegable()
        }
      )
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = modifier
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Spacer(Modifier.height(8.dp))
      CardsEjemplo()
    }
  }
}

@Composable
fun CardsEjemplo() {
  Card() {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = "Soy una tarjeta", style = MaterialTheme.typography.headlineMedium)
      Text(text = "Con un texto dentro")
    }
  }
  ElevatedCard() {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = "Soy una tarjeta", style = MaterialTheme.typography.headlineMedium)
      Text(text = "Con un texto dentro")
    }
  }

  OutlinedCard() {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = "Soy una tarjeta", style = MaterialTheme.typography.headlineMedium)
      Text(text = "Con un texto dentro")
    }
  }
  var selected by remember { mutableStateOf(false) }

  Card(
    onClick = { selected = !selected },
    colors = CardDefaults.cardColors(
      containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
      } else {
        MaterialTheme.colorScheme.surfaceContainer
      }
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = "Tarjeta interactiva")
    }
  }
}

@Composable
fun MenuDesplegable() {
  var expanded by remember { mutableStateOf(false) }
  Box {
    IconButton(onClick = { expanded = !expanded }) {
      Icon(Icons.Default.MoreVert, contentDescription = "More options")
    }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.width(200.dp),
      offset = DpOffset(0.dp, 8.dp)
    ) {
      DropdownMenuItem(
        leadingIcon = { Icon(Icons.Outlined.Save, contentDescription = null) },
        text = { Text("Guardar") },
        onClick = {
          /* Do something... */
          expanded = false
        }
      )
      DropdownMenuItem(
        leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
        text = { Text("Editar") },
        onClick = {
          /* Do something... */
          expanded = false
        }
      )
      HorizontalDivider()
      DropdownMenuItem(
        leadingIcon = { Icon(Icons.Outlined.Remove, contentDescription = null) },
        text = { Text("Eliminar") },
        onClick = {
          /* Do something... */
          expanded = false
        }
      )

    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesContenidoTheme {
    PantallaPrincipal()
  }
}
