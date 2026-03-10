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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("HUNDIR LA FLOTA", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (motorJuego.juegoTerminado && motorJuego.misilesRestantes == motorJuego.MUNICION_MAXIMA) {
            Text(motorJuego.mensaje, modifier = Modifier.padding(bottom = 16.dp))

            Button(onClick = {
                motorJuego.iniciarPartida()
                refrescar++
            }) {
                Text("Iniciar partida")
            }
        }
        else {
            Text(motorJuego.mensaje, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Text("Misiles: ${motorJuego.misilesRestantes} | Aciertos: ${motorJuego.aciertos}/${motorJuego.impactosNecesarios} ")

            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (f in 0 until motorJuego.DIMENSION) {
                    Row {
                        for (c in 0 until motorJuego.DIMENSION) {
                            val estado = motorJuego.oceano[f][c]
                            var simbolo = estado.simbolo
                            if (estado == EstadoCasilla.BARCO && !motorJuego.juegoTerminado) {
                                simbolo = EstadoCasilla.AGUA.simbolo
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .padding(1.dp)
                                    .background(Color.LightGray)
                                    .clickable(enabled = !motorJuego.juegoTerminado) {
                                        motorJuego.turno(f, c)
                                        refrescar++
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(simbolo, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (motorJuego.juegoTerminado) {
                Button(onClick = {
                    motorJuego.iniciarPartida()
                    refrescar++
                }) {
                    Text("Jugar otra vez")
                }
            }
        }
    }

    Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
}