package com.example.ejemplobase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejemplobase.ui.theme.EjemploBaseTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      EjemploBaseTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Greeting(
            name = "EOI",
            modifier = Modifier.padding(innerPadding)
          )
//          EjercicioClase1(Modifier.padding(innerPadding))
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Hello $name!",
      color = Color.Green
    )
    Text(
      text = "Bye $name!",
      color = Color.Blue
    )
    Text(
      text = "Otro texto",
      color = Color.Red
    )
    Row() {
      Text(
        text = "Elem1",
      )
      Spacer(Modifier.width(20.dp))
      Text(
        text = "Elem2",
      )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(
        text = "Elem3",
      )
      Text(
        text = "Elem4",
      )
    }
    Box(modifier = Modifier.size(200.dp).background(Color.Blue).padding(12.dp)) {
      Text(text = "Hola", color = Color.White)
      Text(text = "Adiós", color = Color.White, modifier = Modifier.align(Alignment.Center))
      Text(text = "Otro", color = Color.White, modifier = Modifier.align(Alignment.BottomEnd))
    }
  }
}

@Composable
fun EjercicioClase1(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
      Text(text = "Texto 1")
      Text(text = "Texto 2")
      Text(text = "Texto 3")
    }
    Text(text = "Texto 4")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(text = "Texto 5")
      Text(text = "Texto 6")
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EjemploBaseTheme {
    Greeting("Android")
//    EjercicioClase1()
  }
}
