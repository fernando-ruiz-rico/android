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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PantallaHundirLaFlota()
                }
            }
        }
    }
}

@Composable
fun PantallaHundirLaFlota() {
    val motorJuego = remember { Juego() }
    var refrescar by remember { mutableIntStateOf(0) }

    motorJuego.iniciarPartida()
    refrescar++

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (f in 0 until motorJuego.DIMENSION) {
                Row {
                    for (c in 0 until motorJuego.DIMENSION) {
                        val estado = motorJuego.oceano[f][c]
                        var simbolo = estado.simbolo
                        /*if (estado == EstadoCasilla.BARCO && !motorJuego.juegoTerminado) {
                            simbolo = EstadoCasilla.AGUA.simbolo
                        }*/
                        Text(simbolo, fontSize = 18.sp)
                    }
                }
            }
        }
    }

    Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
}