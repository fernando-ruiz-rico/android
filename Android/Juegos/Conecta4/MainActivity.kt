/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: CONECTA 4
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo gestiona la representación visual del clásico juego Conecta 4.
 * Muestra el menú inicial para seleccionar la dificultad, el tablero dibujado 
 * mediante texto y los controles para que el jugador elija en qué columna soltar su ficha.
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. Renderizado Condicional: Uso de `if/else` dentro de Compose para alternar
 * entre la pantalla de inicio y la del juego activo.
 * 2. Generación dinámica de UI: Uso de bucles `for` y `forEach` para crear 
 * botones automáticamente según el tamaño del tablero o las opciones.
 * 3. Tipos de Estado: Uso de `mutableIntStateOf` optimizado para números enteros.
 * ==============================================================================
 */

package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Actividad principal que sirve como punto de entrada de la aplicación Android.
 */
class MainActivity : ComponentActivity() {
    
    /**
     * Se llama cuando la actividad se inicia por primera vez.
     *
     * @param savedInstanceState Si la actividad se reinicia después de haber sido cerrada, 
     * contiene los datos más recientes suministrados; de lo contrario, es null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent conecta la lógica de UI de Jetpack Compose con la actividad de Android
        setContent {
            // MaterialTheme aplica los estilos básicos visuales del sistema (colores, tipografía)
            MaterialTheme {
                // Surface actúa como el fondo general de la aplicación ocupando todo el tamaño de la pantalla
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Llamamos a nuestra pantalla principal del juego
                    PantallaJuego()
                }
            }
        }
    }
}

/**
 * Composable principal que dibuja toda la interfaz del juego Conecta 4.
 * Dependiendo del estado del motor, muestra el menú de dificultad o el tablero.
 */
@Composable
fun PantallaJuego() {
    // 'remember' mantiene vivo el motor del juego entre redibujados de la pantalla
    val motorJuego = remember { Juego() }
    
    // Estado numérico que nos servirá como "gatillo" para forzar a Compose a redibujar la UI
    var refrescar by remember { mutableIntStateOf(0) }

    // Contenedor principal: organiza los elementos de arriba a abajo y los centra
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título principal del juego
        Text("CONECTA 4", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        // RENDERIZADO CONDICIONAL: ¿El jugador ha elegido ya la dificultad?
        if (motorJuego.dificultadSeleccionada == null) {
            // --- PANTALLA 1: MENÚ DE INICIO ---
            
            Text(motorJuego.mensaje, modifier = Modifier.padding(bottom = 16.dp))

            // Recorremos todos los valores posibles del Enum Dificultad para crear sus botones
            Dificultad.values().forEach { nivel ->
                Button(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        // Al pulsar, iniciamos la partida con ese nivel y forzamos el refresco
                        motorJuego.iniciarPartida(nivel)
                        refrescar++
                    }
                ) {
                    Text(nivel.descripcion) // Muestra "Fácil", "Medio" o "Difícil"
                }
            }
        } else {
            // --- PANTALLA 2: TABLERO DE JUEGO ---
            
            // Mensaje de estado (quién gana, a quién le toca...)
            Text(text = motorJuego.mensaje, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            // Dibuja el tablero. Se usa fuente Monospace para alinear la cuadrícula perfectamente
            Text(
                text = motorJuego.obtenerMapaComoTexto(),
                fontFamily = FontFamily.Monospace,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                modifier = Modifier.padding(vertical = 15.dp)
            )

            // Comprobamos si la partida sigue en curso o ha terminado
            if (!motorJuego.juegoTerminado) {
                // Generamos una fila de botones numerados, uno por cada columna del tablero
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Bucle para crear dinámicamente tantos botones como columnas haya (0 a 6)
                    for (c in 0 until motorJuego.COLUMNAS) {
                        Button(
                            onClick = {
                                // Al pulsar un botón, el jugador tira una ficha en esa columna
                                motorJuego.turno(c)
                                refrescar++
                            },
                            modifier = Modifier.width(35.dp),
                            contentPadding = PaddingValues(0.dp) // Quitamos el padding interno para que quepan
                        ) {
                            Text(c.toString())
                        }
                    }
                }
            }
            else {
                // Si la partida terminó, mostramos un botón para reiniciar
                Button(onClick = {
                    // Reseteamos el estado a 'null' para volver al menú de inicio
                    motorJuego.dificultadSeleccionada = null
                    motorJuego.mensaje = "Selecciona dificultad"
                    refrescar++
                }) {
                    Text("Jugar otra vez")
                }
            }
        }

        // Elemento técnico: Componente invisible vinculado a 'refrescar' para asegurar la actualización visual
        Text(text = "", modifier = Modifier.size(if (refrescar > 0) 0.dp else 0.dp))
    }
}