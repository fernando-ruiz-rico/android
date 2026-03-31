package com.example.ejemplospeeddial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejemplospeeddial.ui.theme.EjemploSpeedDialTheme
import com.example.ejemplospeeddial.ui.theme.Purple40
import com.example.ejemplospeeddial.ui.theme.PurpleGrey40

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploSpeedDialTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          floatingActionButton = { ExpandableFabMenu() }) { innerPadding ->
          Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun ExpandableFabMenu() {
  // 1. Estado para controlar si el menú está abierto o cerrado
  var isExpanded by remember { mutableStateOf(false) }

  // 2. Animación para rotar el icono principal (opcional pero le da un buen toque)
  val rotation by animateFloatAsState(
    targetValue = if (isExpanded) 45f else 0f,
    label = "fab_rotation"
  )

  val color by animateColorAsState(
    targetValue = if (isExpanded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
    label = "fab_color"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom,
    modifier = Modifier.padding(16.dp)
  ) {
    // 3. Los botones secundarios envueltos en AnimatedVisibility
    AnimatedVisibility(
      visible = isExpanded,
      enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
      exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre botones
        modifier = Modifier.padding(bottom = 16.dp)
      ) {
        // Botón secundario 1
        SmallFloatingActionButton(
          onClick = { /* Acción 1 */ isExpanded = false }
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Editar")
        }

        // Botón secundario 2
        SmallFloatingActionButton(
          onClick = { /* Acción 2 */ isExpanded = false }
        ) {
          Icon(Icons.Default.ShoppingCart, contentDescription = "Comprar")
        }
      }
    }

    // 4. El botón FAB principal
    FloatingActionButton(
      onClick = { isExpanded = !isExpanded },
      containerColor = color
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Expandir menú",
        modifier = Modifier.rotate(rotation) // Aplicamos la rotación
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploSpeedDialTheme {
    Greeting("Android")
  }
}
