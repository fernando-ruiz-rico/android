/**
 * ==============================================================================
 * JUEGO 2: MODO FÍSICAS (Gravedad y Rebotes)
 * ==============================================================================
 * Objetivo: Entorno donde los mochis se comportan como pelotas físicas reales.
 * Caen por la gravedad, rebotan perdiendo energía, y podemos golpearlos al tocarlos.
 * ==============================================================================
 */
package com.example.myapplication

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class MotorFisicas {
    data class Mochi(
        var x: Float, var y: Float,
        var velocidadY: Float = 0f, // Empieza a 0, pero la gravedad la aumentará para que caiga
        var velocidadX: Float = 0f,
        val radio: Float = 70f, var emoji: String, val tiempoCreacion: Long = System.currentTimeMillis()
    ) {
        fun fueTocado(xDelDedo: Float, yDelDedo: Float): Boolean {
            val distX = xDelDedo - x
            val distY = yDelDedo - y
            return (distX * distX) + (distY * distY) <= (radio * radio * 1.5f)
        }
    }

    companion object {
        const val MAX_MOCHIS = 100 // Reducimos el máximo a 100 porque calcular colisiones es costoso para la CPU
    }

    private val gravedad = 0.5f       // Píxeles de velocidad hacia abajo que ganan en cada fotograma
    private val elasticidad = 0.75f   // Cuando chocan, mantienen un 75% de su velocidad (pierden un 25%)
    private val friccionSuelo = 0.9f  // Cuando ruedan por el suelo, el rozamiento frena su velocidad lateral

    var mochis = mutableStateListOf<Mochi>()
    val emojisDisponibles = listOf("🍡", "🍮", "🥟", "🍓", "🥞", "🥝", "🫒", "🥑", "🥕", "🥒", "🍥", "🥓", "🌮")

    fun tocar(xToque: Float, yToque: Float) {
        var toco = false

        if (!toco) {
            mochis.add(Mochi(
                x = xToque, y = yToque,
                velocidadX = (Random.nextFloat() * 2f) - 1f, // Empieza moviéndose un poquito de lado
                emoji = emojisDisponibles.random()
            ))
            if (mochis.size > MAX_MOCHIS) mochis.removeFirstOrNull()
        }
    }

    fun limpiarPantalla() = mochis.clear()
}

@Composable
fun PantallaFisicas(alVolver: () -> Unit) {
    val motor = remember { MotorFisicas() }
    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }
    var contadorFotogramas by remember { mutableStateOf(0) }

    var mostrarDialogo by remember { mutableStateOf(false) }

    val animacion = rememberInfiniteTransition()
    val faseOla by animacion.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse)
    )

    val fondo = Brush.verticalGradient(listOf(
        androidx.compose.ui.graphics.lerp(Color(0xFFE3F2FD), Color(0xFF64B5F6), faseOla),
        androidx.compose.ui.graphics.lerp(Color(0xFF64B5F6), Color(0xFFE3F2FD), faseOla)
    ))

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false }, // Si el usuario toca fuera del popup, lo cerramos
            title = { Text("¿Borrar emojis?") },
            confirmButton = { TextButton(onClick = { motor.limpiarPantalla(); mostrarDialogo = false }) { Text("Sí", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(fondo)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = alVolver) {
                Text("⬅️", fontSize = 30.sp)
            }
            Text("Emojis: ${motor.mochis.size} / ${MotorFisicas.MAX_MOCHIS}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFE65100))
            IconButton(onClick = {
                if (motor.mochis.isNotEmpty()) mostrarDialogo = true }) {
                Text("🧹", fontSize = 30.sp) }
        }
    }
}