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
    JUEGO_AROS,
    JUEGO_BALONCESTO,
    JUEGO_PACMAN,
    JUEGO_PESCAR,
    JUEGO_PIRAMIDE_TRIANGULOS
}


@Composable
fun AppMochisUnificada() {

    var pantallaActual by remember { mutableStateOf(EstadoPantalla.MENU)}

    when (pantallaActual) {
        EstadoPantalla.MENU -> MenuPrincipal(
            cambiarPantalla = { pantallaDestino -> pantallaActual = pantallaDestino }
        )

        EstadoPantalla.JUEGO_AROS -> PantallaAros { pantallaActual = EstadoPantalla.MENU }
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
        verticalArrangement = Arrangement.Center // Todo centrado en pantalla
    ) {
        BotonJuego(texto = "Juego aros",
            color = Color(0xFF4CAF50)) { // Verde
            cambiarPantalla(EstadoPantalla.JUEGO_AROS)
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