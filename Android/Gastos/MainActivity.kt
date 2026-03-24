/**
 * =========================================================================================
 * ARCHIVO: MainActivity.kt
 * =========================================================================================
 * PROPÓSITO:
 * Ser el cerebro y el chasis de la aplicación MyBudget (Gestor de Finanzas Personales).
 *
 * QUÉ HACE EL CÓDIGO:
 * Configura las pantallas principales (Dashboard, Nuevo Gasto, Historial) mediante
 * Jetpack Compose. Implementa la lógica de estado: si añadimos un gasto,
 * la interfaz debe reaccionar inmediatamente (Estado Reactivo).
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Arquitectura de Navegación por Estado: En vez de saltar de Activity en Activity,
 * cambiamos `pantallaActual` y Compose redibuja la pantalla entera (Single Page App).
 * 2. Lógica Matemática de Negocio: Cómo calcular promedios, buscar máximos y hacer
 * predicciones básicas a partir de una simple lista de objetos `Gasto`.
 * 3. Filtrado Avanzado: Uso de `filter` y `sortedByDescending` de Kotlin para
 * alterar cómo se muestran los datos en tiempo real según lo que el usuario pulse.
 * 4. Interacción con Android Nativo: Uso de `DatePickerDialog` para seleccionar fechas.
 * =========================================================================================
 */
package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Enum para controlar exactamente en qué rincón de la app nos encontramos. */
enum class Pantalla { DASHBOARD, NUEVO_GASTO, HISTORIAL }

class MainActivity : ComponentActivity() {

    // Presupuesto mensual fijo (Objetivo específico del PDF)
    private val PRESUPUESTO_MENSUAL = 1000f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gestorDatos = GestorDatos(this)

        setContent {
            // ESTADO REACTIVO: Si esto cambia, TODA la pantalla se repinta al instante.
            var pantallaActual by remember { mutableStateOf(Pantalla.DASHBOARD) }
            var listaGastos by remember { mutableStateOf(gestorDatos.obtenerHistorialGastos()) }

            when (pantallaActual) {
                Pantalla.DASHBOARD -> {
                    PantallaDashboard(
                        gastos = listaGastos,
                        presupuesto = PRESUPUESTO_MENSUAL,
                        onNavegarNuevo = { pantallaActual = Pantalla.NUEVO_GASTO },
                        onNavegarHistorial = { pantallaActual = Pantalla.HISTORIAL }
                    )
                }

                Pantalla.NUEVO_GASTO -> {
                    BackHandler { pantallaActual = Pantalla.DASHBOARD }

                    PantallaNuevoGasto(
                        onGuardar = { concepto, cantidad, categoria, fecha ->
                            val nuevoGasto = Gasto(
                                id = System.currentTimeMillis(),
                                concepto = concepto,
                                cantidad = cantidad,
                                categoria = categoria,
                                fecha = fecha
                            )

                            gestorDatos.agregarGasto(nuevoGasto)
                            listaGastos = gestorDatos.obtenerHistorialGastos()

                            Toast.makeText(this, "Gasto guardado", Toast.LENGTH_SHORT).show()
                            pantallaActual = Pantalla.DASHBOARD
                        },
                        onCancelar = { pantallaActual = Pantalla.DASHBOARD }
                    )
                }

                Pantalla.HISTORIAL -> {
                    BackHandler { pantallaActual = Pantalla.DASHBOARD }

                    PantallaHistorial(
                        gastos = listaGastos,
                        onVolver = { pantallaActual = Pantalla.DASHBOARD },
                        onBorrarTodo = {
                            gestorDatos.borrarTodoElHistorial()
                            listaGastos = gestorDatos.obtenerHistorialGastos()
                        }
                    )
                }
            }
        }
    }
}

// =========================================================================================
// COMPOSABLES DE INTERFAZ (UI)
// =========================================================================================

/**
 * Pantalla principal. Muestra el resumen analítico financiero avanzado (Dashboard).
 */
