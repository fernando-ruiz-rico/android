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

data class MovimientoPosible(
    val filaOrigen: Int,
    val columnaOrigen: Int,
    val filaDestino: Int,
    val columnaDestino: Int,
    val esCaptura: Boolean
)

class JuegoDamas {
    var tablero = MutableList(8) {
        MutableList(8) {
            TipoPieza.VACIO
        }
    }

    var turnoActual = Jugador.BLANCO
    var juegoTerminado = false
    var mensaje = "Turno de las Blancas ⚪"

    var filaSeleccionada = -1
    var columnaSeleccionada = -1

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

    fun turno(filaClic:Int, columnaClic:Int) {
        if (juegoTerminado || turnoActual == Jugador.NEGRO) return

        val piezaClic = tablero[filaClic][columnaClic]

        if (filaSeleccionada == -1 && columnaSeleccionada == -1) {
            if (piezaClic != TipoPieza.VACIO && piezaClic.jugador == turnoActual) {
                filaSeleccionada = filaClic
                columnaSeleccionada = columnaClic
                mensaje = "Pieza seleccionada. Elije destino."
            }
            else {
                mensaje = "Selecciona una de tus piezas"
            }
            return
        }

        if (piezaClic == TipoPieza.VACIO) {
            if (intentarMovimiento(filaClic, columnaClic)) {
                if (!juegoTerminado) {
                    cambiarTurno()
                }
            }
        }
    }

    fun cambiarTurno() {
       turnoActual = if (turnoActual == Jugador.BLANCO) Jugador.NEGRO else Jugador.BLANCO
    }

    fun juegoOrdenador() {
        if (juegoTerminado) return

        cambiarTurno()
    }

    fun efectuarMovimiento(filaDestino:Int, columnaDestino:Int) {
        tablero[filaDestino][columnaDestino] = tablero[filaSeleccionada][columnaSeleccionada]
        tablero[filaSeleccionada][columnaSeleccionada] = TipoPieza.VACIO
    }

    fun intentarMovimiento(filaDestino:Int, columnaDestino:Int): Boolean {
        efectuarMovimiento(filaDestino, columnaDestino)

        return true
    }
}