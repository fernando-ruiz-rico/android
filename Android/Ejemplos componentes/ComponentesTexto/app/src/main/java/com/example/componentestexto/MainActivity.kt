package com.example.componentestexto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.componentestexto.ui.theme.ComponentesTextoTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ComponentesTextoTheme {
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
  Column(modifier = modifier) {
    TextFieldClasico()
    TextFieldModerno()
    TextFieldCompleto()
  }
}

@Composable
fun TextFieldClasico() {
  var textValue by remember { mutableStateOf("") }

  TextField(
    value = textValue,
    onValueChange = { textValue = it },
    singleLine = true,
    label = { Text("Nombre") },
    placeholder = { Text("Ingrese su nombre") },
    modifier = Modifier.fillMaxWidth()
  )
  Text(text = textValue)
}

@Composable
fun TextFieldModerno() {
  val textState = rememberTextFieldState()

  TextField(
    state = textState,
    lineLimits = TextFieldLineLimits.SingleLine,
    label = { Text("Nombre") },
    placeholder = { Text("Ingrese su nombre") },
    modifier = Modifier.fillMaxWidth()
  )
  Text(text = "${textState.text}")
}

@Composable
fun TextFieldCompleto() {
  val textState = rememberTextFieldState()

  TextField(
    state = textState,
    placeholder = { Text("Caducidad de la tarjeta") },
    label = { Text("Caducidad") },
    lineLimits = TextFieldLineLimits.SingleLine,
    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
    trailingIcon = {
      if(textState.text.isNotEmpty()) {
        IconButton(onClick = { textState.clearText() }) {
          Icon(Icons.Default.Clear, contentDescription = "Clear text")
        }
      }
   },
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Number
    ),
    inputTransformation = InputTransformation.maxLength(4),
    outputTransformation = {
      if (length > 2) insert(2, "/") // Separador de mes/año
    },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesTextoTheme {
    Greeting()
  }
}
