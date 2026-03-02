package com.example.ejercicio1lista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejercicio1lista.ui.theme.Ejercicio1ListaTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      Ejercicio1ListaTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          EjercicioLista(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun EjercicioLista(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(6.dp))
        .background(Color.Blue.copy(alpha = 0.2f))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = Color.Blue)
      Text(text = "Email", color = Color.Blue)
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(6.dp))
        .background(Color.Blue.copy(alpha = 0.2f))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(imageVector = Icons.Default.Delete, contentDescription = "Email", tint = Color.Blue)
      Text(text = "Delete", color = Color.Blue)
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(6.dp))
        .background(Color.Blue.copy(alpha = 0.2f))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(imageVector = Icons.Default.Call, contentDescription = "Email", tint = Color.Blue)
      Text(text = "Phone", color = Color.Blue)
    }
  }
}

@Preview(showBackground = true)
@Composable
fun EjercicioListaPreview() {
  Ejercicio1ListaTheme {
    EjercicioLista()
  }
}
