/**
 * =========================================================================================
 * ARCHIVO: Graficos.kt
 * =========================================================================================
 * PROPÓSITO:
 * Convertir datos numéricos aburridos en representaciones visuales atractivas e inteligibles.
 *
 * QUÉ HACE EL CÓDIGO:
 * Contiene dos motores gráficos basados en Jetpack Compose Canvas:
 * 1. Gráfico Circular (Pie Chart) para ver el reparto de dinero por categorías con su leyenda.
 * 2. Gráfico de Barras Verticales para analizar la tendencia de gastos diarios.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Native Canvas Text: Cómo escapar del entorno moderno de Compose para usar los pinceles
 * de texto clásicos de Android (`android.graphics.Paint`) y escribir rótulos sobre figuras.
 * =========================================================================================
 */
package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * =========================================================================================
 * GRÁFICO 1: GRÁFICO CIRCULAR (REPARTO POR CATEGORÍA CON LEYENDA)
 * =========================================================================================
 */
@Composable
fun GraficoCircularGastos(gastos: List<Gasto>) {
    if (gastos.isEmpty()) {
        Text("Sin datos", color = Color.Gray, modifier = Modifier.padding(20.dp))
        return
    }

    val totalGastado = gastos.sumOf { it.cantidad.toDouble() }.toFloat()

    if (totalGastado == 0f) return

    val gastosPorCategoria = gastos.groupBy { it.categoria }
    val sumasPorCategoria = gastosPorCategoria.mapValues { entrada ->
        entrada.value.sumOf { it.cantidad.toDouble() }.toFloat()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // DIBUJO DEL CÍRCULO MATEMÁTICO (Aumentamos tamaño de 150 a 220 dp)
        Canvas(modifier = Modifier.size(220.dp).padding(16.dp)) {
            var anguloInicioActual = 0f
            for ((nombreCategoria, suma) in sumasPorCategoria) {
                val porcentaje = suma / totalGastado
                val gradosDelTrozo = porcentaje * 360f
                val colorTrozo = CategoriaGasto.obtenerColorPorNombre(nombreCategoria)

                drawArc(
                    color = colorTrozo,
                    startAngle = anguloInicioActual,
                    sweepAngle = gradosDelTrozo,
                    useCenter = true
                )
                anguloInicioActual += gradosDelTrozo
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // LEYENDA DETALLADA INFERIOR
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            for ((nombreCategoria, suma) in sumasPorCategoria) {
                val porcentaje = (suma / totalGastado) * 100
                val colorCaja = CategoriaGasto.obtenerColorPorNombre(nombreCategoria)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            drawRect(color = colorCaja)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = nombreCategoria, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    Text(
                        text = String.format("%.2f € (%.1f%%)", suma, porcentaje),
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

/**
 * =========================================================================================
 * GRÁFICO 2: GRÁFICO DE BARRAS (TENDENCIA DIARIA CON RÓTULOS DE TEXTO)
 * =========================================================================================
 */
@Composable
fun GraficoBarrasGastos(gastos: List<Gasto>) {
    if (gastos.isEmpty()) {
        Text("Sin datos", color = Color.Gray, modifier = Modifier.padding(20.dp))
        return
    }

    val gastosPorDia = gastos.groupBy { it.fecha }
        .mapValues { entrada -> entrada.value.sumOf { it.cantidad.toDouble() }.toFloat() }

    val ultimosDias = gastosPorDia.entries.toList().takeLast(5)
    val maximoGastoDiario = ultimosDias.maxByOrNull { it.value }?.value ?: 1f

    // Configuramos los pinceles clásicos de Android para escribir texto
    val paintTextoFecha = android.graphics.Paint().apply {
        color = android.graphics.Color.GRAY
        textSize = 30f
        textAlign = android.graphics.Paint.Align.CENTER
    }

    val paintTextoMonto = android.graphics.Paint().apply {
        color = android.graphics.Color.DKGRAY
        textSize = 32f
        isFakeBoldText = true
        textAlign = android.graphics.Paint.Align.CENTER
    }

    // Aumentamos la altura de este Canvas y dejamos "padding" superior e inferior para que quepan las letras
    Canvas(modifier = Modifier.fillMaxWidth().height(220.dp).padding(top = 30.dp, bottom = 40.dp, start = 16.dp, end = 16.dp)) {
        val anchoTotal = size.width
        val altoTotal = size.height

        val espacioPorBarra = anchoTotal / ultimosDias.size
        val anchoBarra = espacioPorBarra * 0.6f

        ultimosDias.forEachIndexed { index, entrada ->
            val fecha = if (entrada.key.length >= 5) entrada.key.substring(0, 5) else entrada.key
            val cantidadGastada = entrada.value

            val porcentajeAltura = cantidadGastada / maximoGastoDiario
            val alturaBarra = altoTotal * porcentajeAltura

            val posX = (index * espacioPorBarra) + (espacioPorBarra - anchoBarra) / 2
            val posY = altoTotal - alturaBarra

            // 1. Dibujar la columna (Barra azul)
            drawRect(
                color = Color(0xFF2196F3),
                topLeft = Offset(posX, posY),
                size = Size(anchoBarra, alturaBarra)
            )

            // 2. Usar NativeCanvas para escribir la FECHA justo debajo de la barra
            drawContext.canvas.nativeCanvas.drawText(
                fecha,
                posX + anchoBarra / 2, // Centrado respecto a la barra
                altoTotal + 35f, // 35 píxeles más abajo del borde inferior
                paintTextoFecha
            )

            // 3. Usar NativeCanvas para escribir la CANTIDAD (En Euros) coronando la barra
            drawContext.canvas.nativeCanvas.drawText(
                "${cantidadGastada.toInt()}€",
                posX + anchoBarra / 2,
                posY - 15f, // 15 píxeles por encima del borde superior de la barra
                paintTextoMonto
            )
        }
    }
}