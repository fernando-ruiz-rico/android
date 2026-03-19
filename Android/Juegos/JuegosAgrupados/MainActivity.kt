package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppJuegosUnificados()
        }
    }
}

enum class EstadoPantalla {
    MENU,
    CONECTA_4,
    HUNDIR_FLOTA,
    INVASORES,
    DAMAS
}

@Composable
fun AppJuegosUnificados() {
}

@Composable
fun MenuPrincipal(cambiarPantalla: (EstadoPantalla) -> Unit) {
}

@Composable
fun PantallaConecta4(volverAlMenu: () -> Unit) {
}

@Composable
fun PantallaHundirLaFlota(volverAlMenu: () -> Unit) {
}

@Composable
fun PantallaInvasores(volverAlMenu: () -> Unit) {
}

@Composable
fun PantallaDamas(volverAlMenu: () -> Unit) {
}