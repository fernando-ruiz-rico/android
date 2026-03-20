/**
 * ==============================================================================
 * APLICACIÓN UNIFICADA: COLECCIÓN DE JUEGOS MOCHIS (Punto de entrada)
 * ==============================================================================
 * * Objetivo del programa:
 * Actuar como el núcleo central de la App. Muestra un menú principal desde 
 * el que podemos navegar a nuestros 3 distintos minijuegos.
 * * Qué aprenderás de Kotlin y programación con este código:
 * 1. Arquitectura y Navegación Básica: Cómo usar variables de Estado para 
 * intercambiar pantallas enteras dentro de Compose.
 * 2. Enumeradores (Enums): Tipado estricto para definir exactamente qué 
 * pantallas existen de forma segura y evitar errores de texto.
 * 3. Hoisting de Estado: Cómo pasar funciones como parámetros (`alHacerClic`) 
 * para que un componente hijo avise a su padre.
 * ==============================================================================
 * Cambios realizados para facilitar la lectura y comprensión del código:
 * - Se han quitado las capas de MaterialTheme y Surface en el arranque.
 * - Se usan nombres explícitos ('cambiarPantalla', 'pantallaDestino') en lugar
 * de atajos de Kotlin como 'it' o prefijos 'on', para que el flujo de datos
 * sea evidente al leerlo.
 * - Hemos creado nuestro propio componente 'BotonJuego' para no repetir código.
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

/**
 * Punto de entrada del sistema Android a nuestra aplicación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Desplegamos nuestro lienzo Compose general
        setContent {
            AppMochisUnificada()
        }
    }
}

/**
 * Catálogo estricto de los "lugares" a los que podemos ir en nuestra App.
 * Al usar un enum (y no simples cadenas de texto) el compilador nos avisará
 * si nos equivocamos escribiendo el nombre de una pantalla.
 */
enum class EstadoPantalla {
    MENU,
    JUEGO_ZEN,
    JUEGO_FISICAS,
    JUEGO_ARCADE
}

/**
 * Controlador de Rutas (El "Policía de tráfico").
 * Decide qué vista enseñar en función de la variable 'pantallaActual'.
 */
@Composable
fun AppMochisUnificada() {
    // Recordamos en qué pantalla estamos. Por defecto, arrancamos en el Menú.
    var pantallaActual by remember { mutableStateOf(EstadoPantalla.MENU)}

    // El 'when' actúa como un intercambiador de vías de tren.
    when (pantallaActual) {
        // Al menú le pasamos una función para que pueda ordenar el cambio de pantalla.
        EstadoPantalla.MENU -> MenuPrincipal(
            cambiarPantalla = { pantallaDestino -> pantallaActual = pantallaDestino }
        )

        // A los juegos les pasamos una función simple: "si me das a la flecha, vuelve al menú"
        EstadoPantalla.JUEGO_ZEN -> PantallaZen { pantallaActual = EstadoPantalla.MENU }
        EstadoPantalla.JUEGO_FISICAS -> PantallaFisicas { pantallaActual = EstadoPantalla.MENU }
        EstadoPantalla.JUEGO_ARCADE -> PantallaArcade { pantallaActual = EstadoPantalla.MENU }
    }
}

/**
 * La pantalla del menú de inicio.
 *
 * @param cambiarPantalla Una lambda (función) inyectada desde fuera que le dice 
 * al componente padre hacia dónde queremos ir.
 */
@Composable
fun MenuPrincipal(cambiarPantalla: (EstadoPantalla) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0)) // Fondo naranja pastel
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Todo centrado en pantalla
    ) {
        BotonJuego(texto = "Modo Zen",
            color = Color(0xFF4CAF50)) { // Verde
            cambiarPantalla(EstadoPantalla.JUEGO_ZEN)
        }
        BotonJuego(texto = "Modo Físicas",
            color = Color(0xFF2196F3)) { // Azul
            cambiarPantalla(EstadoPantalla.JUEGO_FISICAS)
        }
        BotonJuego(texto = "Modo Arcade",
            color = Color(0xFFE91E63)) { // Rosa
            cambiarPantalla(EstadoPantalla.JUEGO_ARCADE)
        }
    }
}

/**
 * Un componente visual reutilizable que nosotros mismos hemos creado.
 * Evita que tengamos que escribir todo el 'Button' y su modificador tres veces.
 *
 * @param texto El texto del botón.
 * @param color El color de fondo del botón.
 * @param alHacerClic La acción que se ejecutará al pulsarlo.
 */
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