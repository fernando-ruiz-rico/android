package com.example.myapplication

import kotlin.random.Random

enum class TipoBarco(val longitud: Int) {
    PORTAAVIONES(5),
    ACORAZADO(4),
    CRUCERO(3),
    SUBMARINO(3),
    DESTRUCTOR(2)
}

enum class EstadoCasilla(val simbolo:String) {
    AGUA("🟦"),
    BARCO("🚢"),
    TOCADO("💥"),
    FALLO("⚪");

    override fun toString(): String = simbolo
}

class Juego {
    val DIMENSION = 10
    val MUNICION_MAXIMA = 50

    var oceano = MutableList(DIMENSION) {
        MutableList(DIMENSION) {
            EstadoCasilla.AGUA
        }
    }
    val impactosNecesarios = TipoBarco.values().sumOf({ it.longitud })

    var aciertos = 0
    var misilesRestantes = MUNICION_MAXIMA
    var juegoTerminado = false

    var mensaje = "Pulsa iniciar para jugar"

    fun iniciarPartida() {
        oceano = MutableList(DIMENSION) { MutableList(DIMENSION) { EstadoCasilla.AGUA } }
        colocarFlotaCompleta()
        aciertos = 0
        misilesRestantes = MUNICION_MAXIMA
        juegoTerminado = false
        mensaje = "Toca una casilla"
    }

    fun esPosicionValida(fila:Int, columna:Int, longitud:Int, horizontal:Boolean): Boolean {
        val anchoBarco = if (horizontal) longitud else 1
        val altoBarco = if (horizontal) 1 else longitud

        if (fila + altoBarco > DIMENSION || columna + anchoBarco > DIMENSION) {
            return false
        }

        val filaInicio = (fila - 1).coerceAtLeast(0)
        val columnaInicio = (columna - 1).coerceAtLeast(0)
        val filaFin = (fila + altoBarco).coerceAtMost(DIMENSION - 1)
        val columnaFin = (columna + anchoBarco).coerceAtMost(DIMENSION - 1)

        for (i in filaInicio..filaFin) {
            for (j in columnaInicio..columnaFin) {
                if (oceano[i][j] != EstadoCasilla.AGUA) {
                    return false
                }
            }
        }

        return true
    }

    fun colocarBarcoEnMatriz(fila:Int, columna:Int, longitud:Int, horizontal:Boolean) {
        for (i in 0 until longitud) {
            if (horizontal) {
                oceano[fila][columna + i] = EstadoCasilla.BARCO
            }
            else {
                oceano[fila + i][columna] = EstadoCasilla.BARCO
            }
        }
    }

    fun colocarBarcoAleatorio(barco: TipoBarco) {
        var colocado = false
        while(!colocado) {
            val fila = Random.nextInt(DIMENSION)
            val columna = Random.nextInt(DIMENSION)
            val horizontal = Random.nextBoolean()

            if (esPosicionValida(fila, columna, barco.longitud, horizontal)) {
                colocarBarcoEnMatriz(fila, columna, barco.longitud, horizontal)
                colocado = true
            }
        }
    }

    fun colocarFlotaCompleta() {
        for (barco in TipoBarco.values()) {
            colocarBarcoAleatorio(barco)
        }
    }
}
