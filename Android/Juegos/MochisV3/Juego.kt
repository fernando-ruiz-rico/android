package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf
import kotlin.math.abs
import kotlin.random.Random

data class Mochi(
    var x: Float,
    var y: Float,
    var velocidadY: Float = 0f,
    var velocidadX: Float = 0f,
    val radio: Float = 70f,
    var emoji: String,
    val tiempoCreacion: Long = System.currentTimeMillis(),
    var explotado: Boolean = false
) {
    fun fueTocado(xDelDedo: Float, yDelDedo: Float): Boolean {
        val distanciaX = xDelDedo - x
        val distanciaY = yDelDedo - y

        val distanciaAlCuadrado = (distanciaX * distanciaX) + (distanciaY * distanciaY)
        val radioAlCuadrado = radio * radio

        return distanciaAlCuadrado <= (radioAlCuadrado * 1.5f)
    }
}

class MotorMochis {
    companion object {
        const val MAX_MOCHIS = 100
    }

    private val gravedad = 0.05f
    private val elasticidad = 0.75f
    private val friccionSuelo = 0.9f

    var mochis = mutableStateListOf<Mochi>()

    val emojisDisponibles = listOf("🎈", "🫧", "🌸", "🦋")

    fun tocar(xToque:Float, yToque:Float) {
        for (mochi in mochis.reversed()) {
            if (mochi.fueTocado(xToque, yToque)) {
                mochi.explotado = true
                break
            }
        }
    }

    fun crearNuevoEmoji(anchoPantalla: Float, altoPantalla: Float) {
        val nuevoMochi = Mochi(
            x = Random.nextFloat() * anchoPantalla,
            y = altoPantalla + 150f,
            velocidadY = 0f,
            velocidadX = (Random.nextFloat() * 2f) - 1f,
            emoji = emojisDisponibles.random()
        )
        mochis.add(nuevoMochi)
        if (mochis.size > MAX_MOCHIS) {
            mochis.removeFirstOrNull()
        }
    }

    fun actualizarFisicas(anchoPantalla:Float, altoPantalla:Float) {
        if (anchoPantalla == 0f || altoPantalla == 0f) return

        for (mochi in mochis) {
            mochi.velocidadY += gravedad
            mochi.y -= mochi.velocidadY
        }

        if (Random.nextFloat() < 0.025) {
            crearNuevoEmoji(anchoPantalla, altoPantalla)
        }
    }

    fun limpiarPantalla() {
        mochis.clear()
    }
}