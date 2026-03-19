/**
 * ==============================================================================
 * JUEGO 3: MODO ARCADE (Explotar Globos/Burbujas)
 * ==============================================================================
 * Objetivo del programa:
 * Minijuego procedimental. Aquí el ordenador genera globos que flotan solos desde
 * abajo (Físicas invertidas), y el jugador debe atraparlos (explotar) con el dedo 
 * para ganar puntos.
 * * Qué aprenderás de Kotlin y programación con este código:
 * 1. Gestión de estados complejos: Uso de variables booleanas (`explotado`) para 
 * cambiar el comportamiento de un objeto vivo a inerte.
 * 2. Generación procedimental: Creación automática y aleatoria de elementos
 * usando probabilidades (`Random.nextFloat() < 0.05`).
 * 3. Lógica condicional en dibujo: Dibujar de forma distinta en el Canvas según 
 * el estado del objeto (vivo vs explotado).
 * 4. Limpieza de memoria (Garbage Collection manual): Borrar elementos que ya 
 * no se ven en pantalla usando `removeAll`.
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

/**
 * Gestor principal que controla la generación, movimiento y colisiones del juego Arcade.
 */
class MotorArcade {
    /**
     * Representa un elemento interactivo que flota en la pantalla.
     *
     * @property x Coordenada horizontal actual en la pantalla.
     * @property y Coordenada vertical actual en la pantalla.
     * @property velocidadY Aceleración acumulada en el eje vertical (flotabilidad).
     * @property velocidadX Velocidad constante en el eje horizontal.
     * @property radio Tamaño del área interactiva (hitbox) del elemento.
     * @property emoji Símbolo visual que se mostrará en pantalla.
     * @property tiempoExplotado Marca de tiempo exacta de cuándo fue tocado (para animación).
     * @property explotado Estado booleano: 'true' si ya lo pinchamos, 'false' si sigue jugando.
     */
    data class Mochi(
        var x: Float,
        var y: Float,
        var velocidadY: Float = 0f,
        var velocidadX: Float = 0f,
        val radio: Float = 70f,
        var emoji: String,
        var tiempoExplotado: Long = 0L, // Cuándo lo tocamos (para la animación de explosión)
        var explotado: Boolean = false  // ¿Sigue jugando o ya lo pinchamos?
    ) {
        /**
         * Comprueba si las coordenadas del toque impactan dentro de este elemento.
         *
         * @param xDelDedo Coordenada horizontal del toque.
         * @param yDelDedo Coordenada vertical del toque.
         * @return `true` si se acertó, `false` si el toque fue fuera.
         */
        fun fueTocado(xDelDedo: Float, yDelDedo: Float): Boolean {
            val distX = xDelDedo - x; val distY = yDelDedo - y
            // Se multiplica por 3f para crear una "hitbox" más generosa y amigable.
            return (distX * distX) + (distY * distY) <= (radio * radio * 3f)
        }
    }

    companion object {
        const val MAX_MOCHIS = 1000 // Límite para proteger la memoria RAM del dispositivo
    }

    var puntuacion: Int = 0 // Marcador global del jugador
    private val gravedad = 0.01f // Actúa como flotabilidad hacia arriba al restarse
    var mochis = mutableStateListOf<Mochi>()
    val emojisDisponibles = listOf("🎈", "🫧", "🌸", "🦋")

    /**
     * Procesa la interacción del usuario. Si toca un globo vivo, lo explota.
     */
    fun tocar(xToque: Float, yToque: Float) {
        // Se recorre al revés para detectar primero los que se dibujan por encima
        for (mochi in mochis.reversed()) {
            if (mochi.fueTocado(xToque, yToque) && !mochi.explotado) {
                mochi.explotado = true // Lo marcamos como muerto
                mochi.tiempoExplotado = System.currentTimeMillis() // Guardamos la hora del "asesinato"
                puntuacion++ // Sumamos un punto al marcador global
                break
            }
        }
    }

    /**
     * Motor principal de físicas. Actualiza el estado de los globos frame a frame.
     */
    fun actualizarFisicas(anchoPantalla: Float, altoPantalla: Float) {
        if (anchoPantalla == 0f || altoPantalla == 0f) return

        for (mochi in mochis) {
            // Solo le aplicamos matemáticas de flotación a los globos vivos.
            // Los explotados se quedan clavados en su sitio haciendo la animación de estrellas.
            if (!mochi.explotado) {
                mochi.velocidadY += gravedad
                mochi.y -= mochi.velocidadY // ¡Restamos Y! En pantallas, la Y = 0 está arriba del todo, restar significa subir.
            }
        }

        // LIMPIEZA DE MEMORIA: Borramos los que salieron de la pantalla por arriba
        mochis.removeAll { it.y <= -250f }

        // GENERACIÓN PROCEDIMENTAL: 5% de probabilidad en cada frame de crear un globo nuevo
        if (Random.nextFloat() < 0.05) {
            mochis.add(Mochi(
                x = Random.nextFloat() * anchoPantalla, // Posición horizontal en cualquier punto
                y = altoPantalla + 150f, // Nace enterrado debajo del suelo para que entre en escena naturalmente
                velocidadX = (Random.nextFloat() * 2f) - 1f,
                emoji = emojisDisponibles.random()
            ))
            // Si hay demasiados, eliminamos el más antiguo
            if (mochis.size > MAX_MOCHIS) mochis.removeFirstOrNull()
        }
    }

