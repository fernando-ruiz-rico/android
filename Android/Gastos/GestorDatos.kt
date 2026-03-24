/**
 * =========================================================================================
 * ARCHIVO: GestorDatos.kt
 * =========================================================================================
 * PROPÓSITO:
 * Actuar como el "Disco Duro" de nuestra aplicación para que los datos sobrevivan
 * al cerrar la app, e implementar la exportación de documentos físicos (PDF).
 *
 * QUÉ HACE EL CÓDIGO:
 * Para mantener el código amigable para principiantes, usamos `SharedPreferences`
 * (como en el minijuego de estrés) en lugar de arquitecturas complejas como Room/SQL.
 * Además, contiene el motor de renderizado de PDFs.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Rutas Públicas de Android: Cómo guardar archivos en la carpeta de "Descargas"
 * para que el usuario pueda verlos con su gestor de archivos normal.
 * 2. StrictMode Hack: Un pequeño "truco" educativo para forzar la apertura de un
 * archivo físico con un visor externo sin usar configuraciones complejas de `FileProvider`.
 * =========================================================================================
 */
package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class GestorDatos(private val context: Context) {

    private val prefs = context.getSharedPreferences("MyBudgetPrefs", Context.MODE_PRIVATE)

    fun obtenerHistorialGastos(): List<Gasto> {
        val historialStr = prefs.getString("gastos_db", "") ?: ""
        if (historialStr.isEmpty()) return emptyList()

        return historialStr.split(";").mapNotNull { bloqueTexto ->
            val partes = bloqueTexto.split("|")
            if (partes.size == 5) {
                try {
                    Gasto(
                        id = partes[0].toLong(),
                        concepto = partes[1],
                        cantidad = partes[2].toFloat(),
                        categoria = partes[3],
                        fecha = partes[4]
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    fun agregarGasto(nuevoGasto: Gasto) {
        val historialActual = prefs.getString("gastos_db", "") ?: ""
        val nuevoDatoStr = "${nuevoGasto.id}|${nuevoGasto.concepto}|${nuevoGasto.cantidad}|${nuevoGasto.categoria}|${nuevoGasto.fecha}"
        val nuevoHistorial = if (historialActual.isEmpty()) nuevoDatoStr else "$historialActual;$nuevoDatoStr"
        prefs.edit().putString("gastos_db", nuevoHistorial).apply()
    }

    fun borrarTodoElHistorial() {
        prefs.edit().remove("gastos_db").apply()
    }

    /**
     * =====================================================================================
     * MOTOR DE EXPORTACIÓN PDF
     * Crea un archivo físico .pdf recorriendo la lista de gastos.
     * =====================================================================================
     */
    fun exportarReportePDF(gastos: List<Gasto>) {
        val documentoPdf = PdfDocument()
        val infoPagina = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val pagina = documentoPdf.startPage(infoPagina)
        val canvas: Canvas = pagina.canvas

        val paintTitulo = Paint().apply {
            color = Color.parseColor("#2196F3")
            textSize = 24f
            isFakeBoldText = true
        }

        val paintTexto = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }

        val paintLinea = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        canvas.drawText("REPORTE DE GASTOS - MyBudget", 50f, 60f, paintTitulo)
        canvas.drawLine(50f, 80f, 545f, 80f, paintLinea)

        var posY = 120f

        for (gasto in gastos) {
            val textoGasto = "${gasto.fecha} | ${gasto.categoria.uppercase()} | ${gasto.concepto}"
            val textoPrecio = String.format("%.2f €", gasto.cantidad)

            canvas.drawText(textoGasto, 50f, posY, paintTexto)
            canvas.drawText(textoPrecio, 480f, posY, paintTexto)

            posY += 30f

            if (posY > 800f) {
                canvas.drawText("... (Sigue en la siguiente página)", 50f, posY, paintTexto)
                break
            }
        }

        documentoPdf.finishPage(pagina)

        // 6. Guardado en Memoria Física del Teléfono
        try {
            // SOLUCIÓN: Usamos la carpeta pública DIRECTORY_DOWNLOADS. Así el alumno
            // podrá ir a su app "Mis Archivos" -> "Descargas" y ver el PDF inmediatamente.
            val directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Añadimos la hora al nombre para no sobreescribir el mismo archivo siempre.
            val nombreArchivo = "Reporte_Gastos_${System.currentTimeMillis()}.pdf"
            val archivoFisico = File(directorio, nombreArchivo)

            val fos = FileOutputStream(archivoFisico)
            documentoPdf.writeTo(fos)

            documentoPdf.close()
            fos.close()

            Toast.makeText(context, "PDF guardado en la carpeta Descargas", Toast.LENGTH_LONG).show()

            // =====================================================================================
            // AUTO-ABRIR EL PDF (Hack educativo)
            // Relaja las políticas estrictas de Android para permitirnos abrir el archivo
            // directamente usando la aplicación predeterminada de lectura de PDFs del móvil.
            // =====================================================================================
            android.os.StrictMode.setVmPolicy(android.os.StrictMode.VmPolicy.Builder().build())
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(android.net.Uri.fromFile(archivoFisico), "application/pdf")
                flags = android.content.Intent.FLAG_ACTIVITY_NO_HISTORY or android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val chooser = android.content.Intent.createChooser(intent, "Abrir PDF con...")
            context.startActivity(chooser)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
        }
    }
}