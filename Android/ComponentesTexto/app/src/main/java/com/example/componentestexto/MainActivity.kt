package com.example.componentestexto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    TextFieldClasico()
    TextFieldModerno()
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
    placeholder = { Text("Ingrese su nombre") }
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
    placeholder = { Text("Ingrese su nombre") }
  )
  Text(text = "${textState.text}")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  ComponentesTextoTheme {
    Greeting("Android")
  }
}
