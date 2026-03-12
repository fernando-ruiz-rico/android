package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf

data class Mochi(
    var x: Float,
    var y: Float,
    var velocidadY: Float = 0f,
    var velocidadX: Float = 0f,
    val radio: Float = 70f,
    var emoji: String,
    val tiempoCreacion: Long = System.currentTimeMillis()
)

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