package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val fondo = Brush.verticalGradient(
                colors = listOf(Color(0xFFE3F2FD), Color(0xFF64B5F6))
            )

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().background(fondo),
                    color = Color.Transparent
                ) {
                    PantallaMochis()
                }
            }
        }
    }
}

@Composable
fun PantallaMochis() {
    val motor = remember { MotorMochis() }
    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }
    var contadorFotogramas by remember { mutableStateOf(0) }
    val medidorDeTexto = rememberTextMeasurer()

    LaunchedEffect(Unit) {
        while(true) {
            withFrameNanos {
                if (tamanyoPantalla != IntSize.Zero) {
                    /*motor.actualizarFisicas(
                        anchoPantalla = tamanyoPantalla.width.toFloat(),
                        altoPantalla = tamanyoPantalla.height.toFloat()
                    )*/
                    contadorFotogramas++
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        nuevoTamanyo -> tamanyoPantalla = nuevoTamanyo
                    }
                    .pointerInput(Unit) {
                        detectTapGestures {
                            toque -> motor.tocar(toque.x, toque.y)
                        }
                    }
            ) {
                val frameActual = contadorFotogramas

                for (mochi in motor.mochis) {
                    val estiloTexto = TextStyle(fontSize = mochi.radio.sp)
                    val medidas = medidorDeTexto.measure(mochi.emoji, style=estiloTexto)

                    drawText(
                        textLayoutResult = medidas,
                        topLeft = Offset(
                            x = mochi.x - (medidas.size.width / 2f),
                            y = mochi.y - (medidas.size.height / 2f)
                        )
                    )
                }
            }
        }
    }
}