/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: INVASORES DEL ESPACIO (JETPACK COMPOSE)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo es el punto de entrada de la aplicación en Android. Gestiona la
 * representación visual del juego (la UI) conectándola con el motor (Juego.kt).
 *
 * Qué aprenderás de Kotlin/Compose con este código:
 * 1. Funciones Composable: Componentes de UI declarativa (marcardos con @Composable).
 * 2. Manejo de Estado (State): Uso de `remember` y `mutableStateOf` para hacer que
 * la pantalla se redibuje automáticamente cuando cambian los datos.
 * 3. Efectos Secundarios: Uso de `LaunchedEffect` con corrutinas (`delay`) para
 * ejecutar código en segundo plano, creando el "reloj" o "TICK" continuo del juego.
 * 4. Layouts: Diseño de pantallas usando `Column` (elementos en vertical) y
 * `Row` (elementos en horizontal).
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
import kotlinx.coroutines.delay // Importante para el temporizador (corrutinas)

/**
 * Actividad principal de la aplicación Android.
 * Su única función es configurar el tema y lanzar nuestra pantalla principal construida en Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio de la pantalla
                    color = MaterialTheme.colorScheme.background // Pone el color de fondo por defecto del móvil
                ) {
                    PantallaJuego()
                }
            }
        }
    }
}

/**
 * Composable principal que dibuja toda la interfaz del juego.
 * Es responsable de mantener el estado de la partida y reaccionar a las interacciones.
 */
@Composable
fun PantallaJuego() {
    // --- ESTADO DE LA INTERFAZ ---
    // 'remember' asegura que la instancia del Juego no se borre cada vez que la pantalla se redibuja.
    val motorJuego = remember { Juego() }

    // Un estado "falso" numérico que incrementamos artificialmente para forzar a Compose a redibujar la pantalla.
    var refrescar by remember { mutableStateOf(0) }

    // Estado booleano que controla si el temporizador automático está funcionando (Play/Pause).
    var juegoEnMarcha by remember { mutableStateOf(false) }

    // --- BUCLE PRINCIPAL DE TIEMPO (Efecto Secundario) ---
    // LaunchedEffect se ejecuta en una corrutina en segundo plano.
    // Al pasarle 'juegoEnMarcha' como clave, se reinicia o cancela automáticamente si este valor cambia.
    LaunchedEffect(juegoEnMarcha) {
        while (juegoEnMarcha) {
            // Pausamos la ejecución temporalmente (el intervalo definido en el motor)
            delay(INTERVALO_MOVIMIENTO)

            // Le indicamos al motor que ha pasado un instante de tiempo
            motorJuego.turno("TICK")

            // Incrementamos la variable para forzar el redibujado de la pantalla
            refrescar++

            // Condición de salida: Si nos matan, pausamos el bucle automáticamente
            if (motorJuego.vidas <= 0) {
                juegoEnMarcha = false
            }
        }
    }

    // --- DISEÑO DE LA PANTALLA ---
    // Columna central que alinea todos los elementos verticalmente
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cabecera: Estadísticas principales (Puntos, Vidas, Oleada)
        Text(
            text = "PUNTOS ${motorJuego.puntos} | VIDAS ${motorJuego.vidas} | OLEADA: ${motorJuego.numeroOleada}",
            fontSize = 16.sp
        )
        // Cabecera: Inventario de bombas
        Text(
            text = "BOMBAS DISPONIBLES: ${motorJuego.bombasMasivas}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio en blanco separador

        // Pantalla del juego principal (El mapa dibujado en formato texto)
        Text(
            text = motorJuego.obtenerMapaComoTexto(),
            fontFamily = FontFamily.Monospace, // Fuente monoespaciada vital para que la cuadrícula quede perfecta
            fontSize = 24.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Zona de mensajes y notificaciones de eventos (daño, powerups recogidos, game over)
        Text(
            text = motorJuego.mensaje,
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTONERA DE CONTROLES (Movimiento y Disparo) ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // Botón Izquierda
            Button(onClick = {
                motorJuego.turno("IZQUIERDA")
                refrescar++ // Obligamos a redibujar para ver la nave moverse al instante
            }) { Text("👈")}

            // Botón Disparo Básico
            Button(onClick = {
                motorJuego.turno("FUEGO")
                refrescar++
            }) { Text("♦️")}

            // Botón Derecha
            Button(onClick = {
                motorJuego.turno("DERECHA")
                refrescar++
            }) { Text("👉")}
        }

        // --- BOTONERA DE SISTEMA (Play, Pause, Bomba) ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // Botón PLAY / REINICIAR
            Button(
                onClick = {
                    // Si le damos a play pero ya estamos muertos, reiniciamos el motor de juego entero
                    if (motorJuego.vidas <= 0) {
                        motorJuego.reiniciar()
                        refrescar++
                    }
                    // Arrancamos el LaunchedEffect poniendo esto a true
                    juegoEnMarcha = true
                },
                // El botón solo se puede pulsar si el juego está pausado O si estamos muertos (necesita reiniciar)
                enabled = !juegoEnMarcha || motorJuego.vidas <= 0
            ) { Text("▶️")}

            // Botón PAUSA
            Button(
                onClick = {
                    // Detiene el bucle del LaunchedEffect al instante
                    juegoEnMarcha = false
                },
                // El botón solo se activa si el juego está actualmente corriendo
                enabled = juegoEnMarcha
            ) { Text("⏸️")}

            // Botón de Arma Especial (Bomba)
            Button(onClick = {
                motorJuego.turno("BOMBA")
                refrescar++
            }) { Text("🧨")}
        }

        // Elemento invisible hack: usamos la variable 'refrescar' en la UI
        // para que Compose detecte que un dato ha cambiado y proceda a redibujar el Composable entero.
        Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
    }
}