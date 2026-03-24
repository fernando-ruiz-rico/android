/**
 * =========================================================================================
 * ARCHIVO: MainActivity.kt
 * =========================================================================================
 * PROPÓSITO:
 * Actuar como la puerta principal (Entry Point) de la aplicación y gestionar las pantallas.
 *
 * QUÉ HACE EL CÓDIGO:
 * Configura la interfaz de usuario inicial utilizando Jetpack Compose. Controla el flujo
 * lógico de navegación entre el menú principal, los cuestionarios de estrés y la
 * ejecución de los propios minijuegos embebidos en `AndroidView`.
 * Además, contiene la lógica para guardar y recuperar el historial de estrés del usuario
 * en la memoria persistente del dispositivo.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Jetpack Compose: El sistema moderno de Android para crear interfaces gráficas mediante
 * código Kotlin (sin usar XML). Aprenderéis a usar @Composable, Column, Row, Slider y Buttons.
 * 2. Gestión de Estado (State): Uso de `mutableStateOf` y `remember`. Al cambiar el valor
 * de una de estas variables, la pantalla entera se redibuja sola automáticamente.
 * 3. Almacenamiento Persistente (SharedPreferences): Cómo guardar datos sencillos (como
 * el historial de estrés) en la memoria interna del teléfono para que no se borren.
 * 4. Interoperabilidad (AndroidView): Cómo incrustar un juego clásico dibujado en un
 * `Canvas` tradicional dentro de la nueva interfaz moderna de Jetpack Compose.
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enumeración que define las diferentes "pantallas" o vistas de la aplicación.
 * Es una forma estructurada y segura de saber exactamente en qué parte del flujo de la app se encuentra el usuario.
 */
enum class Pantalla { MENU, ESTRES_ANTES, JUGANDO, ESTRES_DESPUES, GRAFICOS }

/**
 * Estructura de datos sencilla (Data Class) que representa una partida finalizada.
 * @property fechaHora Cadena de texto con el momento exacto (ej. "24/03/2026 15:30").
 * @property juego El nombre del minijuego al que se jugó (incluye su emoji).
 * @property antes El nivel de estrés declarado antes de iniciar el juego (0-10).
 * @property despues El nivel de estrés tras finalizar el juego (0-10).
 */
data class RegistroEstres(val fechaHora: String, val juego: String, val antes: Int, val despues: Int)

/**
 * Plantilla de datos para registrar los minijuegos en el menú principal.
 * @property nombre Nombre visible que aparecerá en el botón del menú (ej. "⭕ JUEGO AROS").
 * @property creador Función lambda (código ejecutable) que instruye a la app sobre cómo instanciar y cargar el minijuego.
 */
data class JuegoDefinicion(val nombre: String, val creador: (Context) -> View)