@Composable
fun PantallaDashboard(gastos: List<Gasto>, presupuesto: Float, onNavegarNuevo: () -> Unit, onNavegarHistorial: () -> Unit) {
    // =========================================================================
    // LÓGICA DE NEGOCIO: Cálculos Analíticos extraídos del PDF
    // =========================================================================
    val totalGastado = gastos.map { it.cantidad }.sum()
    val porcentajePresupuesto = (totalGastado / presupuesto) * 100

    // 1. Promedio Diario: Dividimos el total entre los días distintos en los que se ha gastado.
    val diasDistintos = gastos.map { it.fecha }.distinct().size
    val promedioDiario = if (diasDistintos > 0) totalGastado / diasDistintos else 0f

    // 2. Predicción de Flujo de Caja: Estimación simple a 30 días vista.
    val prediccionMensual = promedioDiario * 30

    // 3. Categoría Principal: Buscamos qué categoría acumula la mayor suma de dinero.
    val categoriaPrincipal = gastos.groupBy { it.categoria }
        .mapValues { entrada -> entrada.value.sumOf { it.cantidad.toDouble() } }
        .maxByOrNull { it.value }?.key ?: "Ninguna"

    // =========================================================================
    // INTERFAZ GRÁFICA
    // =========================================================================
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4F8)).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Dashboard", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(15.dp))

        // Tarjeta principal de Resumen Financiero
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            // SOLUCIÓN AL CENTRADO: Añadir fillMaxWidth() a la Column interna
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Gastado", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Text(
                    text = String.format("%.2f €", totalGastado),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalGastado > presupuesto) Color.Red else Color(0xFF4CAF50),
                    textAlign = TextAlign.Center
                )
                Text("de ${presupuesto.toInt()} € (Presupuesto Mensual)", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fila con KPIs (Key Performance Indicators) Analíticos
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Tarjeta Promedio Diario
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Promedio/Día", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Text(String.format("%.1f €", promedioDiario), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2196F3), textAlign = TextAlign.Center)
                }
            }
            // Tarjeta Categoría Top
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mayor Gasto", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Text(categoriaPrincipal, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFFF9800), textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Alerta de Presupuesto (Lógica del PDF: Alerta al 80%) y Predicción
        if (porcentajePresupuesto >= 80f) {
            val colorAlerta = if (porcentajePresupuesto >= 100f) Color.Red else Color(0xFFFF9800)
            val textoAlerta = if (porcentajePresupuesto >= 100f) "¡Superaste tu límite!" else "¡Atención! Al 80% del presupuesto."
            Box(modifier = Modifier.fillMaxWidth().background(colorAlerta.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                Text(textoAlerta, color = colorAlerta, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        } else if (gastos.isNotEmpty()) {
            // Si vamos bien, mostramos la predicción de flujo de caja
            Text("Predicción a fin de mes: ${String.format("%.2f €", prediccionMensual)}", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Llamada a los motores de Gráficos (Definidos en Graficos.kt)
        Text("Análisis Visual", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(15.dp))

        // =========================================================================
        // GRÁFICOS UNO DEBAJO DEL OTRO (Columna en vez de LazyRow)
        // =========================================================================
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(15.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Por Categoría", fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        GraficoCircularGastos(gastos = gastos)
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(15.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tendencia Diaria (Barras)", fontWeight = FontWeight.Bold, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        GraficoBarrasGastos(gastos = gastos)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Botonera de Navegación
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onNavegarHistorial, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)), modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Ver Historial")
            }
            Button(onClick = onNavegarNuevo, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)), modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("+ Añadir Gasto")
            }
        }
    }
}

/**
 * Formulario de captura de datos con teclado numérico y selector de fechas.
 */
