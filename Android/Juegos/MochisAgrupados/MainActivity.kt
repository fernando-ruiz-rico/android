package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Arrancamos directamente nuestra App sin capas extra que puedan confundir
        setContent {
            AppMochisAgrupados()
        }
    }
}

// Nuestro menú de opciones disponible
enum class EstadoPantalla {
    MENU, 
    JUEGO_ZEN, 
    JUEGO_FISICAS, 
    JUEGO_ARCADE
}

@Composable
fun AppMochisAgrupados() {
}