/**
 * ==============================================================================
 * JUEGO 2: MODO FÍSICAS (Gravedad y Rebotes)
 * ==============================================================================
 * Objetivo del programa:
 * Entorno donde los mochis se comportan como pelotas físicas reales. Caen por 
 * la gravedad, rebotan perdiendo energía contra el suelo y los bordes, y 
 * podemos golpearlos (aplicar fuerzas) al tocarlos.
 * * Qué aprenderás de Kotlin y programación con este código:
 * 1. Simulación de Físicas 2D: Implementación de gravedad, inercia, elasticidad
 * (pérdida de energía al rebotar) y fricción.
 * 2. Detección de colisiones (AABB / Círculos): Cómo calcular cuándo un objeto 
 * toca los límites de la pantalla y reaccionar invirtiendo vectores.
 * 3. Optimización de bucles: Reducción de elementos máximos para no saturar la 
 * CPU con cálculos matemáticos complejos en cada frame.
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

/**
 * Gestor matemático que calcula las trayectorias físicas de todos los elementos.
 */
class MotorFisicas {
    /**
     * Representa un elemento con propiedades físicas simuladas.
     *
     * @property x Coordenada horizontal.
     * @property y Coordenada vertical.
     * @property velocidadY Aceleración actual en el eje vertical (afectada por gravedad).
     * @property velocidadX Velocidad actual en el eje horizontal (afectada por fricción).
     * @property radio Tamaño físico del objeto para el cálculo de colisiones.
     * @property emoji Símbolo visual a dibujar.
     * @property tiempoCreacion Marca de tiempo para animar el "nacimiento" del objeto.
     */
    data class Mochi(
        var x: Float, var y: Float,
        var velocidadY: Float = 0f, // Empieza a 0, pero la gravedad la aumentará para que caiga
        var velocidadX: Float = 0f,
        val radio: Float = 70f, var emoji: String, val tiempoCreacion: Long = System.currentTimeMillis()
    ) {
        /**
         * Comprueba si el usuario ha tocado este objeto en concreto.
         */
        fun fueTocado(xDelDedo: Float, yDelDedo: Float): Boolean {
            val distX = xDelDedo - x
            val distY = yDelDedo - y
            return (distX * distX) + (distY * distY) <= (radio * radio * 1.5f)
        }
    }

    companion object {
        const val MAX_MOCHIS = 100 // Reducimos el máximo a 100 porque calcular colisiones es costoso para la CPU
    }

    // CONSTANTES FÍSICAS
    private val gravedad = 0.5f       // Píxeles de velocidad hacia abajo que ganan en cada fotograma
    private val elasticidad = 0.75f   // Cuando chocan, mantienen un 75% de su velocidad (pierden un 25%)
    private val friccionSuelo = 0.9f  // Cuando ruedan por el suelo, el rozamiento frena su velocidad lateral

    var mochis = mutableStateListOf<Mochi>()
    val emojisDisponibles = listOf("🍡", "🍮", "🥟", "🍓", "🥞", "🥝", "🫒", "🥑", "🥕", "🥒", "🍥", "🥓", "🌮")

    /**
     * Gestiona el toque en pantalla. Si tocamos un emoji, le aplicamos una "fuerza" (impulso).
     * Si tocamos un espacio vacío, creamos un emoji nuevo.
     */
    fun tocar(xToque: Float, yToque: Float) {
        var toco = false

        for (mochi in mochis.reversed()) {
            if (mochi.fueTocado(xToque, yToque)) {
                // Le damos un "golpe" hacia arriba y un poco de forma lateral aleatoria
                mochi.velocidadY = -40f
                mochi.velocidadX = (Random.nextFloat() * 10f) - 5f
                toco = true
                break
            }
        }

        // Si no golpeamos nada, creamos uno nuevo en esa posición
        if (!toco) {
            mochis.add(Mochi(
                x = xToque, y = yToque,
                velocidadX = (Random.nextFloat() * 2f) - 1f, // Empieza moviéndose un poquito de lado
                emoji = emojisDisponibles.random()
            ))
            if (mochis.size > MAX_MOCHIS) mochis.removeFirstOrNull()
        }
    }

