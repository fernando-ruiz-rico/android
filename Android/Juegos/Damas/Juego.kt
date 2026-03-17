package com.example.myapplication

import kotlin.math.abs

enum class Jugador(val texto: String) {
    BLANCO("Blancas ⚪"),
    NEGRO("Negras ⚫")
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
    val DIMENSION = 8

    var tablero = MutableList(8) {
        MutableList(8) {
            TipoPieza.VACIO
        }
    }

    var turnoActual = Jugador.BLANCO
    var juegoTerminado = false
    var mensaje = "Turno de las ${turnoActual.texto}"

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

        turnoActual = Jugador.BLANCO
        deseleccionar()
        juegoTerminado = false
        mensaje = "Turno de las ${turnoActual.texto}"
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

        if (filaClic == filaSeleccionada && columnaClic == columnaSeleccionada) {
            deseleccionar()
            mensaje = "Turno de las ${turnoActual.texto}"
            return
        }

        if (piezaClic != TipoPieza.VACIO && piezaClic.jugador == turnoActual) {
            filaSeleccionada = filaClic
            columnaSeleccionada = columnaClic
            mensaje = "Pieza seleccionada. Elije destino."
            return
        }

        if (piezaClic == TipoPieza.VACIO) {
            if (intentarMovimiento(filaClic, columnaClic)) {
                if (!juegoTerminado) {
                    cambiarTurno()
                }
            }
            else {
                mensaje = "Movimiento incorrecto"
            }
        }
    }

    fun cambiarTurno() {
        if (juegoTerminado) return

        turnoActual = if (turnoActual == Jugador.BLANCO) Jugador.NEGRO else Jugador.BLANCO
        mensaje = "Turno de las ${turnoActual.texto}"
    }

    fun coronarSiProcede(fila:Int, columna:Int) {
        val pieza = tablero[fila][columna]
        if (pieza == TipoPieza.PEON_BLANCO && fila == 0) {
            tablero[fila][columna] = TipoPieza.DAMA_BLANCA
        }
        else if (pieza == TipoPieza.PEON_NEGRO && fila == DIMENSION - 1) {
            tablero[fila][columna] = TipoPieza.DAMA_NEGRA
        }
    }

    fun jugarOrdenador() {
        if (juegoTerminado) return

        val movimientosPosibles = mutableListOf<MovimientoPosible>()

        val direcciones = listOf(Pair(1,1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))

        for (fila in 0 until DIMENSION) {
            for (columna in 0 until DIMENSION) {
                val pieza = tablero[fila][columna]

                if (pieza.jugador == Jugador.NEGRO) {
                    val esDama = (pieza  == TipoPieza.DAMA_NEGRA)

                    for (direccion in direcciones) {
                        val dirFila = direccion.first
                        val dirColumna = direccion.second

                        if (esDama || dirFila > 0) {
                            val filaDest1 = fila + dirFila
                            val colDest1 = columna + dirColumna

                            val dentroDelTablero1 = (filaDest1 in 0 until DIMENSION && colDest1 in 0 until DIMENSION)

                            if (dentroDelTablero1 && tablero[filaDest1][colDest1] == TipoPieza.VACIO) {
                                movimientosPosibles.add(MovimientoPosible(fila, columna, filaDest1, colDest1, esCaptura = false))
                            }

                            val filaDest2 = fila + (dirFila * 2)
                            val colDest2 = columna + (dirColumna * 2)

                            val dentroDelTablero2 = (filaDest2 in 0 until DIMENSION && colDest2 in 0 until DIMENSION)

                            if (dentroDelTablero1 && dentroDelTablero2 && tablero[filaDest2][colDest2] == TipoPieza.VACIO) {
                                val piezaIntermedia = tablero[filaDest1][colDest1]
                                if (piezaIntermedia != TipoPieza.VACIO && piezaIntermedia.jugador == Jugador.BLANCO) {
                                    movimientosPosibles.add(MovimientoPosible(fila, columna, filaDest2, colDest2, esCaptura = true))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (movimientosPosibles.isEmpty()) {
            juegoTerminado = true
            mensaje = "¡Gana el jugador humano!"
            return
        }

        val movimientosDeCaptura = movimientosPosibles.filter({ it.esCaptura })

        val movimientoElegido = if (movimientosDeCaptura.isNotEmpty()) movimientosDeCaptura.random() else movimientosPosibles.random()

        filaSeleccionada = movimientoElegido.filaOrigen
        columnaSeleccionada = movimientoElegido.columnaOrigen

        intentarMovimiento(movimientoElegido.filaDestino, movimientoElegido.columnaDestino)

        cambiarTurno()
    }

    fun efectuarMovimiento(filaDestino:Int, columnaDestino:Int) {
        tablero[filaDestino][columnaDestino] = tablero[filaSeleccionada][columnaSeleccionada]
        tablero[filaSeleccionada][columnaSeleccionada] = TipoPieza.VACIO
        deseleccionar()
        coronarSiProcede(filaDestino, columnaDestino)
        comprobarFinDejuego()
    }

    fun intentarMovimiento(filaDestino:Int, columnaDestino:Int): Boolean {
        val pieza = tablero[filaSeleccionada][columnaSeleccionada]
        val esDama = (pieza == TipoPieza.DAMA_BLANCA || pieza == TipoPieza.DAMA_NEGRA)

        val diferenciaFila = filaDestino - filaSeleccionada
        val diferenciaColumna = columnaDestino - columnaSeleccionada

        val direccioncorrecta = esDama ||
                (pieza == TipoPieza.PEON_BLANCO && diferenciaFila < 0) ||
                (pieza == TipoPieza.PEON_NEGRO && diferenciaFila > 0)

        if (abs(diferenciaFila) == 1 && abs(diferenciaColumna) == 1 && direccioncorrecta) {
            efectuarMovimiento(filaDestino, columnaDestino)
            return true
        }

        if (abs(diferenciaFila) == 2 && abs(diferenciaColumna) == 2 && direccioncorrecta) {
            val filaMedia = filaSeleccionada + (diferenciaFila / 2)
            val columnaMedia = columnaSeleccionada + (diferenciaColumna / 2)
            val piezaComida = tablero[filaMedia][columnaMedia]

            if (piezaComida != TipoPieza.VACIO && piezaComida.jugador != turnoActual) {
                tablero[filaMedia][columnaMedia] = TipoPieza.VACIO
                efectuarMovimiento(filaDestino, columnaDestino)
                return true
            }
        }

        return false
    }

    fun comprobarFinDejuego() {
        var blancasVivas = false
        var negrasVivas = false

        for (fila in 0 until DIMENSION) {
            for (columna in 0 until DIMENSION) {
                if (tablero[fila][columna].jugador == Jugador.BLANCO) blancasVivas = true
                if (tablero[fila][columna].jugador == Jugador.NEGRO) negrasVivas = true
            }
        }

        if (!blancasVivas) {
            juegoTerminado = true
            mensaje = "¡Has perdido! :-)"
        }
        else if (!negrasVivas) {
            juegoTerminado = true
            mensaje = "¡Has ganado! :-("
        }
    }

    fun deseleccionar() {
        filaSeleccionada = -1
        columnaSeleccionada = -1
    }
}