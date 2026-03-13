package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
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
    var mostrarDialogoLimpiar by remember { mutableStateOf(false) }
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
                    motor.actualizarFisicas(
                        anchoPantalla = tamanyoPantalla.width.toFloat(),
                        altoPantalla = tamanyoPantalla.height.toFloat()
                    )
                    contadorFotogramas++
                }
            }
        }
    }

    if (mostrarDialogoLimpiar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLimpiar = false },
            title = { Text(text = "¿Borrar todos los emojis?")},
            confirmButton = {
                TextButton(
                    onClick = {
                        motor.limpiarPantalla()
                        mostrarDialogoLimpiar = false
                    }
                ) {
                    Text("Sí", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoLimpiar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(fondoMarino)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Emojis: ${motor.puntuacion}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFE65100)
            )

            IconButton(
                onClick = {
                    if (motor.mochis.isNotEmpty()) {
                        mostrarDialogoLimpiar = true
                    }
                }
            ) {
                Text("🧹", fontSize = 30.sp)
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
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
                    if (mochi.explotado && mochi.y > 0) {
                        val milisegundosExplotado = tiempoActual - mochi.tiempoExplotado
                        val factorTamanyo = (milisegundosExplotado / 250f).coerceIn(0f, 1.25f)

                        val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                        val medidas = medidorDeTexto.measure("✨", style = estiloTexto)

                        drawText(
                            textLayoutResult = medidas,
                            topLeft = Offset(
                                x = mochi.x - (medidas.size.width / 2f),
                                y = mochi.y - (medidas.size.height / 2f)
                            )
                        )

                        if (factorTamanyo >= 1.25f) mochi.y = -250f
                    }
                    else {
                        val estiloTexto = TextStyle(fontSize = mochi.radio.sp )
                        val medidas = medidorDeTexto.measure(mochi.emoji, style = estiloTexto)

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
}