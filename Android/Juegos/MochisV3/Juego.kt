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
    val tiempoCreacion: Long = System.currentTimeMillis()
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

    private val gravedad = 0.5f
    private val elasticidad = 0.75f
    private val friccionSuelo = 0.9f

    var mochis = mutableStateListOf<Mochi>()

    val emojisDisponibles = listOf("🍡", "🍮", "🥟", "🍓", "🥞", "🥝", "🫒", "🥑", "🥕", "🥒", "🍥", "🥓", "🌮")

    fun tocar(xToque:Float, yToque:Float) {
        var tocoAlgunoExistente = false

        for (mochi in mochis.reversed()) {
            if (mochi.fueTocado(xToque, yToque)) {
                mochi.velocidadY = -40f
                mochi.velocidadX = (Random.nextFloat() * 10f) - 5f
                tocoAlgunoExistente = true
                break
            }
        }

        if (!tocoAlgunoExistente) {
            val nuevoMochi = Mochi(
                x = xToque,
                y = yToque,
                velocidadY = 0f,
                velocidadX = (Random.nextFloat() * 2f) - 1f,
                emoji = emojisDisponibles.random()
            )
            mochis.add(nuevoMochi)
            if (mochis.size > MAX_MOCHIS) {
                mochis.removeFirstOrNull()
            }
        }
    }

    fun actualizarFisicas(anchoPantalla:Float, altoPantalla:Float) {
        if (anchoPantalla == 0f || altoPantalla == 0f) return

        for (mochi in mochis) {
            mochi.velocidadY += gravedad
            mochi.y += mochi.velocidadY
            mochi.x += mochi.velocidadX

            val limiteSuelo = altoPantalla - mochi.radio

            if (mochi.y > limiteSuelo) {
                mochi.y = limiteSuelo

                if (abs(mochi.velocidadY) < 1.5f) {
                    mochi.velocidadY = 0f
                }
                else {
                    mochi.velocidadY = -mochi.velocidadY * elasticidad
                }

                mochi.velocidadX *= friccionSuelo
            }

            val limiteDerecha = anchoPantalla - mochi.radio

            if (mochi.x < mochi.radio) {
                mochi.x = mochi.radio
                mochi.velocidadX = -mochi.velocidadX * elasticidad
            }
            else if (mochi.x > limiteDerecha) {
                mochi.x = limiteDerecha
                mochi.velocidadX = -mochi.velocidadX * elasticidad
            }
        }
    }

    fun limpiarPantalla() {
        mochis.clear()
    }
}