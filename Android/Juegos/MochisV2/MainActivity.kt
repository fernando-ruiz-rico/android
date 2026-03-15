/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: ENTORNO INTERACTIVO MIXTO (UI + CANVAS + FÍSICAS)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo gestiona la vista de un juego o entorno interactivo. A diferencia
 * de la versión anterior, aquí combinamos la interfaz clásica (una barra superior
 * con un contador y un botón de borrado) con un lienzo (Canvas) donde ocurren
 * las físicas y animaciones en tiempo real.
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. UI Mixta: Cómo combinar layouts clásicos (Row, Column) con un Canvas.
 * 2. Cuadros de diálogo: Uso de `AlertDialog` para pedir confirmación al usuario.
 * 3. Bucle de físicas: Cómo integrar la llamada `actualizarFisicas` dentro del
 * ciclo de refresco de la pantalla (Game Loop).
 * 4. Gestión de estados de UI: Uso de booleanos para mostrar u ocultar pop-ups.
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
 * Sirve como contenedor para nuestra pantalla construida con Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent // El color base es transparente porque pintaremos un fondo animado
                ) {
                    PantallaMochis()
                }
            }
        }
    }
}

/**
 * Composable principal que contiene la interfaz, el diálogo de confirmación, 
 * el bucle de juego y el lienzo de dibujo.
 */
@Composable
fun PantallaMochis() {
    // --- 1. ESTADO GLOBAL DE LA PANTALLA ---
    // Instancia del motor lógico (se mantiene vivo entre redibujados)
    val motor = remember { MotorMochis() }
    
    // Tamaño de la pantalla (se inicializa a cero y se actualiza al medir el Canvas)
    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }
    
    // Disparador invisible para obligar al Canvas a redibujarse constantemente
    var contadorFotogramas by remember { mutableStateOf(0) }
    
    // Estado que controla si la ventana emergente de confirmación está visible o no
    var mostrarDialogoLimpiar by remember { mutableStateOf(false) }
    
    // Herramienta para medir cuánto ocupan los emojis antes de pintarlos
    val medidorDeTexto = rememberTextMeasurer()

    // --- 2. ANIMACIÓN DEL FONDO ---
    val animacion = rememberInfiniteTransition()

    // Valor que oscila infinitamente entre 0 y 1 para crear un ciclo suave
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

    // Interpolar (mezclar) colores según la oscilación para simular olas
    val colorArriba = lerp(azulClaro, azulOscuro, faseOla)
    val colorAbajo = lerp(azulOscuro, azulClaro, faseOla)
    val fondoMarino = Brush.verticalGradient(listOf(colorArriba, colorAbajo))

    // --- 3. BUCLE DE JUEGO (GAME LOOP) CON FÍSICAS ---
    LaunchedEffect(Unit) {
        while(true) {
            // Se ejecuta sincronizado con el refresco del monitor (ej. 60 FPS)
            withFrameNanos {
                if (tamanyoPantalla != IntSize.Zero) {
                    // ¡NUEVO! Antes de pintar, le decimos al motor que calcule 
                    // dónde deberían estar los Mochis aplicando la gravedad y rebotes.
                    motor.actualizarFisicas(
                        anchoPantalla = tamanyoPantalla.width.toFloat(),
                        altoPantalla = tamanyoPantalla.height.toFloat()
                    )
                    // Sumamos 1 para notificar a Compose que debe redibujar la pantalla
                    contadorFotogramas++
                }
            }
        }
    }

    // --- 4. VENTANA EMERGENTE (ALERT DIALOG) ---
    // Este bloque solo se añade a la interfaz si la variable es verdadera
    if (mostrarDialogoLimpiar) {
        
        AlertDialog(
            // Qué pasa si el usuario toca fuera del diálogo para cancelarlo
            onDismissRequest = { mostrarDialogoLimpiar = false },
            title = { Text(text = "¿Borrar todos los emojis?")},
            confirmButton = {
                TextButton(
                    onClick = {
                        motor.limpiarPantalla() // Vaciamos la memoria
                        mostrarDialogoLimpiar = false // Ocultamos el diálogo
                    }
                ) {
                    Text("Sí", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoLimpiar = false // Ocultamos sin hacer nada
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- 5. ESTRUCTURA VISUAL DE LA PANTALLA ---
    // Column apila elementos de arriba hacia abajo
    Column(modifier = Modifier.fillMaxSize().background(fondoMarino)) {
        
        // --- A. BARRA SUPERIOR (HUD) ---
        // Row organiza elementos de izquierda a derecha
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Empuja los elementos a los extremos
            verticalAlignment = Alignment.CenterVertically, // Los centra verticalmente
        ) {
            // Texto dinámico que muestra el inventario actual
            Text(
                text = "Emojis: ${motor.mochis.size} / ${MotorMochis.MAX_MOCHIS}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFE65100) // Color naranja oscuro
            )

            // Botón con icono de escoba
            IconButton(
                onClick = {
                    // Solo mostramos el diálogo de confirmación si hay algo que borrar
                    if (motor.mochis.isNotEmpty()) {
                        mostrarDialogoLimpiar = true
                    }
                }
            ) {
                Text("🧹", fontSize = 30.sp)
            }
        }

        // --- B. ÁREA DE JUEGO (CANVAS) ---
        // Box es un contenedor que permite apilar elementos uno encima de otro, 
        // pero aquí lo usamos para que el Canvas ocupe el resto del espacio disponible.
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    // Detecta y guarda el tamaño del área de juego
                    .onSizeChanged {
                            nuevoTamanyo -> tamanyoPantalla = nuevoTamanyo
                    }
                    // Detecta toques en la pantalla y se los envía al motor
                    .pointerInput(Unit) {
                        detectTapGestures {
                                toque -> motor.tocar(toque.x, toque.y)
                        }
                    }
            ) {
                // Leemos las variables para forzar la actualización continua en este bloque
                val frameActual = contadorFotogramas
                val tiempoActual = System.currentTimeMillis()

                // Bucle de renderizado: dibuja cada mochi en su posición actual (ya calculada por las físicas)
                for (mochi in motor.mochis) {
                    // Animación de aparición (crece de tamaño gradualmente)
                    val milisegundosCreado = tiempoActual - mochi.tiempoCreacion
                    val factorTamanyo = (milisegundosCreado / 250f).coerceIn(0f, 1f)

                    val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                    val medidas = medidorDeTexto.measure(mochi.emoji, style=estiloTexto)

                    // Animación secundaria: pequeña vibración o flotación vertical visual usando Seno
                    val flotacionY = (sin(tiempoActual / 500.0 + mochi.x / 100.0) * 15f).toFloat()

                    // Dibujado del Emoji en las coordenadas del motor (modificadas visualmente al centro)
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