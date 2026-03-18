/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: JUEGO DE EXPLOTAR GLOBOS/BURBUJAS (CON ANIMACIONES)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo gestiona la visualización del minijuego donde elementos flotan
 * hacia arriba y el usuario debe tocarlos. La gran novedad aquí es cómo el
 * Canvas diferencia visualmente el estado "vivo" (flotando) del estado "muerto"
 * (mostrando una animación temporal de explosión o chispas).
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. Lógica condicional en dibujo: Cómo usar `if/else` dentro del Canvas para 
 * dibujar cosas completamente distintas según el estado de un objeto.
 * 2. Animaciones de estado efímeras: Cómo calcular el tiempo transcurrido desde 
 * un evento (explosión) para animar un efecto temporal (chispas ✨).
 * 3. Actualización de Puntuación: Vinculación del texto del HUD con la variable
 * `puntuacion` del motor.
 * ==============================================================================
 */
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

/**
 * Actividad principal de la aplicación Android.
 * Contenedor base de la interfaz de Compose.
 */
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

/**
 * Composable principal del juego. 
 * Combina UI tradicional (texto, botones) con gráficos interactivos (Canvas).
 */
@Composable
fun PantallaMochis() {
    // --- 1. ESTADO DEL JUEGO Y LA UI ---
    val motor = remember { MotorMochis() }
    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }
    var contadorFotogramas by remember { mutableStateOf(0) }
    var mostrarDialogoLimpiar by remember { mutableStateOf(false) }
    val medidorDeTexto = rememberTextMeasurer()
    val animacion = rememberInfiniteTransition()

    // --- 2. FONDO ANIMADO (CIELO / MAR) ---
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

    // --- 3. BUCLE PRINCIPAL (GAME LOOP) ---
    LaunchedEffect(Unit) {
        while(true) {
            withFrameNanos {
                if (tamanyoPantalla != IntSize.Zero) {
                    // Calculamos las físicas (flotabilidad y limpieza automática)
                    motor.actualizarFisicas(
                        anchoPantalla = tamanyoPantalla.width.toFloat(),
                        altoPantalla = tamanyoPantalla.height.toFloat()
                    )
                    contadorFotogramas++ // Forzamos a que el Canvas se repinte
                }
            }
        }
    }

    // --- 4. CUADRO DE DIÁLOGO DE CONFIRMACIÓN ---
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
                    onClick = { mostrarDialogoLimpiar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- 5. INTERFAZ GRÁFICA (HUD Y CANVAS) ---
    Column(modifier = Modifier.fillMaxSize().background(fondoMarino)) {
        
        // -- A. HUD (Cabecera Superior) --
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Muestra la puntuación en tiempo real enlazada al motor
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

        // -- B. ÁREA DE JUEGO (CANVAS) --
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { nuevoTamanyo -> tamanyoPantalla = nuevoTamanyo }
                    .pointerInput(Unit) {
                        detectTapGestures { toque -> 
                            // Enviamos el toque al motor para que procese posibles "explosiones"
                            motor.tocar(toque.x, toque.y) 
                        }
                    }
            ) {
                // Leemos estas variables para forzar la actualización y tener la hora actual
                val frameActual = contadorFotogramas
                val tiempoActual = System.currentTimeMillis()

                // Recorremos todos los elementos en juego
                for (mochi in motor.mochis) {
                    
                    // LÓGICA CONDICIONAL DE DIBUJO: ¿El globo está explotado o vivo?
                    if (mochi.explotado && mochi.y > 0) {
                        // --- ESTADO: EXPLOTADO (Animación de chispas) ---
                        
                        // Calculamos el tiempo desde que el usuario lo tocó
                        val milisegundosExplotado = tiempoActual - mochi.tiempoExplotado
                        // Factor de animación: Crece de 0 a 1.25 a lo largo de 250 milisegundos
                        val factorTamanyo = (milisegundosExplotado / 250f).coerceIn(0f, 1.25f)

                        // Aplicamos el factor de crecimiento al tamaño de la fuente
                        val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                        // Reemplazamos su emoji original por unas chispas (✨)
                        val medidas = medidorDeTexto.measure("✨", style = estiloTexto)

                        // Dibujamos las chispas en su última posición conocida
                        drawText(
                            textLayoutResult = medidas,
                            topLeft = Offset(
                                x = mochi.x - (medidas.size.width / 2f),
                                y = mochi.y - (medidas.size.height / 2f)
                            )
                        )

                        // TRUCO DE LIMPIEZA: 
                        // Si la animación ya terminó (factor llegó a 1.25), movemos el objeto 
                        // artificialmente fuera de la pantalla (-250f). 
                        // El motor borrará todos los elementos en -250f en su función `actualizarFisicas`.
                        if (factorTamanyo >= 1.25f) mochi.y = -250f
                    }
                    else {
                        // --- ESTADO: VIVO (Dibujo normal flotando) ---
                        
                        // Si no está explotado, usamos su tamaño de radio normal
                        val estiloTexto = TextStyle(fontSize = mochi.radio.sp )
                        // Y medimos el emoji que le tocó al nacer (ej. 🎈, 🦋)
                        val medidas = medidorDeTexto.measure(mochi.emoji, style = estiloTexto)

                        // Dibujamos el elemento centrado en sus coordenadas X/Y
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