package com.example.componentescontenido

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.ComponentDialog
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.componentescontenido.ui.theme.ComponentesContenidoTheme
import kotlinx.coroutines.launch

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
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Spacer(Modifier.height(8.dp))
      CardsEjemplo()
      Galeria()
      EjemploBottomSheet()
      EjemploAlert()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Galeria() {
  data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    val contentDescription: String
  )

  val carouselItems = remember {
    listOf(
      CarouselItem(0, R.drawable.casa1, "casa1"),
      CarouselItem(1, R.drawable.casa2, "casa2"),
      CarouselItem(2, R.drawable.casa3, "casa3"),
      CarouselItem(3, R.drawable.casa4, "casa4"),
      CarouselItem(4, R.drawable.casa5, "casa5"),
      CarouselItem(5, R.drawable.casa6, "casa6"),
      CarouselItem(6, R.drawable.casa7, "casa7"),
      CarouselItem(7, R.drawable.casa8, "casa8"),
    )
  }

  HorizontalUncontainedCarousel(
    state = rememberCarouselState { carouselItems.count() },
    itemWidth = 180.dp,
    itemSpacing = 8.dp,
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
  ) { i ->
    val item = carouselItems[i]
    Image(
      modifier = Modifier.maskClip(MaterialTheme.shapes.medium),
      painter = painterResource(id = item.imageResId),
      contentDescription = item.contentDescription,
      contentScale = ContentScale.Fit
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjemploBottomSheet() {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()

  Button(onClick = { scope.launch { sheetState.expand() } }) {
    Text("Mostrar BottomSheet")
  }

  if(sheetState.isVisible) {
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = {}) {
      val menuOptions = listOf("Compartir", "Editar", "Eliminar")

      Column() {
        menuOptions.forEach { option ->
          ListItem(modifier = Modifier.clickable(onClick = {
            scope.launch {
              // Resto de acciones al pulsar la opción
              sheetState.hide()
            }
          }), headlineContent = { Text(option) })
        }
      }
    }
  }
}

@Composable
fun EjemploAlert() {
  var show by remember { mutableStateOf(false) }
  var resultado by remember { mutableStateOf("") } // Información sobre el botón pulsado

  Button(onClick = { show = true }) {
    Text("Mostrar alerta")
  }
  Text("Resultado: $resultado")

  if(show) {
    AlertDialog(
      onDismissRequest = {
        show = false
        resultado = "Has pulsado fuera"
      },
      confirmButton = {
        TextButton(onClick = {
          show = false
          resultado = "Has pulsado OK"
        }) {
          Text("OK")
        }
      },
      dismissButton = {
        TextButton(onClick = {
          show = false
          resultado = "Has pulsado Cancelar"
        }) {
          Text("Cancelar")
        }
      },
      title = {
        Text("Título de la alerta")
      },
      text = {
        Text("Contenido de la alerta")
      },
      icon = {
        Icon(Icons.Outlined.Info, contentDescription = null)
      }
    )
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesContenidoTheme {
    PantallaPrincipal()
  }
}
