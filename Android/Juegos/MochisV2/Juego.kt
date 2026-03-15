/**
 * ==============================================================================
 * MOTOR DE FÍSICAS: GESTOR DE MOCHIS AVANZADO
 * ==============================================================================
 * Objetivo del programa:
 * Esta evolución del gestor no solo crea Mochis, sino que les aplica leyes de 
 * la física como gravedad, rebote y fricción. Además, permite interactuar con 
 * los Mochis que ya están en pantalla (hacerlos saltar al tocarlos).
 *
 * Qué aprenderás de Kotlin y programación con este código:
 * 1. Funciones dentro de Data Classes: Cómo dar comportamiento a un modelo de datos.
 * 2. Matemáticas aplicadas: Uso del Teorema de Pitágoras para detectar toques.
 * 3. Físicas básicas: Implementación de gravedad, inercia, fricción y elasticidad.
 * 4. Iteración inversa: Uso de `reversed()` para interactuar con los elementos
 * que están "por encima" visualmente.
 * ==============================================================================
 */
package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf
import kotlin.math.abs
import kotlin.random.Random

/**
 * Representa un Mochi con propiedades físicas y capacidad de saber si lo han tocado.
 * @property x Coordenada horizontal actual.
 * @property y Coordenada vertical actual.
 * @property velocidadY Velocidad de movimiento en el eje vertical.
 * @property velocidadX Velocidad de movimiento en el eje horizontal.
 * @property radio Tamaño del área del Mochi para calcular colisiones.
 * @property emoji Símbolo visual que representará a este Mochi en la UI.
 * @property tiempoCreacion Marca de tiempo exacta de su creación.
 */
data class Mochi(
    var x: Float,
    var y: Float,
    var velocidadY: Float = 0f,
    var velocidadX: Float = 0f,
    val radio: Float = 70f,
    var emoji: String,
    val tiempoCreacion: Long = System.currentTimeMillis()
) {
    /**
     * Calcula si las coordenadas del dedo caen dentro del área circular de este Mochi.
     * Utiliza una variante del Teorema de Pitágoras para calcular la distancia.
     *
     * @param xDelDedo Coordenada horizontal exacta donde el usuario tocó la pantalla.
     * @param yDelDedo Coordenada vertical exacta donde el usuario tocó la pantalla.
     * @return `true` si el toque ocurrió dentro del área del Mochi, `false` en caso contrario.
     */
    fun fueTocado(xDelDedo: Float, yDelDedo: Float): Boolean {
        // Distancia en ambos ejes entre el dedo y el centro del Mochi
        val distanciaX = xDelDedo - x
        val distanciaY = yDelDedo - y

        // Calculamos la distancia al cuadrado (a² + b² = c²). 
        // Nota de rendimiento: Comparamos cuadrados en lugar de usar raíces cuadradas 
        // (Math.sqrt) porque calcular raíces consume muchos más recursos del procesador.
        val distanciaAlCuadrado = (distanciaX * distanciaX) + (distanciaY * distanciaY)
        val radioAlCuadrado = radio * radio

        // Multiplicamos por 1.5f para dar un pequeño "margen de error" al usuario
        // y que sea más fácil acertar con el dedo (hitbox ligeramente más grande).
        return distanciaAlCuadrado <= (radioAlCuadrado * 1.5f)
    }
}

/**
 * Gestor principal que aplica las reglas del mundo físico a los Mochis.
 */
class MotorMochis {
    companion object {
        const val MAX_MOCHIS = 100 // Reducido a 100 porque las físicas exigen más cálculos
    }

    // Constantes físicas del "mundo"
    private val gravedad = 0.5f       // Fuerza que tira de los mochis hacia abajo en cada fotograma
    private val elasticidad = 0.75f   // Porcentaje de energía que conservan al rebotar (75%)
    private val friccionSuelo = 0.9f  // Frena el movimiento horizontal al rozar el suelo (pierden un 10%)

    var mochis = mutableStateListOf<Mochi>()
    val emojisDisponibles = listOf("🍡", "🍮", "🥟", "🍓", "🥞", "🥝", "🫒", "🥑", "🥕", "🥒", "🍥", "🥓", "🌮")

