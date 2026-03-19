/**
 * ==============================================================================
 * INTERFAZ GRÁFICA: JUEGOS UNIFICADOS (COMPOSE)
 * ==============================================================================
 * Objetivo del programa:
 * Centralizar 4 motores de juego distintos y conectarlos visualmente al usuario
 * a través del moderno sistema de UI de Android (Jetpack Compose). Define pantallas,
 * botones, tableros construidos a partir de matrices y bucles de juego asíncronos.
 *
 * Qué aprenderás de Kotlin/Jetpack Compose con este código:
 * 1. State Hoisting (Elevación de estado): Cómo pasar funciones `cambiarPantalla` 
 * por parámetro para controlar la navegación desde hijos hacia el padre.
 * 2. Truco de Recomposición Manual (`refrescar`): Se usa un entero mutable que 
 * incrementa para forzar a Compose a redibujar un juego (técnica manual cuando 
 * las listas internas de un objeto no son observadas nativamente por Compose).
 * 3. Renderizado Condicional: Construcción visual de cuadrículas (Rows y Columns)
 * leyendo directamente matrices de datos lógicas de los motores de juego.
 * 4. LaunchedEffect & Corrutinas: Creación de un "Timer" o pulso de juego asíncrono 
 * para mover cosas en tiempo real sin congelar la pantalla.
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Actividad principal de Android. Punto de entrada de la app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppJuegosUnificados()
        }
    }
}

/**
 * Enumerador utilizado como enrutador (Router) para saber qué vista mostrar.
 */
enum class EstadoPantalla {
    MENU,
    CONECTA_4,
    HUNDIR_FLOTA,
    INVASORES,
    DAMAS
}

/**
 * Controlador de navegación (Router/Host) que decide qué `Composable` se pinta.
 */
@Composable
fun AppJuegosUnificados() {
    // Almacena la pantalla visible y redibuja toda la App cuando este valor cambia.
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

/**
 * Composable reutilizable para crear los botones estéticos del menú principal.
 */
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

/**
 * Pantalla que muestra la lista de juegos disponibles.
 */
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

/**
 * Pantalla gráfica que enlaza visualmente con el motor de [JuegoConecta4].
 */
@Composable
fun PantallaConecta4(volverAlMenu: () -> Unit) {
    // Se inicializa el motor lógico y se sobrevive a los redibujados (remember)
    var motorJuego = remember { JuegoConecta4() }
    
    // TRUCO DE RECOMPOSICIÓN: Como Compose no observa nativamente los cambios
    // de una matriz normal (tablero), usamos un contador que se incrementa.
    // Al leerlo al final de esta función, forzamos a redibujar el tablero modificado.
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

        // UI Condicional: Si no hay dificultad, mostramos botones de selección.
        if (motorJuego.dificultadSeleccionada == null) {
            Text(motorJuego.mensaje, modifier = Modifier.padding(bottom = 16.dp))
            JuegoConecta4.Dificultad.values().forEach { nivel ->
                Button(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        motorJuego.iniciarPartida(nivel)
                        refrescar++ // Fuerza el repintado
                    }
                ) {
                    Text(nivel.descripcion)
                }
            }
        } else {
            // UI Condicional: El juego está activo
            Text(text = motorJuego.mensaje, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            // DIBUJO DEL TABLERO: Creando Columnas y Filas anidadas leyendo datos puros.
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
                                    .aspectRatio(1f) // Para que los agujeros sean perfectamente cuadrados/circulares
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

            // DIBUJO DE CONTROLES
            if (!motorJuego.juegoTerminado) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    for (c in 0 until JuegoConecta4.COLUMNAS) {
                        Button(
                            onClick = {
                                motorJuego.turno(c) // Envía la acción al motor
                                refrescar++ // Fuerza actualización
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
        // Hack necesario: Leer explícitamente la variable de recomposición aquí
        // para asegurar que Compose la trackea como una dependencia gráfica.
        Text(text = "", modifier = Modifier.size(if (refrescar > 0) 0.dp else 0.dp))
    }
}

/**
 * Pantalla gráfica que enlaza visualmente con el motor de [JuegoHundirFlota].
 */
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
                            
                            // LÓGICA DE OCULTACIÓN: Si el usuario no ha destruido ni acabado, se esconde el barco.
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
                                        motorJuego.turno(f, c) // Interactividad al tocar casilla
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

/**
 * Pantalla gráfica que enlaza visualmente con el motor de [JuegoInvasores].
 */
@Composable
fun PantallaInvasores(volverAlMenu: () -> Unit) {
    val motorJuego = remember { JuegoInvasores() }
    var refrescar by remember { mutableStateOf(0) }
    var juegoEnMarcha by remember { mutableStateOf(false) }

    // BUCLE DE JUEGO (GAME LOOP) EN UI:
    // LaunchedEffect corre de fondo de forma asíncrona sin bloquear la pantalla principal.
    // Al cambiar juegoEnMarcha a true, arranca el timer y manda pulsos al motor lógico.
    LaunchedEffect(juegoEnMarcha) {
        while (juegoEnMarcha) {
            // Ahora cogemos la constante a través de la clase (Companion Object)
            delay(JuegoInvasores.INTERVALO_MOVIMIENTO) // Pausa de ejecución
            motorJuego.turno("TICK") // Envía la instrucción de pulso automático
            refrescar++
            if (motorJuego.vidas <= 0) {
                juegoEnMarcha = false // Para el bucle si pierdes
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

        // PANTALLA VISUAL (Representación de Texto Monospace de los objetos)
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text (
                text = motorJuego.obtenerMapaComoTexto(),
                fontFamily = FontFamily.Monospace, // Clave para mantener la cuadrícula ASCII ordenada
                fontSize = 24.sp,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = motorJuego.mensaje, color = MaterialTheme.colorScheme.error, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // BOTONES DE CONTROL DE NAVE
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

        // BOTONES DE CONTROL DE ESTADO (PAUSA/PLAY)
        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            Button(
                onClick = {
                    if (motorJuego.vidas <= 0) {
                        motorJuego.reiniciar()
                        refrescar++
                    }
                    juegoEnMarcha = true // Reactiva el LaunchedEffect
                },
                enabled = !juegoEnMarcha || motorJuego.vidas <= 0
            ) { Text("▶️")}

            Button(
                onClick = { juegoEnMarcha = false }, // Detiene el LaunchedEffect
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

/**
 * Pantalla estéticamente vacía esperando enlazar con el motor [JuegoDamas].
 */
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