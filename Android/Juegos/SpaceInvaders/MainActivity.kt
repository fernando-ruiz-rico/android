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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio de la pantalla
                    color = MaterialTheme.colorScheme.background // Pone el color de fondo por defecto del móvil
                ) {
                    PantallaJuego()
                }
            }
        }
    }
}

@Composable
fun PantallaJuego() {
    val motorJuego = remember { Juego() }
    var refrescar by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PUNTOS ${motorJuego.puntos} | VIDAS ${motorJuego.vidas} | OLEADA: ${motorJuego.numeroOleada}",
            fontSize = 16.sp
        )
        Text(
            text = "BOMBAS DISPONIBLES: ${motorJuego.bombasMasivas}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = motorJuego.obtenerMapaComoTexto(),
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = motorJuego.mensaje,
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                motorJuego.reiniciar()
                refrescar++
            }) { Text("REINICIAR")}
        }

        Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
    }
}