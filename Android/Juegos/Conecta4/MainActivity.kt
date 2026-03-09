package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================================
// PUNTO DE ENTRADA DE LA APP ANDROID
// ============================================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent conecta la lógica de UI de Jetpack Compose con la actividad de Android
        setContent {
            // MaterialTheme aplica los estilos básicos visuales del sistema (colores, tipografía)
            MaterialTheme {
                // Surface actúa como el fondo general de la aplicación ocupando todo el tamaño
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Llamamos a nuestra pantalla principal del juego
                    PantallaJuego()
                }
            }
        }
    }
}

// ============================================================================
// INTERFAZ DE USUARIO (Jetpack Compose)
// ============================================================================
@Composable
fun PantallaJuego() {
    val motorJuego = remember { Juego() }
    var refrescar by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("*** CONECTA 4 ***", fontSize = 24.sp, modifier = Modifier.padding(bottom =16.dp))
    }
}