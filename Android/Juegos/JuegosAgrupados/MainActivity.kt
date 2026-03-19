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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
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
    var pantallaActual by remember { mutableStateOf(EstadoPantalla.MENU) }

    when (pantallaActual) {
        EstadoPantalla.MENU -> MenuPrincipal(
            cambiarPantalla = { pantallaDestino -> pantallaActual = pantallaDestino }
        )

        EstadoPantalla.CONECTA_4 -> PantallaConecta4 { pantallaActual = EstadoPantalla.MENU }
        EstadoPantalla.HUNDIR_FLOTA -> PantallaHundirLaFlota { pantallaActual = EstadoPantalla.MENU }
        EstadoPantalla.INVASORES -> PantallaInvasores { pantallaActual = EstadoPantalla.MENU }
        EstadoPantalla.DAMAS -> PantallaDamas { pantallaActual = EstadoPantalla.MENU }
    }
}

@Composable
fun BotonJuego(texto: String, color: Color, alHacerClic: () -> Unit) {
    Button(
        onClick = alHacerClic,
        // fillMaxWidth() hace que el botón se estire hasta los bordes
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = texto, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
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
        Text(
            text = "🎮 Juegos Unificados",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE65100),
            modifier = Modifier.padding(bottom = 48.dp)
        )

        BotonJuego(texto = "Jugar a Conecta 4", color = Color(0xFF4CAF50)) {
            cambiarPantalla(EstadoPantalla.CONECTA_4)
        }

        BotonJuego(texto = "Jugar a Hundir la Flota", color = Color(0xFF2196F3)) {
            cambiarPantalla(EstadoPantalla.HUNDIR_FLOTA)
        }

        BotonJuego(texto = "Jugar a Invasores del Espacio", color = Color(0xFFE91E63)) {
            cambiarPantalla(EstadoPantalla.INVASORES)
        }

        BotonJuego(texto = "Jugar a las Damas", color = Color(0xFF9C27B0)) {
            cambiarPantalla(EstadoPantalla.DAMAS)
        }
    }
}

@Composable
fun PantallaConecta4(volverAlMenu: () -> Unit) {
    var motorJuego = remember { JuegoConecta4() }
    var refrescar by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = volverAlMenu,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Menú")
        }

        Text("CONECTA 4", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (motorJuego.dificultadSeleccionada == null) {
            Text(motorJuego.mensaje, modifier = Modifier.padding(bottom = 16.dp))
            JuegoConecta4.Dificultad.values().forEach { nivel ->
                Button(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        motorJuego.iniciarPartida(nivel)
                        refrescar++
                    }
                ) {
                    Text(nivel.descripcion)
                }
            }
        } else {
            Text(text = motorJuego.mensaje, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(Color(0xFF1E88E5), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (fila in 0 until JuegoConecta4.FILAS) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (columna in 0 until JuegoConecta4.COLUMNAS) {
                            val ficha = motorJuego.tablero[fila][columna]

                            Box (
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .background(Color(0xFF0D47A1), shape = androidx.compose.foundation.shape.CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ficha.simbolo, fontSize = 30.sp)
                            }
                        }
                    }
                }
            }

            if (!motorJuego.juegoTerminado) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    for (c in 0 until JuegoConecta4.COLUMNAS) {
                        Button(
                            onClick = {
                                motorJuego.turno(c)
                                refrescar++
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("▼", fontSize = 18.sp)
                        }
                    }
                }
            }
            else {
                Button(onClick = {
                    motorJuego.dificultadSeleccionada = null
                    motorJuego.mensaje = "Selecciona dificultad"
                    refrescar++
                }) {
                    Text("Jugar otra vez")
                }
            }
        }
        Text(text = "", modifier = Modifier.size(if (refrescar > 0) 0.dp else 0.dp))
    }
}

