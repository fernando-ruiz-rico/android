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
        Text("CONECTA 4", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (motorJuego.dificultadSeleccionada == null) {
            Text(motorJuego.mensaje, modifier = Modifier.padding(bottom = 16.dp))

            Dificultad.values().forEach { nivel ->
                Button(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        motorJuego.iniciarPartida(nivel)
                        refrescar++
                    }
                ) {
                    Text(nivel.descripcion)
                }
            }
        } else {
            Text(text = motorJuego.mensaje, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            Text(
                text = motorJuego.obtenerMapaComoTexto(),
                fontFamily = FontFamily.Monospace,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                modifier = Modifier.padding(vertical = 15.dp)
            )

            if (!motorJuego.juegoTerminado) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth())
                {
                    for (c in 0 until motorJuego.COLUMNAS) {
                        Button(
                            onClick = {
                                motorJuego.turno(c)
                                refrescar++
                            },
                            modifier = Modifier.width(35.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(c.toString())
                        }
                    }
                }
            }
            else {
                Button(onClick = {
                    motorJuego.dificultadSeleccionada = null
                    motorJuego.mensaje = "Selecciona dificultad"
                    refrescar++
                }) {
                    Text("Jugar otra vez")
                }
            }
        }

        Text(text = "", modifier = Modifier.size(if (refrescar > 0) 0.dp else 0.dp))
    }
}