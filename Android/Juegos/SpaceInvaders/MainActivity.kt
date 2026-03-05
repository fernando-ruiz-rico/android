/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: INVASORES DEL ESPACIO (VERSIÓN POR TURNOS)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo es el punto de entrada de la aplicación en Android. Gestiona la
 * representación visual del juego (la interfaz de usuario o UI) y conecta los
 * botones que pulsa el jugador con la lógica del motor (Juego.kt).
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. Funciones Composable: Construcción de interfaces declarativas (@Composable).
 * 2. Manejo de Estado (State): Uso de `remember` y `mutableStateOf` para mantener
 * los datos vivos y forzar a la pantalla a redibujarse cuando algo cambia.
 * 3. Layouts (Diseños): Agrupación de elementos visuales usando `Column` (vertical)
 * y `Row` (horizontal), además de espaciadores (`Spacer`).
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
 * Actividad principal de la aplicación Android.
 * Es la ventana básica que el sistema operativo Android abre al iniciar la app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent define qué elementos visuales (Composables) van dentro de esta ventana
        setContent {
            // Aplicamos el tema visual predeterminado de Material Design 3
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), // Ocupa todo el espacio de la pantalla
                    color = MaterialTheme.colorScheme.background // Pone el color de fondo por defecto del móvil
                ) {
                    // Llamamos a nuestro componente principal que contiene el juego
                    PantallaJuego()
                }
            }
        }
    }
}

/**
 * Composable principal que dibuja toda la interfaz del juego.
 * Es responsable de mantener el estado de la partida y reaccionar a los botones.
 */
@Composable
fun PantallaJuego() {
    // --- ESTADO DE LA INTERFAZ ---
    // 'remember' asegura que la instancia del Juego no se borre ni se reinicie cada vez que Compose redibuja la pantalla.
    val motorJuego = remember { Juego() }
    
    // Como las variables internas de 'motorJuego' no son estados de Compose, creamos este estado numérico artificial.
    // Al sumarle 1 (refrescar++), Compose detecta un cambio y vuelve a dibujar toda la pantalla con los nuevos datos.
    var refrescar by remember { mutableStateOf(0) }

    // --- DISEÑO DE LA PANTALLA ---
    // Column organiza a todos sus "hijos" uno debajo del otro de forma vertical.
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp), // Ocupa todo el espacio y deja un margen de 16dp
        horizontalAlignment = Alignment.CenterHorizontally, // Centra los elementos horizontalmente
        verticalArrangement = Arrangement.Center // Centra los elementos verticalmente en la pantalla
    ) {
        // Cabecera: Estadísticas principales
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

        // Hueco vacío para separar elementos
        Spacer(modifier = Modifier.height(16.dp))

        // Pantalla del juego principal (El mapa dibujado en texto)
        Text(
            text = motorJuego.obtenerMapaComoTexto(),
            // Usamos fuente monoespaciada para que todos los caracteres midan lo mismo
            // Esto es crucial para que la "cuadrícula" del tablero quede perfectamente alineada
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Zona de mensajes y notificaciones (daño, game over, powerups)
        Text(
            text = motorJuego.mensaje,
            color = MaterialTheme.colorScheme.error, // Lo pintamos del color de "error" (generalmente rojo)
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTONERA 1: Movimiento y Disparo ---
        // Row organiza a sus "hijos" uno al lado del otro de forma horizontal.
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp) // Separa los botones entre sí por 15dp
        ) {
            // Botón Mover Izquierda
            Button(onClick = {
                motorJuego.turno("IZQUIERDA")
                refrescar++ // Obligamos a la UI a actualizarse para ver el movimiento
            }) { Text("👈")}

            // Botón Disparo Básico
            Button(onClick = {
                motorJuego.turno("FUEGO")
                refrescar++
            }) { Text("♦️")}

            // Botón Mover Derecha
            Button(onClick = {
                motorJuego.turno("DERECHA")
                refrescar++
            }) { Text("👉")}
        }

        // --- BOTONERA 2: Acciones del sistema ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // Botón Pasar Turno (Esperar)
            Button(onClick = {
                motorJuego.turno("ESPERAR")
                refrescar++
            }) { Text("🟢")}

            // Botón Arma Especial
            Button(onClick = {
                motorJuego.turno("BOMBA")
                refrescar++
            }) { Text("🧨")}

            // Botón Reiniciar Partida
            Button(onClick = {
                motorJuego.reiniciar()
                refrescar++
            }) { Text("♻️")}
        }

        // Elemento técnico: Este Text invisible está aquí para "engañar" a Compose.
        // Al leer la variable 'refrescar', Compose registra que esta pantalla depende de ella,
        // garantizando que se redibuje cada vez que 'refrescar' cambie.
        Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
    }
}