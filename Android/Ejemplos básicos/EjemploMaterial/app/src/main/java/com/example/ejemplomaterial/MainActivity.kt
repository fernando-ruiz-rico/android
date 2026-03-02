package com.example.ejemplomaterial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejemplomaterial.ui.theme.EjemploMaterialTheme
import com.example.ejemplomaterial.ui.theme.ExtendedTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploMaterialTheme {
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
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Hello $name!",
      color = MaterialTheme.colorScheme.primary,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(8.dp)
    )
    Text(
      text = "Hello $name!",
      color = MaterialTheme.colorScheme.primary,
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    )
    Text(
      text = "Hello $name!",
      color = MaterialTheme.colorScheme.secondary,
      modifier = Modifier
        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .padding(8.dp)
    )
    Text(
      text = "Hello $name!",
      color = MaterialTheme.colorScheme.tertiary,
      modifier = Modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.tertiaryContainer)
        .padding(8.dp)
    )
    Text(
      text = "Hello $name!",
      color = MaterialTheme.colorScheme.error,
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(MaterialTheme.colorScheme.errorContainer)
        .padding(8.dp)
    )
    Text(
      text = "Hello $name!",
      color = ExtendedTheme.colors.morado.color,
      modifier = Modifier
        .clip(CutCornerShape(6.dp))
        .background(ExtendedTheme.colors.morado.colorContainer)
        .padding(8.dp)
    )

  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploMaterialTheme {
    Greeting("Android")
  }
}
