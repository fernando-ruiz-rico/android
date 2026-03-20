package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.random.Random

data class Anilla(
    var x: Float,
    var y: Float,
    var vx: Float = 0f, // Velocidad horizontal
    var vy: Float = 0f, // Velocidad vertical
    val radio: Float = 30f,
    val color: Color
)

class MotorAcuatico {
    val anillas = mutableListOf<Anilla>()

    var anchoPantalla = 0f
    var altoPantalla = 0f
    private var estaInicializado = false

    var frame by mutableIntStateOf(0)
        private set

    private val GRAVEDAD_AGUA = 0.2f // Cae despacio
    private val RESISTENCIA_AGUA = 0.97f // Frena el movimiento constantemente
    private val FUERZA_CHORRO = -15f // Fuerza hacia arriba al pulsar botón

    fun inicializarJuego(size: IntSize) {
        if (estaInicializado) return
        anchoPantalla = size.width.toFloat()
        altoPantalla = size.height.toFloat()

        val coloresAnillas = listOf(Color.Yellow, Color.Cyan, Color.Magenta, Color.Green, Color(0xFFFFA500))
        for (i in 1..15) {
            anillas.add(
                Anilla(
                    x = Random.nextFloat() * anchoPantalla,
                    y = altoPantalla - Random.nextFloat() * 200f, // Empiezan abajo
                    color = coloresAnillas.random()
                )
            )
        }
        estaInicializado = true
    }

    fun activarChorroIzquierdo() {
        aplicarFuerzaChorro(vxChorro = 8f) // Un empuje un poco más fuerte hacia los lados
    }

    fun activarChorroDerecho() {
        aplicarFuerzaChorro(vxChorro = -8f)
    }

    /** Aplica fuerza a las anillas, sobre todo a las que están más abajo */
    private fun aplicarFuerzaChorro(vxChorro: Float) {
        anillas.forEach { anilla ->
            // El chorro afecta más si la anilla está en la mitad inferior de la pantalla
            if (anilla.y > altoPantalla * 0.4f) {
                // Añadimos velocidad hacia arriba (negativa en Y)
                anilla.vy += FUERZA_CHORRO * (Random.nextFloat() * 0.5f + 0.8f) // Pequeña variación aleatoria
                // Añadimos un poco de velocidad lateral
                anilla.vx += vxChorro * Random.nextFloat()
            }
        }
    }

    fun actualizarFisicas() {
        if (anchoPantalla == 0f) return

        anillas.forEach { anilla ->
            anilla.vy += GRAVEDAD_AGUA

            anilla.vx *= RESISTENCIA_AGUA
            anilla.vy *= RESISTENCIA_AGUA

            anilla.x += anilla.vx
            anilla.y += anilla.vy

            if (anilla.y + anilla.radio > altoPantalla) {
                anilla.y = altoPantalla - anilla.radio
                anilla.vy = -abs(anilla.vy) * 0.3f // Rebote con mucha pérdida de energía
            }
            if (anilla.y - anilla.radio < 0) {
                anilla.y = anilla.radio
                anilla.vy = abs(anilla.vy) * 0.3f
            }

            if (anilla.x + anilla.radio > anchoPantalla || anilla.x - anilla.radio < 0) {
                anilla.vx *= -0.5f // Invertir dirección y perder energía
                anilla.x = anilla.x.coerceIn(anilla.radio, anchoPantalla - anilla.radio)
            }
        }
        frame++
    }
}

// --- UI COMPOSABLE ---

@Composable
fun PantallaAros(onVolver: () -> Unit) {
    val motor = remember { MotorAcuatico() }

    // Bucle de juego correcto sincronizado con los FPS de la pantalla
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                motor.actualizarFisicas()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1), Color(0xFF01579B))
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding() // Respeta la barra de notificaciones superior
        ) {
            IconButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                "Juego de Agua",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {

            val frameActual = motor.frame

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size ->
                        motor.inicializarJuego(size)
                    }
            ) {
                val f = frameActual

                // 2. Dibujar Anillas
                motor.anillas.forEach { anilla ->
                    // Dibujamos un círculo hueco (Stroke)
                    drawCircle(
                        color = anilla.color,
                        radius = anilla.radio,
                        center = Offset(anilla.x, anilla.y),
                        style = Stroke(width = 8f) // Grosor de la anilla
                    )
                    // Un pequeño brillo blanco para efecto de plástico
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = anilla.radio - 4f,
                        center = Offset(anilla.x - 5f, anilla.y - 5f),
                        style = Stroke(width = 3f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(), // Respeta la barra de navegación de Android
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espaciado central
        ) {
            // Botón Izquierdo
            BotonBombaAgua(
                texto = "BOMBA IZQ.",
                modifier = Modifier.weight(1f)
            ) { motor.activarChorroIzquierdo() }

            // Botón Derecho
            BotonBombaAgua(
                texto = "BOMBA DER.",
                modifier = Modifier.weight(1f)
            ) { motor.activarChorroDerecho() }
        }
    }
}

@Composable
fun BotonBombaAgua(
    texto: String,
    modifier: Modifier = Modifier,
    alPulsar: () -> Unit
) {
    Button(
        onClick = alPulsar,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFDD835), // Amarillo juguete
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            pressedElevation = 2.dp,
            defaultElevation = 10.dp
        )
    ) {
        Text(texto, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}