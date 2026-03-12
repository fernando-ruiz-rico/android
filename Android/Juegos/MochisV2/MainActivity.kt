package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
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

    val animacion = rememberInfiniteTransition()

    val faseOla by animacion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val azulClaro = Color(0xFFE3F2FD)
    val azulOscuro = Color(0xFF64B5F6)

    val colorArriba = lerp(azulClaro, azulOscuro, faseOla)
    val colorAbajo = lerp(azulOscuro, azulClaro, faseOla)
    val fondoMarino = Brush.verticalGradient(listOf(colorArriba, colorAbajo))

    LaunchedEffect(Unit) {
        while(true) {
            withFrameNanos {
                if (tamanyoPantalla != IntSize.Zero) {
                    contadorFotogramas++
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
                .fillMaxSize()
                .background(fondoMarino)
        ) {
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
                val tiempoActual = System.currentTimeMillis()

                for (mochi in motor.mochis) {
                    val milisegundosCreado = tiempoActual - mochi.tiempoCreacion
                    val factorTamanyo = (milisegundosCreado / 250f).coerceIn(0f, 1f)

                    val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                    val medidas = medidorDeTexto.measure(mochi.emoji, style=estiloTexto)

                    val flotacionY = (sin(tiempoActual / 500.0 + mochi.x / 100.0) * 15f).toFloat()

                    drawText(
                        textLayoutResult = medidas,
                        topLeft = Offset(
                            x = mochi.x - (medidas.size.width / 2f),
                            y = mochi.y - (medidas.size.height / 2f) + flotacionY
                        )
                    )
                }
            }
        }
    }
}