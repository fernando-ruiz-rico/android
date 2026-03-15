/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: ENTORNO INTERACTIVO (CANVAS Y ANIMACIONES)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo gestiona la representación visual de los Mochis. A diferencia de 
 * una interfaz normal con botones, aquí usamos un 'Canvas' (Lienzo) para dibujar 
 * libremente a 60 fotogramas por segundo, creando una experiencia fluida.
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. Dibujo avanzado: Uso de `Canvas` y `drawText` para renderizar gráficos.
 * 2. Bucle de juego (Game Loop): Uso de `LaunchedEffect` y `withFrameNanos` para 
 * ejecutar código en cada fotograma de la pantalla.
 * 3. Animaciones de Compose: Uso de `rememberInfiniteTransition` para el fondo.
 * 4. Matemáticas aplicadas: Uso de la función seno (`sin`) para simular flotación.
 * 5. Gestos: Uso de `pointerInput` para detectar toques exactos en la pantalla.
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

/**
 * Actividad principal de la aplicación Android.
 * Es el contenedor base donde vivirá nuestra interfaz de Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent // El fondo lo manejaremos nosotros después
                ) {
                    PantallaMochis()
                }
            }
        }
    }
}

/**
 * Composable principal que dibuja el lienzo, el fondo animado y los Mochis.
 */
@Composable
fun PantallaMochis() {
    // --- 1. ESTADO DE LA APLICACIÓN ---
    // Instanciamos nuestro motor lógico. 'remember' evita que se reinicie al redibujar.
    val motor = remember { MotorMochis() }
    
    // Guardamos el tamaño de la pantalla. Empieza en cero hasta que Compose la mida.
    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }
    
    // Este contador es el "corazón" de nuestro motor de renderizado. 
    // Al cambiar constantemente, forzará al Canvas a redibujarse.
    var contadorFotogramas by remember { mutableStateOf(0) }
    
    // Herramienta necesaria para calcular cuánto ocupa un texto (emoji) ANTES de dibujarlo.
    val medidorDeTexto = rememberTextMeasurer()

    // --- 2. ANIMACIÓN DEL FONDO (Efecto Olas/Respiración) ---
    val animacion = rememberInfiniteTransition()

    // Creamos un valor que va de 0.0 a 1.0 y vuelve a 0.0 de forma infinita
    val faseOla by animacion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing), // Tarda 3 segundos en ir de 0 a 1
            repeatMode = RepeatMode.Reverse // Al llegar a 1, vuelve hacia 0 (efecto rebote)
        )
    )

    // Definimos los colores del mar
    val azulClaro = Color(0xFFE3F2FD)
    val azulOscuro = Color(0xFF64B5F6)

    // Mezclamos (interpolar/lerp) los colores según la fase de la ola para crear movimiento
    val colorArriba = lerp(azulClaro, azulOscuro, faseOla)
    val colorAbajo = lerp(azulOscuro, azulClaro, faseOla)
    // Creamos un degradado vertical con esos colores dinámicos
    val fondoMarino = Brush.verticalGradient(listOf(colorArriba, colorAbajo))

    // --- 3. BUCLE DE JUEGO (GAME LOOP) ---
    // LaunchedEffect ejecuta una rutina en segundo plano. Al pasarle 'Unit', 
    // solo se ejecuta una vez cuando la pantalla se crea.
    LaunchedEffect(Unit) {
        while(true) {
            // 'withFrameNanos' se sincroniza con el refresco de la pantalla (ej. 60Hz).
            // Se ejecuta justo antes de pintar el siguiente fotograma.
            withFrameNanos {
                // Solo empezamos a contar fotogramas si la pantalla ya tiene un tamaño real
                if (tamanyoPantalla != IntSize.Zero) {
                    // Al sumar 1, el estado cambia y obligamos al Canvas a redibujarse
                    contadorFotogramas++
                }
            }
        }
    }

    // --- 4. ESTRUCTURA VISUAL ---
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
                .fillMaxSize()
                .background(fondoMarino) // Aplicamos nuestro fondo animado
        ) {
                        // Lienzo de dibujo de alto rendimiento
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    // Detecta cuando la pantalla cambia de tamaño (ej. al girar el móvil)
                    .onSizeChanged {
                            nuevoTamanyo -> tamanyoPantalla = nuevoTamanyo
                    }
                    // Detección de gestos táctiles
                    .pointerInput(Unit) {
                        detectTapGestures {
                            // Cuando el usuario toca, le enviamos las coordenadas exactas al Motor
                                toque -> motor.tocar(toque.x, toque.y)
                        }
                    }
            ) {
                // --- 5. LÓGICA DE DIBUJO (Se ejecuta decenas de veces por segundo) ---
                
                // Leemos 'contadorFotogramas' para "engañar" a Compose y obligarle
                // a ejecutar este bloque continuamente, aunque no usemos la variable directamente.
                val frameActual = contadorFotogramas 
                val tiempoActual = System.currentTimeMillis()

                // Recorremos todos los mochis guardados en el motor
                for (mochi in motor.mochis) {
                    
                    // --- A. Animación de "Nacimiento" (Pop-in) ---
                    // Calculamos cuánto tiempo lleva vivo este Mochi
                    val milisegundosCreado = tiempoActual - mochi.tiempoCreacion
                    // Creamos un factor de tamaño de 0f a 1f. Tarda 250ms en llegar a su tamaño final.
                    val factorTamanyo = (milisegundosCreado / 250f).coerceIn(0f, 1f)

                    // Configuramos el tamaño de la fuente multiplicando su radio por la animación
                    val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                    
                    // Medimos exactamente cuántos píxeles de ancho y alto ocupará el emoji
                    val medidas = medidorDeTexto.measure(mochi.emoji, style = estiloTexto)

                    // --- B. Animación de Flotación (Trigonometría) ---
                    // Usamos la función Seno para crear un movimiento ondulante suave arriba y abajo.
                    // Depende del tiempo (para moverse) y de su posición X (para que no floten todos a la vez).
                    val flotacionY = (sin(tiempoActual / 500.0 + mochi.x / 100.0) * 15f).toFloat()

                    // --- C. Dibujo final ---
                    drawText(
                        textLayoutResult = medidas,
                        // Offset define dónde se dibuja el centroide (esquina superior izquierda por defecto)
                        topLeft = Offset(
                            // Restamos la mitad de lo que mide para que se dibuje centrado en el toque del usuario
                            x = mochi.x - (medidas.size.width / 2f),
                            y = mochi.y - (medidas.size.height / 2f) + flotacionY // Le sumamos la flotación
                        )
                    )
                }
            }
        }
    }
}