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
    var emoji: String
) {
    fun fueTocado(xDelDedo:Float, yDelDedo:Float): Boolean {
        val distanciaX = xDelDedo - x
        val distanciaY = yDelDedo - y

        val distanciaAlCuadrado = (distanciaX * distanciaX) + (distanciaY * distanciaY)
        val radioAlCuadrado = radio * radio

        return distanciaAlCuadrado <= (radioAlCuadrado * 1.5f)
    }
}

class MotorMochis {
    companion object {
        const val MAX_MOCHIS = 1000
    }

    var mochis = mutableStateListOf<Mochi>()

    val emojisDisponibles = listOf("🍡", "🍮", "🥟", "🍓", "🥞", "🥝", "🫒", "🥑", "🥕", "🥒", "🍥", "🥓", "🌮")

    fun tocar(xToque:Float, yToque:Float) {
        val nuevoMochi = Mochi(
            x = xToque,
            y = yToque,
            velocidadY = 0f,
            velocidadX = 0f,
            emoji = emojisDisponibles.random()
        )
        mochis.add(nuevoMochi)
        if (mochis.size > MAX_MOCHIS) {
            mochis.removeFirstOrNull()
        }
    }
}