package com.example.myapplication

import kotlin.math.abs

enum class Jugador(val texto: String) {
    BLANCO("Blancas ⚪"),
    NEGRO("Ordenador ⚫")
}

enum class TipoPieza(val simbolo: String, val jugador:Jugador?) {
    VACIO("", null),
    PEON_BLANCO("⚪", Jugador.BLANCO),
    PEON_NEGRO("⚫", Jugador.NEGRO),
    DAMA_BLANCA("♕", Jugador.BLANCO),
    DAMA_NEGRA("♛", Jugador.NEGRO)
}

class JuegoDamas {
    var tablero = MutableList(8) {
        MutableList(8) {
            TipoPieza.VACIO
        }
    }

    init {
        iniciarPartida()
    }

    fun iniciarPartida() {
        for (fila in 0 until 8) {
            for (columna in 0 until 8) {
                if ((fila + columna) % 2 != 0) {
                    if (fila in 0..2) {
                        tablero[fila][columna] = TipoPieza.PEON_NEGRO
                    }
                    else if (fila in 5..7) {
                        tablero[fila][columna] = TipoPieza.PEON_BLANCO
                    }
                }
            }
        }
    }
}