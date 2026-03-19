/**
 * ==============================================================================
 * JUEGO 3: MODO ARCADE (Explotar Globos/Burbujas)
 * ==============================================================================
 * Objetivo: Minijuego procedimental. Aquí el ordenador genera globos que flotan
 * solos desde abajo (Físicas invertidas), y el jugador debe atraparlos (explotar) 
 * con el dedo para ganar puntos.
 * ==============================================================================
 */
package com.example.myapplication

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MotorArcade {
    data class Mochi(
        var x: Float,
        var y: Float,
        var velocidadY: Float = 0f,
        var velocidadX: Float = 0f,
        val radio: Float = 70f,
        var emoji: String,
        var tiempoExplotado: Long = 0L, // Cuándo lo tocamos (para la animación de explosión)
        var explotado: Boolean = false  // ¿Sigue jugando o ya lo pinchamos?
    )
}

@Composable
fun PantallaArcade(alVolver: () -> Unit) {
}