/**
 * Clase principal de Android. Todo el ciclo de vida de la aplicación arranca desde aquí.
 * Hereda de [ComponentActivity] y sirve como contenedor principal para Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    /** Variable de estado que guarda en qué pantalla estamos actualmente. Al cambiar, la UI se redibuja. */
    private var pantallaActual = mutableStateOf(Pantalla.MENU)
    /** Variable de estado que guarda temporalmente qué minijuego ha elegido el usuario antes de empezar a jugar. */
    private var juegoSeleccionado = mutableStateOf<JuegoDefinicion?>(null)
    /** Variable de estado que almacena la respuesta del usuario en la primera encuesta de estrés. */
    private var nivelEstresAntes = mutableStateOf(0f)

    /**
     * Primer método del ciclo de vida de una Activity en Android.
     * Aquí le indicamos al sistema operativo qué contenido gráfico debe mostrar al abrirse.
     *
     * @param savedInstanceState Datos recuperados si la actividad fue destruida y reconstruida por el sistema.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent es la puerta de entrada para usar el motor gráfico Jetpack Compose.
        setContent {
            val pantalla by pantallaActual // Leemos el estado reactivo actual

            // 'when' evalúa en qué estado nos encontramos y ejecuta el código correspondiente.
            when (pantalla) {
                Pantalla.MENU -> {
                    MenuPrincipal(
                        alElegirJuego = { juegoElegido ->
                            juegoSeleccionado.value = juegoElegido
                            pantallaActual.value = Pantalla.ESTRES_ANTES
                        },
                        alVerGraficos = {
                            pantallaActual.value = Pantalla.GRAFICOS
                        }
                    )
                }

                Pantalla.ESTRES_ANTES -> {
                    PantallaPreguntaEstres(
                        titulo = "¿Nivel de estrés ANTES de jugar?",
                        estresPrevio = null,
                        onResponder = { nivel ->
                            nivelEstresAntes.value = nivel
                            pantallaActual.value = Pantalla.JUGANDO
                        }
                    )
                }

                Pantalla.JUGANDO -> {
                    // BackHandler intercepta el botón físico o gesto "Atrás" del sistema Android
                    // para que no cierre la app por accidente, sino que ejecute nuestra lógica.
                    BackHandler { crearMenuPrincipal() }

                    val juegoActual = juegoSeleccionado.value
                    if (juegoActual != null) {
                        // AndroidView es un puente mágico. Nos permite incrustar vistas antiguas
                        // como un `Canvas` y `SurfaceView` tradicional dentro de Jetpack Compose.
                        AndroidView(
                            factory = { contexto -> juegoActual.creador(contexto) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Pantalla.ESTRES_DESPUES -> {
                    PantallaPreguntaEstres(
                        titulo = "¿Nivel de estrés DESPUÉS de jugar?",
                        estresPrevio = nivelEstresAntes.value,
                        onResponder = { nivelDespues ->
                            // El jugador completó el juego y la segunda encuesta. Guardamos los datos.
                            val nombreJuego = juegoSeleccionado.value?.nombre ?: "Juego Desconocido"
                            guardarRegistroEstres(this, nombreJuego, nivelEstresAntes.value.toInt(), nivelDespues.toInt())

                            // Reseteamos las selecciones temporales y volvemos al menú base.
                            juegoSeleccionado.value = null
                            pantallaActual.value = Pantalla.MENU
                        }
                    )
                }

                Pantalla.GRAFICOS -> {
                    BackHandler { pantallaActual.value = Pantalla.MENU }
                    PantallaGraficos(
                        context = this,
                        onVolver = { pantallaActual.value = Pantalla.MENU }
                    )
                }
            }
        }
    }

    /**
     * Lógica pública de navegación. Determina a dónde enviar al usuario si solicita "salir"
     * o cancelar su progreso actual.
     */
    fun crearMenuPrincipal() {
        if (pantallaActual.value == Pantalla.JUGANDO) {
            // Si estaba jugando y sale, le obligamos educadamente a rellenar la encuesta final.
            pantallaActual.value = Pantalla.ESTRES_DESPUES
        } else {
            // En cualquier otro caso, lo mandamos directo al menú inicial y borramos cualquier rastro.
            pantallaActual.value = Pantalla.MENU
            juegoSeleccionado.value = null
        }
    }

    /**
     * Almacenamiento permanente de datos en el móvil utilizando `SharedPreferences`.
     * En lugar de montar una base de datos compleja (SQL), guardamos un bloque de texto gigante
     * donde cada partida está separada por un punto y coma (;).
     *
     * @param context Necesario para solicitar permisos de escritura al sistema Android.
     * @param juego String con el nombre completo del minijuego jugado.
     * @param antes Valor entero de estrés previo al juego.
     * @param despues Valor entero de estrés final tras el juego.
     */
    private fun guardarRegistroEstres(context: Context, juego: String, antes: Int, despues: Int) {
        val prefs = context.getSharedPreferences("EstresPrefs", Context.MODE_PRIVATE)
        val historialAnterior = prefs.getString("historial", "") ?: ""

        // Capturamos el reloj interno del móvil y lo formateamos para lectura humana.
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaActual = formatoFecha.format(Date())

        // Ensamblamos la línea de datos separada por comas (formato CSV simplificado).
        val nuevoDato = "$fechaActual,$juego,$antes,$despues"

        // Concatenamos el historial histórico anterior con el nuevo evento.
        val nuevoHistorial = if (historialAnterior.isEmpty()) nuevoDato else "$historialAnterior;$nuevoDato"

        // Ejecutamos el guardado en el disco del teléfono.
        prefs.edit().putString("historial", nuevoHistorial).apply()
    }
}

/**
 * ======================= COMPONENTES VISUALES (COMPOSABLES) =======================
 * La etiqueta @Composable indica que esta función es un componente de UI y se encargará
 * de pintar elementos visuales en la pantalla.
 */

/**
 * Pantalla que muestra el título y la botonera con los minijuegos disponibles.
 *
 * @param alElegirJuego Callback ejecutado cuando el usuario hace tap en un minijuego concreto.
 * @param alVerGraficos Callback ejecutado cuando el usuario hace tap en el botón estadístico azul.
 */