    /**
     * El corazón matemático del modo físicas. Calcula el movimiento de cada frame.
     */
    fun actualizarFisicas(anchoPantalla: Float, altoPantalla: Float) {
        if (anchoPantalla == 0f || altoPantalla == 0f) return // Prevención de errores si aún no tenemos pantalla

        for (mochi in mochis) {
            // 1. Aplicar Gravedad: Cada milisegundo es empujado más rápido hacia abajo
            mochi.velocidadY += gravedad

            // 2. Mover: Sumamos la velocidad actual a la posición
            mochi.y += mochi.velocidadY
            mochi.x += mochi.velocidadX

            // 3. Colisión con el suelo (El límite no es el borde del móvil, sino restando el radio del mochi)
            val limiteSuelo = altoPantalla - mochi.radio
            if (mochi.y > limiteSuelo) {
                mochi.y = limiteSuelo // Lo forzamos a no traspasar el suelo

                // Si la velocidad al botar es muy pequeña (abs saca el valor positivo absoluto),
                // lo detenemos (velocidad 0) para que deje de vibrar microscópicamente.
                if (abs(mochi.velocidadY) < 1.5f) {
                    mochi.velocidadY = 0f
                } else {
                    // Rebote: invertimos su velocidad (lo mandamos hacia arriba) pero perdiendo energía (elasticidad)
                    mochi.velocidadY = -mochi.velocidadY * elasticidad
                }

                // Al chocar contra el suelo, el rozamiento horizontal lo frena
                mochi.velocidadX *= friccionSuelo
            }

            // 4. Colisiones Laterales (Paredes izquierda y derecha)
            if (mochi.x < mochi.radio) {
                mochi.x = mochi.radio // Evitamos que salga por la izquierda
                mochi.velocidadX = -mochi.velocidadX * elasticidad // Rebote horizontal
            }
            else if (mochi.x > anchoPantalla - mochi.radio) {
                mochi.x = anchoPantalla - mochi.radio // Evitamos que salga por la derecha
                mochi.velocidadX = -mochi.velocidadX * elasticidad // Rebote horizontal
            }
        }
    }

    /**
     * Borra todos los objetos de la física.
     */
    fun limpiarPantalla() = mochis.clear()
}

/**
 * Interfaz de Compose para la simulación física.
 */
@Composable
fun PantallaFisicas(alVolver: () -> Unit) {
    val motor = remember { MotorFisicas() }
    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }
    var contadorFotogramas by remember { mutableStateOf(0) }

    var mostrarDialogo by remember { mutableStateOf(false) }

    val medidorDeTexto = rememberTextMeasurer()

    // Fondo animado azul
    val animacion = rememberInfiniteTransition()
    val faseOla by animacion.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse)
    )

    val fondo = Brush.verticalGradient(listOf(
        androidx.compose.ui.graphics.lerp(Color(0xFFE3F2FD), Color(0xFF64B5F6), faseOla),
        androidx.compose.ui.graphics.lerp(Color(0xFF64B5F6), Color(0xFFE3F2FD), faseOla)
    ))

    // BUCLE PRINCIPAL DE FÍSICAS
    LaunchedEffect(Unit) {
        while(true) {
            withFrameNanos {
                if (tamanyoPantalla != IntSize.Zero) {
                    // ¡Importante! Antes de decirle a Compose que pinte (+1 fotograma),
                    // le decimos al motor lógico que recalcule el mundo de la física.
                    motor.actualizarFisicas(tamanyoPantalla.width.toFloat(), tamanyoPantalla.height.toFloat())
                    contadorFotogramas++
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false }, // Si el usuario toca fuera del popup, lo cerramos
            title = { Text("¿Borrar emojis?") },
            confirmButton = { TextButton(onClick = { motor.limpiarPantalla(); mostrarDialogo = false }) { Text("Sí", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(fondo)) {
        // Cabecera superior (HUD)
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

        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier.fillMaxSize()
                    .onSizeChanged { tamanyoPantalla = it }
                    .pointerInput(Unit) { detectTapGestures { toque -> motor.tocar(toque.x, toque.y) } }
            ) {
                val tiempoActual = System.currentTimeMillis()
                val frameActual = contadorFotogramas

                for (mochi in motor.mochis) {
                    // Animación de aparición suave ("Pop-in") de 250ms
                    val milisegundosCreado = tiempoActual - mochi.tiempoCreacion
                    val factorTamanyo = (milisegundosCreado / 250f).coerceIn(0f, 1f)

                    // Un pequeño efecto extra: flotación oscilatoria basada en el tiempo (seno)
                    val flotacionY = (sin(tiempoActual / 500.0 + mochi.x / 100.0) * 15f).toFloat()

                    val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                    val medidas = medidorDeTexto.measure(mochi.emoji, style = estiloTexto)

                    drawText(
                        textLayoutResult = medidas,
                        topLeft = Offset(x = mochi.x - (medidas.size.width / 2f), y = mochi.y - (medidas.size.height / 2f) + flotacionY)
                    )
                }
            }
        }
    }
}