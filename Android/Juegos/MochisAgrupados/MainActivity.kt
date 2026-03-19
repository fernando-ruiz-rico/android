/**
 * ==============================================================================
 * APLICACIÓN UNIFICADA: COLECCIÓN DE JUEGOS MOCHIS (Punto de entrada)
 * ==============================================================================
 * VERSIÓN SIMPLIFICADA PARA PRINCIPIANTES
 * * Cambios realizados para facilitar la lectura:
 * - Se han quitado las capas sobrantes (MaterialTheme, Surface) en el arranque.
 * - Se usan nombres explícitos ('cambiarPantalla', 'pantallaDestino') en lugar
 * de atajos de Kotlin como 'it' o prefijos 'on', para que el flujo de datos
 * sea evidente al leerlo.
 * - ¡Hemos creado nuestro propio componente 'BotonJuego' para no repetir código!
 * ==============================================================================
 */
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AppMochisUnificada()
        }
    }
}

enum class EstadoPantalla {
    MENU,
    JUEGO_ZEN,
    JUEGO_FISICAS,
    JUEGO_ARCADE
}

@Composable
fun AppMochisUnificada() {
    var pantallaActual by remember { mutableStateOf(EstadoPantalla.MENU)}

    when (pantallaActual) {
        EstadoPantalla.MENU -> MenuPrincipal(
            cambiarPantalla = { pantallaDestino -> pantallaActual = pantallaDestino }
        )

        EstadoPantalla.JUEGO_ZEN -> PantallaZen { pantallaActual = EstadoPantalla.MENU }
        EstadoPantalla.JUEGO_FISICAS -> PantallaFisicas { pantallaActual = EstadoPantalla.MENU }
        EstadoPantalla.JUEGO_ARCADE -> PantallaArcade { pantallaActual = EstadoPantalla.MENU }
    }
}

@Composable
fun MenuPrincipal(cambiarPantalla: (EstadoPantalla) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BotonJuego(texto = "Modo Zen",
            color = Color(0xFF4CAF50)) {
            cambiarPantalla(EstadoPantalla.JUEGO_ZEN)
        }
        BotonJuego(texto = "Modo Físicas",
            color = Color(0xFF2196F3)) {
            cambiarPantalla(EstadoPantalla.JUEGO_FISICAS)
        }
        BotonJuego(texto = "Modo Arcade",
            color = Color(0xFFE91E63)) {
            cambiarPantalla(EstadoPantalla.JUEGO_ARCADE)
        }
    }
}

@Composable
fun BotonJuego(texto:String, color:Color, alHacerClic: () -> Unit) {
    Button(
        onClick = alHacerClic,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = texto, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
    }
}