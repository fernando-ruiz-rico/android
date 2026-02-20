package com.example.componentesseleccion

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.componentesseleccion.ui.theme.ComponentesSeleccionTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ComponentesSeleccionTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
  Column(modifier = modifier.padding(horizontal = 8.dp)) {
    CheckboxSwitchEjemplo()
    HorizontalDivider()
    RadioButtonEjemplo()
    HorizontalDivider()
    ChipsEjemplo()
    HorizontalDivider()
    SliderEjemplo()
  }
}

@Composable
fun CheckboxSwitchEjemplo() {
  var checked by remember { mutableStateOf(false) }

  Row(
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      "¿Aceptas condiciones?"
    )
    Checkbox(
      checked = checked,
      onCheckedChange = { checked = it }
    )
  }

  ListItem(
    headlineContent = { Text("¿Aceptas condiciones?") },
    leadingContent = { Icon(Icons.Default.Info, "Info") },
    trailingContent = { Checkbox(checked = checked, onCheckedChange = null) },
    modifier = Modifier.selectable(
      selected = checked,
      onClick = { checked = !checked },
      role = Role.Checkbox
    )
  )

  ListItem(
    headlineContent = { Text("¿Aceptas condiciones?") },
    leadingContent = { Icon(Icons.Default.Info, "Info") },
    trailingContent = { Switch(checked = checked, onCheckedChange = null) },
    modifier = Modifier.selectable(
      selected = checked,
      onClick = { checked = !checked },
      role = Role.Checkbox
    )
  )

  Text(
    if (checked) "Has dicho que sí" else "No aceptas condiciones"
  )
}

@Composable
fun RadioButtonEjemplo() {
  val radioOptions = listOf("Pizza", "Hamburguesa", "Ensalada")
  var selectedOption by remember { mutableStateOf("Pizza") }

  Column(Modifier.selectableGroup()) {
    radioOptions.forEach { text ->
      ListItem(
        headlineContent = { Text(text) },
        trailingContent = { RadioButton(selected = text == selectedOption, onClick = null) },
        modifier = Modifier.selectable(
          selected = text == selectedOption,
          onClick = { selectedOption = text },
          role = Role.RadioButton
        )
      )
    }
  }
}

@Composable
fun ChipsEjemplo() {
  var times by remember { mutableIntStateOf(0) }
  var selected by remember { mutableStateOf(false) }

  AssistChip(
    onClick = { times++ },
    label = { Text("Assist chip") },
    leadingIcon = {
      Icon(
        Icons.Filled.Settings,
        contentDescription = "Localized description",
        Modifier.size(AssistChipDefaults.IconSize)
      )
    }
  )
  Text("Veces presionado: $times")
  FilterChip(
    onClick = { selected = !selected },
    label = {
      Text("Filter chip")
    },
    selected = selected,
    leadingIcon = if (selected) {
      {
        Icon(
          imageVector = Icons.Filled.Done,
          contentDescription = "Done icon",
          modifier = Modifier.size(FilterChipDefaults.IconSize)
        )
      }
    } else {
      null
    },
  )
}

@Composable
fun SliderEjemplo() {
  var sliderPosition by remember { mutableFloatStateOf(0f) }
  Row(
    modifier = Modifier.fillMaxWidth().height(56.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Icon(Icons.Filled.Star, "Low", Modifier.size(12.dp))
    Slider(
      value = sliderPosition,
      onValueChange = { sliderPosition = it },
      modifier = Modifier.weight(1f),
      valueRange = 0f..10f,
      steps = 19
    )
    Icon(Icons.Filled.Star, "High")
  }
  Text(text = sliderPosition.toString())
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesSeleccionTheme {
    Greeting()
  }
}