@Composable
fun PantallaNuevoGasto(onGuardar: (String, Float, String, String) -> Unit, onCancelar: () -> Unit) {
    var concepto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf(CategoriaGasto.OTROS.nombre) }

    // =========================================================================
    // LÓGICA DE SELECCIÓN DE FECHA (DATEPICKER)
    // =========================================================================
    val context = LocalContext.current
    val calendario = java.util.Calendar.getInstance()

    // Por defecto marcamos la fecha de hoy
    var fechaSeleccionada by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendario.time))
    }

    // Instanciamos el diálogo nativo de Android
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = java.util.Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            fechaSeleccionada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
        },
        calendario.get(java.util.Calendar.YEAR),
        calendario.get(java.util.Calendar.MONTH),
        calendario.get(java.util.Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrar Nuevo Gasto", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 20.dp))

        // Botón visual para seleccionar la fecha
        Text("Fecha del gasto", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold, color = Color.Gray)
        OutlinedButton(
            onClick = { datePickerDialog.show() },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(text = "📅 $fechaSeleccionada", fontSize = 16.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = concepto,
            onValueChange = { concepto = it },
            label = { Text("¿En qué lo has gastado?") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text("Categoría", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold)

        Column(modifier = Modifier.fillMaxWidth()) {
            CategoriaGasto.values().forEach { cat ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { categoriaSeleccionada = cat.nombre }.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (categoriaSeleccionada == cat.nombre),
                        onClick = { categoriaSeleccionada = cat.nombre }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = cat.nombre, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val floatCantidad = cantidad.toFloatOrNull()
                    if (concepto.isNotBlank() && floatCantidad != null && floatCantidad > 0) {
                        onGuardar(concepto, floatCantidad, categoriaSeleccionada, fechaSeleccionada)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}

/**
 * Listado infinito con sistema de Filtros Avanzados y Exportación PDF.
 */
@Composable
fun PantallaHistorial(gastos: List<Gasto>, onVolver: () -> Unit, onBorrarTodo: () -> Unit) {
    val context = LocalContext.current // Necesario para ejecutar la exportación a PDF

    var mostrarAvisoBorrado by remember { mutableStateOf(false) }

    // Variables de Estado para el Filtrado Avanzado
    var filtroCategoria by remember { mutableStateOf("Todas") }
    var ordenamiento by remember { mutableStateOf("Más Reciente") }

    // Aplicar Transformaciones Matemáticas en Tiempo Real
    val gastosFiltrados = gastos.filter {
        if (filtroCategoria == "Todas") true else it.categoria == filtroCategoria
    }.let { lista ->
        when (ordenamiento) {
            "Mayor Precio" -> lista.sortedByDescending { it.cantidad }
            "Menor Precio" -> lista.sortedBy { it.cantidad }
            else -> lista.sortedByDescending { it.id } // "Más Reciente" (por la marca de tiempo)
        }
    }

    if (mostrarAvisoBorrado) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoBorrado = false },
            title = { Text("⚠️ Peligro") },
            text = { Text("¿Estás seguro de que quieres borrar todos tus apuntes financieros?") },
            confirmButton = { TextButton(onClick = { onBorrarTodo(); mostrarAvisoBorrado = false }) { Text("Borrar", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { mostrarAvisoBorrado = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)).padding(20.dp)) {
        // Cabecera superior con botón para Exportar PDF
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("🔙", fontSize = 28.sp, modifier = Modifier.clickable { onVolver() })
            Text("Historial", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

            Row {
                // Icono para generar el Reporte PDF (llamando al Gestor de la lección anterior)
                Text("📄", fontSize = 28.sp, modifier = Modifier.clickable {
                    GestorDatos(context).exportarReportePDF(gastosFiltrados)
                }.padding(end = 15.dp))

                Text("🗑️", fontSize = 28.sp, modifier = Modifier.clickable { mostrarAvisoBorrado = true })
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // =========================================================================
        // BARRA DE HERRAMIENTAS DE FILTRADO (Sin Scroll, todo en pantalla centrado)
        // =========================================================================
        Text("Filtrar por Categoría:", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        val opcionesCat = listOf("Todas") + CategoriaGasto.values().map { it.nombre }
        val filasDeFiltros = opcionesCat.chunked(3) // Agrupamos de 3 en 3 para crear filas automáticas

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            filasDeFiltros.forEach { fila ->
                Row(modifier = Modifier.padding(bottom = 8.dp), horizontalArrangement = Arrangement.Center) {
                    fila.forEach { cat ->
                        val seleccionado = filtroCategoria == cat
                        Box(
                            modifier = Modifier.padding(horizontal = 4.dp)
                                .background(if (seleccionado) Color(0xFF2196F3) else Color.LightGray, RoundedCornerShape(16.dp))
                                .clickable { filtroCategoria = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(cat, color = if (seleccionado) Color.White else Color.Black, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
            Text("Ordenar por:", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(end = 10.dp))
            val opcionesOrden = listOf("Más Reciente", "Mayor Precio", "Menor Precio")
            opcionesOrden.forEach { opcion ->
                Text(
                    text = opcion,
                    fontSize = 12.sp,
                    fontWeight = if (ordenamiento == opcion) FontWeight.Bold else FontWeight.Normal,
                    color = if (ordenamiento == opcion) Color(0xFF2196F3) else Color.Gray,
                    modifier = Modifier.clickable { ordenamiento = opcion }.padding(end = 8.dp)
                )
            }
        }

        // =========================================================================
        // LISTADO DE RESULTADOS
        // =========================================================================
        if (gastosFiltrados.isEmpty()) {
            Text("No se encontraron gastos con estos filtros.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 40.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(gastosFiltrados) { gasto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(gasto.concepto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    val colorCat = CategoriaGasto.obtenerColorPorNombre(gasto.categoria)
                                    androidx.compose.foundation.Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = colorCat) }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${gasto.categoria} • ${gasto.fecha}", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                            Text("- ${String.format("%.2f €", gasto.cantidad)}", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}