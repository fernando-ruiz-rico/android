/**
 * ==============================================================================
 * MOTOR DE INTERACCIÓN: GESTOR DE MOCHIS
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo contiene la lógica para crear y gestionar "Mochis" (objetos 
 * representados por emojis) en la pantalla. Responde a las interacciones del 
 * usuario (toques) y mantiene una lista reactiva para que la interfaz gráfica 
 * se actualice automáticamente.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Data classes: Uso de `data class` para modelar estados y propiedades puras.
 * 2. Estado en Compose: Uso de `mutableStateListOf` para crear colecciones que 
 * actualizan la interfaz gráfica de forma automática al cambiar.
 * 3. Companion Objects: Uso de `companion object` para definir constantes de clase.
 * 4. Funciones de extensión de colecciones: Uso de `random()` y `removeFirstOrNull()`.
 * 5. Valores por defecto: Asignación de valores predeterminados en constructores.
 * ==============================================================================
 */
package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf

/**
 * Representa un elemento interactivo (Mochi) en la pantalla.
 * Al ser un 'data class', Kotlin la optimiza para almacenar datos de forma eficiente.
 *
 * @property x Coordenada horizontal actual en la pantalla.
 * @property y Coordenada vertical actual en la pantalla.
 * @property velocidadY Velocidad de movimiento en el eje vertical (por defecto 0f).
 * @property velocidadX Velocidad de movimiento en el eje horizontal (por defecto 0f).
 * @property radio Tamaño del área del Mochi, útil para físicas o colisiones.
 * @property emoji Símbolo visual que representará a este Mochi en la UI.
 * @property tiempoCreacion Marca de tiempo exacta de su creación (se auto-asigna al nacer).
 */
data class Mochi(
    var x: Float,
    var y: Float,
    var velocidadY: Float = 0f,
    var velocidadX: Float = 0f,
    val radio: Float = 70f,
    var emoji: String,
    val tiempoCreacion: Long = System.currentTimeMillis()
)

/**
 * Gestor principal del estado de los Mochis.
 * Se encarga de almacenar los objetos actuales y controlar la lógica de creación
 * para no saturar la memoria del dispositivo.
 */
class MotorMochis {
    
    // El 'companion object' sirve para guardar variables estáticas que pertenecen
    // a la clase en general, no a una instancia específica del MotorMochis.
    companion object {
        // Límite máximo de Mochis en pantalla para evitar problemas de rendimiento
        const val MAX_MOCHIS = 1000
    }

    // Lista reactiva especial de Jetpack Compose. 
    // Cualquier cambio aquí (añadir o quitar mochis) notificará a la interfaz 
    // gráfica para que se redibuje automáticamente.
    var mochis = mutableStateListOf<Mochi>()

    // Catálogo inmutable con todos los "disfraces" posibles para nuestros mochis
    val emojisDisponibles = listOf("🍡", "🍮", "🥟", "🍓", "🥞", "🥝", "🫒", "🥑", "🥕", "🥒", "🍥", "🥓", "🌮")

    /**
     * Crea un nuevo Mochi exactamente en la posición donde el usuario ha tocado.
     *
     * @param xToque Coordenada horizontal del toque en la pantalla.
     * @param yToque Coordenada vertical del toque en la pantalla.
     */
    fun tocar(xToque: Float, yToque: Float) {
        // Construimos una nueva entidad Mochi usando las coordenadas recibidas
        val nuevoMochi = Mochi(
            x = xToque,
            y = yToque,
            velocidadY = 0f, // Nace sin movimiento vertical
            velocidadX = 0f, // Nace sin movimiento horizontal
            emoji = emojisDisponibles.random() // Elige un emoji al azar de nuestro catálogo
        )
        
        // Lo añadimos a la lista reactiva para que aparezca inmediatamente en pantalla
        mochis.add(nuevoMochi)
        
        // Control de población: verificamos si hemos superado el límite de seguridad
        if (mochis.size > MAX_MOCHIS) {
            // Si hay demasiados, eliminamos el más antiguo (el primero que entró en la lista)
            // Usamos 'removeFirstOrNull' para evitar errores si la lista estuviera vacía
            mochis.removeFirstOrNull()
        }
    }
}