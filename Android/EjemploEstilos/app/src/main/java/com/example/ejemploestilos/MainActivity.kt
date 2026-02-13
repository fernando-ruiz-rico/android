package com.example.ejemploestilos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejemploestilos.ui.theme.EjemploEstilosTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploEstilosTheme {
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
  Column(modifier = modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(
      text = "Prueba",
      color = Color.White,
      modifier = Modifier
        .padding(12.dp)
        .background(Color.Blue)
    )
    Text(
      text = "Prueba",
      color = Color.White,
      modifier = Modifier
        .background(Color.Blue)
        .padding(12.dp)
    )
    Text(
      text = "Prueba",
      style = TextStyle(
        color = Color.White,
        fontSize = 12.sp,
        letterSpacing = 3.sp,
        fontWeight = FontWeight.ExtraBold,
        textDecoration = TextDecoration.Underline
      ),
      modifier = Modifier
        .clip(CircleShape)
        .background(Color.Blue)
        .padding(12.dp)
    )
    Button(
      onClick = {},
      colors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.secondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
      )
    ) {
    Text("Hola")
  }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploEstilosTheme {
    Greeting()
  }
}