@Composable
fun PantallaHundirLaFlota(volverAlMenu: () -> Unit) {
    val motorJuego = remember { JuegoHundirFlota() }
    var refrescar by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = volverAlMenu,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Menú")
        }

        Text("HUNDIR LA FLOTA", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        if (motorJuego.juegoTerminado && motorJuego.misilesRestantes == JuegoHundirFlota.MUNICION_MAXIMA) {
            Text(motorJuego.mensaje, modifier = Modifier.padding(bottom = 16.dp))
            Button(onClick = {
                motorJuego.iniciarPartida()
                refrescar++
            }) {
                Text("Iniciar partida")
            }
        } else {
            Text(motorJuego.mensaje, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Text("Misiles: ${motorJuego.misilesRestantes} | Aciertos: ${motorJuego.aciertos}/${motorJuego.impactosNecesarios} ")

            Spacer(modifier = Modifier.height(16.dp))

            // TABLERO ESTILO RADAR / OCÉANO
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho
                    .background(Color(0xFF01579B), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)) // Borde azul oscuro
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (f in 0 until JuegoHundirFlota.DIMENSION) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (c in 0 until JuegoHundirFlota.DIMENSION) {
                            val estado = motorJuego.oceano[f][c]
                            var simbolo = estado.simbolo
                            if (estado == JuegoHundirFlota.EstadoCasilla.BARCO && !motorJuego.juegoTerminado) {
                                simbolo = JuegoHundirFlota.EstadoCasilla.AGUA.simbolo
                            }

                            val esAgua = (simbolo == JuegoHundirFlota.EstadoCasilla.AGUA.simbolo)
                            // Color más claro para el agua normal, grisáceo si ha habido un impacto/fallo
                            val colorCasilla = if (esAgua) Color(0xFF0288D1) else Color(0xFFB3E5FC)

                            Box(
                                modifier = Modifier
                                    .weight(1f) // Se reparten el espacio
                                    .aspectRatio(1f) // Casillas perfectamente cuadradas
                                    .padding(1.dp) // Pequeña separación (líneas de cuadrícula)
                                    .background(colorCasilla, shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                                    .clickable(enabled = !motorJuego.juegoTerminado) {
                                        motorJuego.turno(f, c)
                                        refrescar++
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Quitamos el emoji redundante de agua y hacemos los demás iconos más grandes
                                if (!esAgua) {
                                    Text(simbolo, fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (motorJuego.juegoTerminado) {
                Button(onClick = {
                    motorJuego.iniciarPartida()
                    refrescar++
                }) {
                    Text("Jugar otra vez")
                }
            }
        }
        Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
    }
}

@Composable
fun PantallaInvasores(volverAlMenu: () -> Unit) {
    val motorJuego = remember { JuegoInvasores() }
    var refrescar by remember { mutableStateOf(0) }
    var juegoEnMarcha by remember { mutableStateOf(false) }

    LaunchedEffect(juegoEnMarcha) {
        while (juegoEnMarcha) {
            // Ahora cogemos la constante a través de la clase (Companion Object)
            delay(JuegoInvasores.INTERVALO_MOVIMIENTO)
            motorJuego.turno("TICK")
            refrescar++
            if (motorJuego.vidas <= 0) {
                juegoEnMarcha = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = volverAlMenu,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Menú")
        }

        Text("INVASORES DEL ESPACIO", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        Text(text = "PUNTOS ${motorJuego.puntos} | VIDAS ${motorJuego.vidas} | OLEADA: ${motorJuego.numeroOleada}", fontSize = 16.sp)
        Text(text = "BOMBAS DISPONIBLES: ${motorJuego.bombasMasivas}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(16.dp))

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text (
                text = motorJuego.obtenerMapaComoTexto(),
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = motorJuego.mensaje, color = MaterialTheme.colorScheme.error, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            Button(onClick = {
                motorJuego.turno("IZQUIERDA")
                refrescar++
            }) { Text("👈")}
            Button(onClick = {
                motorJuego.turno("FUEGO")
                refrescar++
            }) { Text("♦️")}
            Button(onClick = {
                motorJuego.turno("DERECHA")
                refrescar++
            }) { Text("👉")}
        }

        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            Button(
                onClick = {
                    if (motorJuego.vidas <= 0) {
                        motorJuego.reiniciar()
                        refrescar++
                    }
                    juegoEnMarcha = true
                },
                enabled = !juegoEnMarcha || motorJuego.vidas <= 0
            ) { Text("▶️")}

            Button(
                onClick = { juegoEnMarcha = false },
                enabled = juegoEnMarcha
            ) { Text("⏸️")}

            Button(onClick = {
                motorJuego.turno("BOMBA")
                refrescar++
            }) { Text("🧨")}
        }
        Text(text = "", modifier = Modifier.size(if(refrescar > 0) 0.dp else 0.dp))
    }
}

@Composable
fun PantallaDamas(volverAlMenu: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = volverAlMenu,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Menú")
        }

        Text("DAMAS", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
    }
}