    /**
     * Gestiona el evento de tocar la pantalla. 
     * Puede hacer saltar un mochi existente o crear uno nuevo.
     *
     * @param xToque Coordenada horizontal del toque registrado por la interfaz.
     * @param yToque Coordenada vertical del toque registrado por la interfaz.
     */
    fun tocar(xToque: Float, yToque: Float) {
        var tocoAlgunoExistente = false

        // Recorremos la lista al revés (reversed). 
        // ¿Por qué? Porque los últimos creados se dibujan "encima". Si tocamos una zona
        // donde hay dos Mochis superpuestos, queremos interactuar con el que vemos arriba.
        for (mochi in mochis.reversed()) {
            if (mochi.fueTocado(xToque, yToque)) {
                // Si tocamos uno, le damos un fuerte impulso hacia arriba (negativo en Y)
                mochi.velocidadY = -40f
                // Y un pequeño empujón lateral aleatorio
                mochi.velocidadX = (Random.nextFloat() * 10f) - 5f
                
                tocoAlgunoExistente = true
                break // Cortamos el bucle: solo queremos hacer saltar uno a la vez
            }
        }

        // Si el toque fue en una zona vacía, creamos un nuevo Mochi
        if (!tocoAlgunoExistente) {
            val nuevoMochi = Mochi(
                x = xToque,
                y = yToque,
                velocidadY = 0f,
                // Nace con una pequeñísima velocidad lateral para que no caiga recto
                velocidadX = (Random.nextFloat() * 2f) - 1f, 
                emoji = emojisDisponibles.random()
            )
            mochis.add(nuevoMochi)
            
            // Control de población para no saturar el móvil
            if (mochis.size > MAX_MOCHIS) {
                mochis.removeFirstOrNull()
            }
        }
    }

    /**
     * El corazón del motor físico. Se debe llamar constantemente (ej. cada fotograma) 
     * para actualizar la posición de todos los elementos.
     *
     * @param anchoPantalla La anchura total del espacio de dibujo, usada para los rebotes laterales.
     * @param altoPantalla La altura total del espacio de dibujo, usada para calcular dónde está el suelo.
     */
    fun actualizarFisicas(anchoPantalla: Float, altoPantalla: Float) {
        // Prevención de errores: si la pantalla no tiene dimensiones, no hacemos nada
        if (anchoPantalla == 0f || altoPantalla == 0f) return

        for (mochi in mochis) {
            // 1. Aplicar Gravedad: Cada fotograma aumenta la velocidad hacia abajo
            mochi.velocidadY += gravedad
            
            // 2. Mover: Actualizar la posición sumándole las velocidades actuales
            mochi.y += mochi.velocidadY
            mochi.x += mochi.velocidadX

            // 3. Colisión con el SUELO
            val limiteSuelo = altoPantalla - mochi.radio

            if (mochi.y > limiteSuelo) {
                // Corrección: Forzamos a que no traspase el suelo
                mochi.y = limiteSuelo

                // Si se mueve muy despacio hacia arriba/abajo, lo detenemos del todo
                // para evitar que se quede "vibrando" eternamente (microrrebotes).
                if (abs(mochi.velocidadY) < 1.5f) {
                    mochi.velocidadY = 0f
                } else {
                    // Rebote: Invertimos la dirección (se vuelve negativa) y le aplicamos 
                    // pérdida de energía (elasticidad)
                    mochi.velocidadY = -mochi.velocidadY * elasticidad
                }

                // Al tocar el suelo, la fricción frena su movimiento horizontal
                mochi.velocidadX *= friccionSuelo
            }

            // 4. Colisiones con las PAREDES (izquierda y derecha)
            val limiteDerecha = anchoPantalla - mochi.radio

            if (mochi.x < mochi.radio) {
                // Choca pared izquierda
                mochi.x = mochi.radio
                mochi.velocidadX = -mochi.velocidadX * elasticidad // Rebote lateral
            } 
            else if (mochi.x > limiteDerecha) {
                // Choca pared derecha
                mochi.x = limiteDerecha
                mochi.velocidadX = -mochi.velocidadX * elasticidad // Rebote lateral
            }
        }
    }

    /**
     * Vacía la lista, eliminando todos los mochis de la pantalla de golpe.
     */
    fun limpiarPantalla() {
        mochis.clear()
    }
}