@Composable
fun MenuPrincipal(alElegirJuego: (JuegoDefinicion) -> Unit, alVerGraficos: () -> Unit) {
    // Definimos el catálogo base de juegos inyectando su nombre y el creador de la vista.
    val juegos = listOf(
        JuegoDefinicion("⭕ JUEGO AROS") { ctx -> JuegoArosView(ctx) },
        JuegoDefinicion("🏀 JUEGO BALONCESTO") { ctx -> JuegoBaloncestoView(ctx) },
        JuegoDefinicion("👻 JUEGO PACMAN") { ctx -> JuegoPacmanView(ctx) },
        JuegoDefinicion("🎣 JUEGO PESCAR") { ctx -> JuegoPescarView(ctx) },
        JuegoDefinicion("🔺 JUEGO PIRÁMIDE") { ctx -> JuegoPiramideTriangulosView(ctx) }
    )

    // `Column` agrupa elementos uno debajo de otro verticalmente.
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE0F7FA)),
        verticalArrangement = Arrangement.Center, // Centramos los elementos en el eje Y
        horizontalAlignment = Alignment.CenterHorizontally // Centramos los elementos en el eje X
    ) {
        Text("Juegos de Agua Clásicos", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 40.dp))

        Button(
            onClick = alVerGraficos,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp)
        ) {
            Text("📊 VER GRÁFICOS DE ESTRÉS", fontSize = 16.sp, color = Color.White, modifier = Modifier.padding(8.dp))
        }

        // Bucle mágico `for` que genera visualmente un botón físico por cada juego registrado en la lista superior.
        for (juegoDef in juegos) {
            Button(
                onClick = { alElegirJuego(juegoDef) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 10.dp)
            ) {
                Text(juegoDef.nombre, fontSize = 18.sp, color = Color.White, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

/**
 * Pantalla genérica para captar el nivel de estrés del usuario con una barra deslizable (Slider).
 *
 * @param titulo Texto descriptivo de la cabecera ("Antes" o "Después").
 * @param estresPrevio Valor opcional del estrés previo. Si se proporciona, muestra el valor anterior fijado como recordatorio.
 * @param onResponder Callback que recoge el número seleccionado por el usuario y permite avanzar la aplicación.
 */
@Composable
fun PantallaPreguntaEstres(titulo: String, estresPrevio: Float?, onResponder: (Float) -> Unit) {
    // `remember` guarda el estado en memoria para que no se resetee al deslizar el dedo.
    var sliderValue by remember { mutableStateOf(5f) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE0F7FA)).padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Si venimos de terminar la partida, mostramos un recordatorio visual del estrés inicial.
        if (estresPrevio != null) {
            Text(text = "Tu estrés ANTES de jugar:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center)
            Text(text = "${estresPrevio.toInt()}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Slider(
                value = estresPrevio,
                onValueChange = {}, // Vacío porque es de solo lectura.
                valueRange = 0f..10f,
                steps = 9,
                enabled = false, // Desactivamos su interactividad táctil.
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 2.dp, modifier = Modifier.fillMaxWidth(0.6f))
            Spacer(modifier = Modifier.height(30.dp))
        }

        Text(text = titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(30.dp))

        // Número dinámico en tamaño gigante que reacciona instantáneamente al tocar el Slider.
        Text(text = "${sliderValue.toInt()}", fontSize = 60.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
        Text(text = "(0 = Relajado, 10 = Muy estresado)", color = Color.Gray, modifier = Modifier.padding(bottom = 20.dp))

        // Barra interactiva de selección de nivel de estrés.
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { onResponder(sliderValue) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text("Continuar", fontSize = 18.sp)
        }
    }
}

/**
 * Pantalla analítica. Dibuja una gráfica matemática por vectores basándose en el historial y lista las partidas jugadas.
 *
 * @param context El puente con el sistema de Android para leer datos locales.
 * @param onVolver Acción a realizar cuando el usuario quiere regresar al menú principal.
 */
@Composable
fun PantallaGraficos(context: Context, onVolver: () -> Unit) {
    val prefs = context.getSharedPreferences("EstresPrefs", Context.MODE_PRIVATE)

    // Claves de estado. `refreshKey` es un truco para obligar a la interfaz a recargarse cuando borramos los datos.
    var refreshKey by remember { mutableStateOf(0) }
    var mostrarDialogoBorrar by remember { mutableStateOf(false) }

    // Parseamos el string gigante almacenado separando primero por punto y coma, y luego por comas internas.
    val historialStr = remember(refreshKey) { prefs.getString("historial", "") ?: "" }
    val registros = if (historialStr.isEmpty()) {
        emptyList<RegistroEstres>()
    } else {
        historialStr.split(";").mapNotNull {
            val partes = it.split(",")
            when (partes.size) {
                // Formato Estándar: [FechaHora, NombreJuego, Antes, Despues]
                4 -> RegistroEstres(fechaHora = partes[0], juego = partes[1], antes = partes[2].toInt(), despues = partes[3].toInt())
                else -> null
            }
        }
    }

    // Diálogo flotante modal de Android para confirmar el borrado crítico de memoria.
    if (mostrarDialogoBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            title = { Text("¿Borrar historial?") },
            text = { Text("¿Estás seguro de que quieres borrar todos los datos de estrés? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    prefs.edit().remove("historial").apply() // Ejecutamos la destrucción en disco
                    mostrarDialogoBorrar = false
                    refreshKey++ // Al sumar 1, Compose se da cuenta de que la variable cambió y redibuja la pantalla limpia.
                }) {
                    Text("Borrar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila superior con Emojis simulando botones táctiles limpios
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🔙", fontSize = 32.sp, modifier = Modifier.clickable { onVolver() }.padding(8.dp))
            Text("Evolución de tu Estrés", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = "🗑️", fontSize = 32.sp, modifier = Modifier.clickable { mostrarDialogoBorrar = true }.padding(8.dp))
        }

        if (registros.isEmpty()) {
            Text("Juega al menos una partida para ver tus gráficos.", modifier = Modifier.padding(30.dp), color = Color.Gray)
            Spacer(modifier = Modifier.weight(1f))
        } else {
            // Leyenda informativa de colores
            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(16.dp).background(Color.Red))
                Text(" Antes", modifier = Modifier.padding(end = 20.dp))
                Box(modifier = Modifier.size(16.dp).background(Color(0xFF4CAF50)))
                Text(" Después")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =========================================================================================
            // GRÁFICA VECTORIAL MATEMÁTICA EN CANVAS
            // Traducimos datos abstractos del 0 al 10 en coordenadas X,Y dentro del recuadro blanco.
            // =========================================================================================
            Canvas(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.White)) {
                val maxPuntos = Math.max(2, registros.size)
                // Dividimos el ancho total entre la cantidad de partidas menos una para hallar el salto de paso X
                val anchoPaso = size.width / (maxPuntos - 1).toFloat()
                // La altura se divide en 10 niveles fijos (que representan el estrés)
                val altoPaso = size.height / 10f

                val pathAntes = Path()
                val pathDespues = Path()

                registros.forEachIndexed { index, registro ->
                    val x = index * anchoPaso
                    // Invertimos el Y restando, porque en los Canvas el (0,0) está arriba, no abajo.
                    val yAntes = size.height - (registro.antes * altoPaso)
                    val yDespues = size.height - (registro.despues * altoPaso)

                    // Empezamos los vectores en el primer punto
                    if (index == 0) {
                        pathAntes.moveTo(x, yAntes)
                        pathDespues.moveTo(x, yDespues)
                    } else {
                        // Trazamos línea al siguiente punto
                        pathAntes.lineTo(x, yAntes)
                        pathDespues.lineTo(x, yDespues)
                    }

                    // Marcadores circulares para cada partida específica
                    drawCircle(color = Color.Red, radius = 8f, center = Offset(x, yAntes))
                    drawCircle(color = Color(0xFF4CAF50), radius = 8f, center = Offset(x, yDespues))
                }

                // Dibujamos las líneas gruesas de 5 píxeles de grosor.
                drawPath(path = pathAntes, color = Color.Red, style = Stroke(width = 5f))
                drawPath(path = pathDespues, color = Color(0xFF4CAF50), style = Stroke(width = 5f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Historial Detallado", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(10.dp))

            // `LazyColumn` es la versión avanzada e hiper-eficiente para listas con desplazamiento infinito.
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Invertimos (.reversed) para que la partida más fresca y reciente aparezca en la parte superior.
                items(registros.reversed()) { registro ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = registro.juego, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = registro.fechaHora, color = Color.Gray, fontSize = 14.sp)
                                Text(
                                    text = "${registro.antes}  ➔  ${registro.despues}",
                                    fontWeight = FontWeight.Bold,
                                    // Feedback visual: Verde si ayudamos a rebajar la tensión. Rojo si el juego estresó más.
                                    color = if (registro.despues < registro.antes) Color(0xFF4CAF50) else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}