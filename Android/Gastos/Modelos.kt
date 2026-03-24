/**
 * =========================================================================================
 * ARCHIVO: Modelos.kt
 * =========================================================================================
 * PROPÓSITO:
 * Definir las estructuras de datos fundamentales de nuestra aplicación MyBudget.
 *
 * QUÉ HACE EL CÓDIGO:
 * Contiene las "Data Classes" (Clases de datos). Estas clases son como moldes o
 * plantillas que nos permiten empaquetar información relacionada en un solo objeto.
 * En lugar de tener variables sueltas para el precio, el nombre y la fecha, lo
 * agrupamos todo dentro de una entidad lógica llamada `Gasto`.
 *
 * LO QUE SE APRENDE EN ESTE FICHERO:
 * 1. Data Classes: Son una característica estrella de Kotlin. Automáticamente generan
 * métodos útiles por detrás (como `toString` o `equals`) sin que tengamos que programarlos.
 * 2. Enums (Enumeraciones): Usamos un `enum class` para restringir las categorías.
 * Así evitamos que el usuario escriba "Coche" un día y "Transporte" otro, forzando
 * a que siempre elija de una lista predefinida, lo que facilita los gráficos.
 * =========================================================================================
 */
package com.example.myapplication

import androidx.compose.ui.graphics.Color

/**
 * Plantilla que representa una transacción económica única.
 * @property id Identificador único numérico (usualmente basado en el tiempo exacto).
 * @property concepto Texto descriptivo introducido por el usuario (ej. "Compra Mercadona").
 * @property cantidad El valor monetario del gasto (ej. 45.50f).
 * @property categoria La clasificación del gasto, extraída de nuestro Enum.
 * @property fecha Texto con la fecha en la que se realizó (ej. "24/03/2026").
 */
data class Gasto(
    val id: Long,
    val concepto: String,
    val cantidad: Float,
    val categoria: String,
    val fecha: String
)

/**
 * Enumeración estricta de las categorías disponibles en la app.
 * Vincular cada categoría con un color específico aquí nos facilita pintar
 * el gráfico circular (Pie Chart) más adelante.
 *
 * @property nombre El texto legible que verá el usuario.
 * @property color El color vectorial de Jetpack Compose asignado a esta categoría.
 */
enum class CategoriaGasto(val nombre: String, val color: Color) {
    ALIMENTACION("Alimentación", Color(0xFF4CAF50)), // Verde
    TRANSPORTE("Transporte", Color(0xFF2196F3)),   // Azul
    HOGAR("Hogar", Color(0xFFFF9800)),             // Naranja
    OCIO("Ocio", Color(0xFF9C27B0)),               // Morado
    OTROS("Otros", Color(0xFF9E9E9E));             // Gris

    companion object {
        /**
         * Función utilitaria para buscar el color de una categoría sabiendo solo su nombre en texto.
         * Si por algún error no existe, devuelve Gris por defecto.
         */
        fun obtenerColorPorNombre(nombre: String): Color {
            return values().find { it.nombre == nombre }?.color ?: OTROS.color
        }
    }
}