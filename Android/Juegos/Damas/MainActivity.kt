/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: DAMAS (CHECKERS)
 * ==============================================================================
 * Objetivo del programa:
 * Actúa como la capa visual que conecta con el motor de juego de las Damas.
 * Dibuja el tablero interactivo, alterna los colores de las casillas y gestiona 
 * la actualización de la pantalla cada vez que el usuario o el ordenador interactúan.
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. Efectos Secundarios (LaunchedEffect): Uso de corrutinas para ejecutar el turno 
 * del ordenador con un ligero retraso sin bloquear la interfaz.
 * 2. Generación de tableros: Dibujo de cuadrículas anidando `Column` y `Row` calculando
 * colores dinámicamente según la posición (par/impar).
 * 3. Gestión de estados: Uso de variables para forzar la recomposición visual (`refrescar`).
 * ==============================================================================
 */

package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Actividad principal que carga el framework de Jetpack Compose y sirve de ventana a la aplicación.
 */
class MainActivity : ComponentActivity() {
    /**
     * Método inicial del ciclo de vida de Android. Configura y dibuja la interfaz base.
     *
     * @param savedInstanceState Si la actividad se reinicia, contiene los datos recuperados; si es primer inicio, es null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    PantallaDamas()
                }
            }
        }
    }
}

/**
 * Composable que define el layout general del juego, mostrando el tablero y el estado de la partida.
 */
@Composable
fun PantallaDamas() {
    // Instancia persistente del motor del juego a lo largo de las recomposiciones
    val motorJuego = remember { JuegoDamas() }
    
    // Variable de estado numérico utilizada para forzar a la pantalla a redibujarse tras una interacción
    var refrescar by remember { mutableIntStateOf(0) }

    // Efecto secundario que se lanza automáticamente cuando cambia 'motorJuego.turnoActual'
    LaunchedEffect(motorJuego.turnoActual) {
        // Si es el turno del ordenador (NEGRO) y el juego no ha terminado
        if (motorJuego.turnoActual == Jugador.NEGRO && !motorJuego.juegoTerminado) {
            delay(500) // Pausa de medio segundo para dar sensación de que la IA está "pensando"
            motorJuego.jugarOrdenador()
            refrescar++ // Forzamos el redibujado tras el movimiento de la IA
        }
    }

    // Contenedor principal para organizar la pantalla verticalmente centrada
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título principal del juego
        Text("DAMAS", fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp))

        // Mensaje de estado (Turnos, errores, o fin de partida)
        Text(motorJuego.mensaje, fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp))

        // Dibujo del mapa (Tablero de casillas 8x8)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Iteramos por las filas (vertical)
            for (fila in 0 until 8) {
                Row {
                    // Iteramos por las columnas (horizontal)
                    for (columna in 0 until 8) {
                        
                        // Calculamos el color de fondo de la casilla
                        val fondoCasilla = when {
                            // Casilla resaltada si está seleccionada
                            fila == motorJuego.filaSeleccionada && columna == motorJuego.columnaSeleccionada -> Color(0xFF81C784)
                            // Casillas oscuras (patrón ajedrezado matemático)
                            (fila + columna) % 2 != 0 -> Color(0xFFB58863)
                            // Casillas claras
                            else -> Color(0xFFF0D9B5)
                        }

                        // Representación visual de cada celda del tablero
                        Box(
                            modifier = Modifier
                                .size(40.dp) // Tamaño cuadrado para cada celda
                                .background(fondoCasilla) // Aplicamos el color calculado
                                .clickable() {
                                    // Pasamos las coordenadas al motor al hacer clic y refrescamos
                                    motorJuego.turno(fila, columna)
                                    refrescar++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Obtenemos la pieza en estas coordenadas y dibujamos su símbolo (emoji)
                            val pieza = motorJuego.tablero[fila][columna]
                            Text(pieza.simbolo, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }

    // Truco técnico de Jetpack Compose: este texto invisible enlaza la lectura de la variable
    // 'refrescar' con el Composable para garantizar que se actualiza al cambiar su valor.
    Text(text = "", modifier = Modifier.size(if (refrescar > 0) 0.dp else 0.dp))
}