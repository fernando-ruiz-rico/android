package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    PantallaDamas()
                }
            }
        }
    }
}

@Composable
fun PantallaDamas() {
    val motorJuego = remember { JuegoDamas() }
    var refrescar by remember { mutableIntStateOf(0) }

    LaunchedEffect(motorJuego.turnoActual) {
        if (motorJuego.turnoActual == Jugador.NEGRO && !motorJuego.juegoTerminado) {
            delay(500)
            //motorJuego.jugarOrdenador()
            refrescar++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("DAMAS", fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp))

        Text(motorJuego.mensaje, fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (fila in 0 until 8) {
                Row {
                    for (columna in 0 until 8) {
                        val fondoCasilla = when {
                            fila == motorJuego.filaSeleccionada && columna == motorJuego.columnaSeleccionada -> Color(0xFF81C784)
                            (fila + columna) % 2 != 0 -> Color(0xFFB58863)
                            else -> Color(0xFFF0D9B5)
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(fondoCasilla)
                                .clickable() {
                                    motorJuego.turno(fila, columna)
                                    refrescar++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val pieza = motorJuego.tablero[fila][columna]
                            Text(pieza.simbolo, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }

    Text(text = "", modifier = Modifier.size(if (refrescar > 0) 0.dp else 0.dp))
}