    /**
     * Reinicia el juego, vaciando la pantalla y poniendo los puntos a cero.
     */
    fun limpiarPantalla() {
        mochis.clear()
        puntuacion = 0 // Al reiniciar, también ponemos los puntos a cero
    }
}

/**
 * Interfaz gráfica del modo Arcade. Maneja el bucle de juego y el Canvas.
 */
@Composable
fun PantallaArcade(alVolver: () -> Unit) {
    val motor = remember { MotorArcade() }
    var tamanyoPantalla by remember { mutableStateOf(IntSize.Zero) }
    var contadorFotogramas by remember { mutableStateOf(0) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val medidorDeTexto = rememberTextMeasurer()

    // Fondo animado: Un degradado que oscila suavemente (atardecer rosa)
    val animacion = rememberInfiniteTransition()
    val faseOla by animacion.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse)
    )
    val fondo = Brush.verticalGradient(listOf(
        androidx.compose.ui.graphics.lerp(Color(0xFFFCE4EC), Color(0xFFF06292), faseOla), // Tonos rosas atardecer
        androidx.compose.ui.graphics.lerp(Color(0xFFF06292), Color(0xFFFCE4EC), faseOla)
    ))

    // BUCLE DE JUEGO PRINCIPAL (Game Loop)
    LaunchedEffect(Unit) {
        while(true) {
            withFrameNanos {
                if (tamanyoPantalla != IntSize.Zero) {
                    motor.actualizarFisicas(tamanyoPantalla.width.toFloat(), tamanyoPantalla.height.toFloat())
                    contadorFotogramas++ // Fuerza a Compose a redibujar el Canvas
                }
            }
        }
    }

    // CUADRO DE DIÁLOGO DE CONFIRMACIÓN
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Reiniciar puntuación y limpiar?") },
            confirmButton = { TextButton(onClick = { motor.limpiarPantalla(); mostrarDialogo = false }) { Text("Sí", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") } }
        )
    }

    // INTERFAZ DE USUARIO (HUD y Lienzo)
    Column(modifier = Modifier.fillMaxSize().background(fondo)) {
        // Cabecera con botón de retroceso, puntuación y botón de reset
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = alVolver) { Text("⬅️", fontSize = 30.sp) }

            // Texto dinámico: Puntuación actual del jugador
            Text(
                "Puntos: ${motor.puntuacion}",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Blue
            )

            IconButton(onClick = { mostrarDialogo = true }) {
                Text(
                    "🧹",
                    fontSize = 30.sp
                )
            } // Icono de resetear
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

                    // LÓGICA DE DIBUJO CONDICIONAL:
                    // El Canvas pinta de una forma si está vivo, y de OTRA muy distinta si ha explotado.
                    if (mochi.explotado && mochi.y > 0) {

                        // Estado: MUERTO (Dibujamos Animación de Explosión)
                        val milisegundosExplotado = tiempoActual - mochi.tiempoExplotado

                        // En 250ms hace un "Pop". Va de 0 al tamaño de 1.25x
                        val factorTamanyo = (milisegundosExplotado / 250f).coerceIn(0f, 1.25f)

                        val estiloTexto = TextStyle(fontSize = mochi.radio.sp * factorTamanyo)
                        // SUSTITUCIÓN VISUAL: No dibujamos el globo, dibujamos chispas mágicas
                        val medidas = medidorDeTexto.measure("✨", style = estiloTexto)

                        drawText(
                            textLayoutResult = medidas,
                            topLeft = Offset(x = mochi.x - (medidas.size.width / 2f), y = mochi.y - (medidas.size.height / 2f))
                        )

                        // Cuando acaba la animación mágica visual (supera 1.25f), teletransportamos las chispas
                        // arriba del todo (-250f). ¿Recuerdas la "Recolección de Basura" en el motor?
                        // El motor detectará que está en -250f y lo borrará definitivamente de la memoria por nosotros.
                        if (factorTamanyo >= 1.25f) mochi.y = -250f

                    } else {
                        // Estado: VIVO (Dibujamos el globo flotando normalmente)
                        val estiloTexto = TextStyle(fontSize = mochi.radio.sp )
                        val medidas = medidorDeTexto.measure(mochi.emoji, style = estiloTexto)

                        drawText(
                            textLayoutResult = medidas,
                            topLeft = Offset(x = mochi.x - (medidas.size.width / 2f), y = mochi.y - (medidas.size.height / 2f))
                        )
                    }
                }
            }
        }
    }
}