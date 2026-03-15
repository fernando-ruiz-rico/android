/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: HUNDIR LA FLOTA (BATTLESHIP)
 * ==============================================================================
 * Objetivo del programa:
 * Actúa como la capa visual que conecta con el motor de juego. Dibuja el tablero
 * interactivo (ocultando la posición de los barcos mientras juegas) y gestiona 
 * la actualización de la pantalla cada vez que el usuario hace un movimiento.
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. Elementos Interactivos: Uso del modificador `clickable` sobre contenedores `Box`.
 * 2. Cuadrículas: Dibujo de tableros anidando `Column` y `Row`.
 * 3. Lógica visual: Ocultación de información en la UI (barcos) que sí existe en la lógica.
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
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PantallaHundirLaFlota()
                }
            }
        }
    }
}

/**
 * Composable que define el layout general del juego, mostrando el menú inicial o el tablero activo.
 */
@Composable
fun PantallaHundirLaFlota() {
    // Instancia persistente del motor del juego a lo largo de las recomposiciones
    val motorJuego = remember { Juego() }
    
    // Variable de estado numérico utilizada para forzar a la pantalla a redibujarse tras una interacción
    var refrescar by remember { mutableIntStateOf(0) }

    // Contenedor principal para organizar la pantalla verticalmente
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título del juego
        Text("HUNDIR LA FLOTA", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        // PANTALLA INICIAL: Antes de comenzar por primera vez
        if (motorJuego.juegoTerminado && motorJuego.misilesRestantes == motorJuego.MUNICION_MAXIMA) {
            Text(motorJuego.mensaje, modifier = Modifier.padding(bottom = 16.dp))

            Button(onClick = {
                // Inicia la lógica interna y fuerza el redibujado de la interfaz
                motorJuego.iniciarPartida()
                refrescar++
            }) {
                Text("Iniciar partida")
            }
        }
        // PANTALLA DE JUEGO: Durante la partida y al terminar
        else {
            // Panel de información (Mensajes y contadores)
            Text(motorJuego.mensaje, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Text("Misiles: ${motorJuego.misilesRestantes} | Aciertos: ${motorJuego.aciertos}/${motorJuego.impactosNecesarios} ")

            Spacer(modifier = Modifier.height(16.dp))

            // Dibujo del mapa (Tablero de casillas)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Iteramos por las filas (vertical)
                for (f in 0 until motorJuego.DIMENSION) {
                    Row {
                        // Iteramos por las columnas (horizontal)
                        for (c in 0 until motorJuego.DIMENSION) {
                            val estado = motorJuego.oceano[f][c]
                            var simbolo = estado.simbolo
                            
                            // LÓGICA DE OCULTACIÓN (Niebla de guerra):
                            // Si la casilla tiene un barco oculto y aún estamos jugando, dibujamos AGUA para que no se vea.
                            // Solo se revelan los barcos si la partida ha terminado.
                            if (estado == EstadoCasilla.BARCO && !motorJuego.juegoTerminado) {
                                simbolo = EstadoCasilla.AGUA.simbolo
                            }

                            // Representación visual de cada celda del tablero
                            Box(
                                modifier = Modifier
                                    .size(34.dp) // Tamaño cuadrado para cada celda
                                    .padding(1.dp) // Pequeño margen para simular la rejilla
                                    .background(Color.LightGray)
                                    // Hacemos que la caja sea pulsable sólo si el juego sigue en curso
                                    .clickable(enabled = !motorJuego.juegoTerminado) {
                                        // Efectuamos el disparo en el motor y forzamos la actualización visual
                                        motorJuego.turno(f, c)
                                        refrescar++
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Dibujamos el emoji correspondiente en el centro de la caja
                                Text(simbolo, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN DE REINICIO: Solo aparece cuando la partida ha concluido
            if (motorJuego.juegoTerminado) {
                Button(onClick = {
                    motorJuego.iniciarPartida()
                    refrescar++
                }) {
                    Text("Jugar otra vez")
                }
            }
        }
    }

    // Truco técnico de Jetpack Compose: este texto invisible enlaza la lectura de la variable
    // 'refrescar' con el Composable para garantizar que se actualiza al cambiar su valor.
    Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
}