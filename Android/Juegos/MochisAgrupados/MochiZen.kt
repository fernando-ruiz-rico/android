/**
 * ==============================================================================
 * JUEGO 1: MODO ZEN (Sin físicas complejas)
 * ==============================================================================
 * Objetivo: Permitir al usuario dibujar mochis libremente en la pantalla con
 * un efecto suave de flotación. Es el punto de partida para entender Canvas.
 * ==============================================================================
 */
package com.example.myapplication

// Importamos librerías matemáticas y de dibujo
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

class MotorZen {
    data class Mochi(
        var x: Float, // Posición horizontal (izquierda/derecha)
        var y: Float, // Posición vertical (arriba/abajo)
        val radio: Float = 70f, // Tamaño por defecto
        var emoji: String, // El dibujito en sí
        val tiempoCreacion: Long = System.currentTimeMillis() // Guardamos la hora exacta
    )

    companion object {
        const val MAX_MOCHIS = 1000 // Para que el móvil no se quede sin RAM si pintamos demasiados
    }

    var mochis = mutableStateListOf<Mochi>()

    val emojisDisponibles = listOf("🍡", "🍮", "🥟", "🍓", "🥞", "🥝", "🫒", "🥑", "🥕", "🥒", "🍥", "🥓", "🌮")

    fun tocar(xToque: Float, yToque: Float) {
        mochis.add(Mochi(
            x = xToque,
            y = yToque,
            emoji = emojisDisponibles.random())
        )
        if (mochis.size > MAX_MOCHIS) mochis.removeFirstOrNull()
    }
}

@Composable
fun PantallaZen(alVolver: () -> Unit) {
    val motor = remember { MotorZen() }

    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }

    var contadorFotogramas by remember { mutableStateOf(0) }

    val medidorDeTexto = rememberTextMeasurer()

    val animacion = rememberInfiniteTransition()

    val faseOla by animacion.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse)
    )

    val fondo = Brush.verticalGradient(listOf(
        androidx.compose.ui.graphics.lerp(Color(0xFFE8F5E9), Color(0xFF81C784), faseOla), // De claro a oscuro
        androidx.compose.ui.graphics.lerp(Color(0xFF81C784), Color(0xFFE8F5E9), faseOla)  // De oscuro a claro
    ))

    LaunchedEffect(Unit) {
        while(true) { // Bucle infinito
            withFrameNanos {
                if (tamanyoPantalla != IntSize.Zero) contadorFotogramas++
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(fondo)) {
        Canvas(
            modifier = Modifier.fillMaxSize()
                .onSizeChanged { tamanyoPantalla = it }
                .pointerInput(Unit) {
                    detectTapGestures { toque ->
                        motor.tocar(toque.x, toque.y)
                    }
                }
        ) {
            val frameActual = contadorFotogramas
            val tiempoActual = System.currentTimeMillis()

            for (mochi in motor.mochis) {
                val milisegundosCreado = tiempoActual - mochi.tiempoCreacion
                val factorTamanyo = (milisegundosCreado / 250f).coerceIn(0f, 1f)
                val flotacionY = (sin(tiempoActual / 500.0 + mochi.x / 100.0) * 15f).toFloat()

                val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                val medidas = medidorDeTexto.measure(mochi.emoji, style = estiloTexto)

                drawText(
                    textLayoutResult = medidas,
                    topLeft = Offset(
                        x = mochi.x - (medidas.size.width / 2f),
                        y = mochi.y - (medidas.size.height / 2f) + flotacionY
                    )
                )
            }
        }

        IconButton(
            onClick = alVolver,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("⬅️", fontSize = 32.sp)
        }
    }
}