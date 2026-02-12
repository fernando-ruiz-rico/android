package com.example.ejemploimagenes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejemploimagenes.ui.theme.EjemploImagenesTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploImagenesTheme {
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
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Image(painter = painterResource(id = R.drawable.casa7), contentDescription = "Casa")
    Text(
      text = "Imagen casa",
      color = Color.White,
      modifier = Modifier
        .clip(CircleShape)
        .background(Color.Black.copy(alpha = 0.7f))
        .padding(12.dp)
    )
    Row(modifier = Modifier.align(Alignment.TopEnd).offset(x = (-10).dp, y = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      Icon(
        imageVector = Icons.Default.AddCircle,
        contentDescription = "Add",
        tint = Color.White
      )
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Add",
        tint = Color.White
      )
    }

  }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploImagenesTheme {
    Greeting()
  }
}
