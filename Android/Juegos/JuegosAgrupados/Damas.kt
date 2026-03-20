/**
 * ==============================================================================
 * MOTOR DE JUEGO: DAMAS (Esqueleto / Pendiente de implementación)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo actúa como reserva o plantilla (stub) para la futura 
 * implementación de un motor de lógica para el juego de las Damas.
 *
 * Qué aprenderás de Kotlin y programación con este código:
 * 1. Estructuración modular: Dejar clases preparadas en archivos separados 
 * para construir funcionalidades de forma progresiva sin romper la app.
 * ==============================================================================
 */
package com.example.myapplication

import kotlin.math.abs

/**
 * Clase principal para las Damas.
 * (La lógica de movimientos en diagonal, promoción a reina y captura de piezas
 * se implementará aquí en el futuro).
 */
class JuegoDamas {
    data class MovimientoPosible(
        val filaOrigen: Int,
        val columnaOrigen: Int,
        val filaDestino: Int,
        val columnaDestino: Int,
        val esCaptura: Boolean
    )

    enum class Jugador(val texto: String) {
        BLANCO("Blancas ⚪"),
        NEGRO("Ordenador ⚫")
    }

    enum class TipoPieza(val simbolo: String, val jugador: Jugador?) {
        VACIO("", null),
        PEON_BLANCO("⚪", Jugador.BLANCO),
        PEON_NEGRO("⚫", Jugador.NEGRO),
        DAMA_BLANCA("♕", Jugador.BLANCO),
        DAMA_NEGRA("♛", Jugador.NEGRO)
    }

    companion object {
        const val DIMENSION = 8
    }

    var tablero = MutableList(DIMENSION) {
        MutableList(DIMENSION) {
            TipoPieza.VACIO
        }
    }
    var turnoActual = Jugador.BLANCO
    var juegoTerminado = false
    var mensaje = "Turno de las Blancas ⚪"

    var filaSeleccionada = -1
    var columnaSeleccionada = -1

    // Variables para controlar los saltos múltiples (comer en cadena)
    var saltoEnCadenaFila = -1
    var saltoEnCadenaColumna = -1

    init {
        iniciarPartida()
    }

    fun iniciarPartida() {
        tablero = MutableList(DIMENSION) { MutableList(DIMENSION) { TipoPieza.VACIO } }
        for (fila in 0 until DIMENSION) {
            for (columna in 0 until DIMENSION) {
                if ((fila + columna) % 2 != 0) {
                    if (fila in 0..2) tablero[fila][columna] = TipoPieza.PEON_NEGRO
                    else if (fila in 5..7) tablero[fila][columna] = TipoPieza.PEON_BLANCO
                }
            }
        }
        turnoActual = Jugador.BLANCO
        //deseleccionar()
        saltoEnCadenaFila = -1
        saltoEnCadenaColumna = -1
        juegoTerminado = false
        mensaje = "Turno de las ${turnoActual.texto}"
    }

    private fun obtenerMovimientosValidos(jugador: Jugador): List<MovimientoPosible> {
        val movimientos = mutableListOf<MovimientoPosible>()
        val direcciones = listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))

        // Si estamos en medio de un salto múltiple, SOLO la pieza que está saltando puede moverse
        val forzarPieza = (saltoEnCadenaFila != -1 && saltoEnCadenaColumna != -1)

        for (fila in 0 until DIMENSION) {
            for (columna in 0 until DIMENSION) {
                if (forzarPieza && (fila != saltoEnCadenaFila || columna != saltoEnCadenaColumna)) continue

                val pieza = tablero[fila][columna]
                if (pieza.jugador == jugador) {
                    val esDama = (pieza == TipoPieza.DAMA_BLANCA || pieza == TipoPieza.DAMA_NEGRA)

                    for (direccion in direcciones) {
                        val dirFila = direccion.first
                        val dirColumna = direccion.second

                        val direccionCorrecta = esDama ||
                                (pieza == TipoPieza.PEON_BLANCO && dirFila < 0) ||
                                (pieza == TipoPieza.PEON_NEGRO && dirFila > 0)

                        if (direccionCorrecta) {
                            if (!esDama) {
                                // LÓGICA PARA PEONES (Paso corto)
                                // 1. Comprobar movimiento normal (solo si no estamos obligados a seguir saltando)
                                if (!forzarPieza) {
                                    val filaDest1 = fila + dirFila
                                    val colDest1 = columna + dirColumna
                                    if (filaDest1 in 0 until DIMENSION && colDest1 in 0 until DIMENSION && tablero[filaDest1][colDest1] == TipoPieza.VACIO) {
                                        movimientos.add(MovimientoPosible(fila, columna, filaDest1, colDest1, false))
                                    }
                                }

                                // 2. Comprobar movimiento de captura
                                val filaDest2 = fila + (dirFila * 2)
                                val colDest2 = columna + (dirColumna * 2)
                                if (filaDest2 in 0 until DIMENSION && colDest2 in 0 until DIMENSION && tablero[filaDest2][colDest2] == TipoPieza.VACIO) {
                                    val filaMedia = fila + dirFila
                                    val colMedia = columna + dirColumna
                                    val piezaIntermedia = tablero[filaMedia][colMedia]

                                    if (piezaIntermedia != TipoPieza.VACIO && piezaIntermedia.jugador != jugador) {
                                        movimientos.add(MovimientoPosible(fila, columna, filaDest2, colDest2, true))
                                    }
                                }
                            } else {
                                // LÓGICA PARA DAMAS VOLADORAS (Paso largo)
                                var distancia = 1
                                var encontradaEnemiga = false

                                while (true) {
                                    val fDest = fila + (dirFila * distancia)
                                    val cDest = columna + (dirColumna * distancia)

                                    // Si nos salimos del tablero, paramos en esta dirección
                                    if (fDest !in 0 until DIMENSION || cDest !in 0 until DIMENSION) break

                                    val piezaDestino = tablero[fDest][cDest]

                                    if (!encontradaEnemiga) {
                                        if (piezaDestino == TipoPieza.VACIO) {
                                            // Casilla vacía normal
                                            if (!forzarPieza) {
                                                movimientos.add(MovimientoPosible(fila, columna, fDest, cDest, false))
                                            }
                                        } else if (piezaDestino.jugador != jugador) {
                                            // Encontramos una enemiga para saltar
                                            encontradaEnemiga = true
                                        } else {
                                            // Chocamos con una pieza propia, la diagonal se acaba aquí
                                            break
                                        }
                                    } else {
                                        if (piezaDestino == TipoPieza.VACIO) {
                                            // Después de saltar a la enemiga, podemos aterrizar en CUALQUIER casilla vacía detrás
                                            movimientos.add(MovimientoPosible(fila, columna, fDest, cDest, true))
                                        } else {
                                            // Si hay OTRA pieza detrás (no importa el color), ya no podemos seguir volando
                                            // porque no se pueden saltar dos piezas seguidas sin aterrizar.
                                            break
                                        }
                                    }
                                    distancia++
                                }
                            }
                        }
                    }
                }
            }
        }
        val capturas = movimientos.filter { it.esCaptura }
        return if (capturas.isNotEmpty()) capturas else movimientos
    }

    fun turno(filaClic: Int, columnaClic: Int) {
        if (juegoTerminado || turnoActual == Jugador.NEGRO) return

        val movimientosValidos = obtenerMovimientosValidos(turnoActual)

        // Regla: Si no tienes movimientos posibles, pierdes.
        if (movimientosValidos.isEmpty()) {
            juegoTerminado = true
            mensaje = "¡GANA EL ORDENADOR! 🏆 (No tienes movimientos)"
            return
        }

        val piezaClic = tablero[filaClic][columnaClic]

        // Caso 1: No hay ninguna pieza seleccionada aún
        if (filaSeleccionada == -1 && columnaSeleccionada == -1) {
            val puedeMoverse = movimientosValidos.any { it.filaOrigen == filaClic && it.columnaOrigen == columnaClic }
            if (puedeMoverse) {
                filaSeleccionada = filaClic
                columnaSeleccionada = columnaClic
                mensaje = "Pieza seleccionada. Elige destino."
            } else {
                if (movimientosValidos.any { it.esCaptura }) {
                    mensaje = "¡Comer es obligatorio! Selecciona la pieza correcta."
                } else if (piezaClic.jugador == turnoActual) {
                    mensaje = "Esa pieza no puede moverse."
                } else {
                    mensaje = "Selecciona una de tus piezas."
                }
            }
            return
        }

        // Caso 2: Clic en la misma pieza para deseleccionar
        if (filaClic == filaSeleccionada && columnaClic == columnaSeleccionada) {
            if (saltoEnCadenaFila == -1) {
                deseleccionar()
                mensaje = "Turno de las ${turnoActual.texto}"
            } else {
                mensaje = "¡Debes terminar tu salto múltiple!"
            }
            return
        }

        // Caso 3: Clic en otra de mis piezas para cambiar la selección
        if (piezaClic != TipoPieza.VACIO && piezaClic.jugador == turnoActual) {
            if (saltoEnCadenaFila == -1) {
                val puedeMoverse = movimientosValidos.any { it.filaOrigen == filaClic && it.columnaOrigen == columnaClic }
                if (puedeMoverse) {
                    filaSeleccionada = filaClic
                    columnaSeleccionada = columnaClic
                    mensaje = "Pieza cambiada. Elige destino."
                } else {
                    if (movimientosValidos.any { it.esCaptura }) {
                        mensaje = "¡Comer es obligatorio!"
                    } else {
                        mensaje = "Esa pieza no puede moverse."
                    }
                }
            } else {
                mensaje = "¡Debes terminar tu salto con la misma pieza!"
            }
            return
        }

        // Caso 4: Clic en una casilla vacía para moverse
        if (piezaClic == TipoPieza.VACIO) {
            val mov = movimientosValidos.find {
                it.filaOrigen == filaSeleccionada && it.columnaOrigen == columnaSeleccionada &&
                        it.filaDestino == filaClic && it.columnaDestino == columnaClic
            }

            if (mov != null) {
                ejecutarMovimiento(mov)
            } else {
                mensaje = "Movimiento inválido."
            }
        }
    }

    private fun ejecutarMovimiento(mov: MovimientoPosible) {
        // Mover la pieza
        tablero[mov.filaDestino][mov.columnaDestino] = tablero[mov.filaOrigen][mov.columnaOrigen]
        tablero[mov.filaOrigen][mov.columnaOrigen] = TipoPieza.VACIO

        // Si es captura, buscamos la pieza enemiga en el camino y la borramos
        if (mov.esCaptura) {
            val dirF = if (mov.filaDestino > mov.filaOrigen) 1 else -1
            val dirC = if (mov.columnaDestino > mov.columnaOrigen) 1 else -1
            var fRuta = mov.filaOrigen + dirF
            var cRuta = mov.columnaOrigen + dirC

            while (fRuta != mov.filaDestino && cRuta != mov.columnaDestino) {
                if (tablero[fRuta][cRuta] != TipoPieza.VACIO) {
                    tablero[fRuta][cRuta] = TipoPieza.VACIO
                    break // Borramos la pieza enemiga que nos hemos saltado
                }
                fRuta += dirF
                cRuta += dirC
            }
        }

        val corono = coronarSiProcede(mov.filaDestino, mov.columnaDestino)

        // Comprobar saltos múltiples
        var puedeSeguirSaltando = false
        if (mov.esCaptura && !corono) {
            saltoEnCadenaFila = mov.filaDestino
            saltoEnCadenaColumna = mov.columnaDestino

            // Ver si ESA pieza que acaba de saltar tiene más capturas
            val siguientesMovs = obtenerMovimientosValidos(turnoActual)
            if (siguientesMovs.isNotEmpty() && siguientesMovs.first().esCaptura) {
                puedeSeguirSaltando = true
            }
        }

        if (puedeSeguirSaltando) {
            // Mantener la selección para obligar a dar el siguiente salto
            filaSeleccionada = mov.filaDestino
            columnaSeleccionada = mov.columnaDestino
            mensaje = "¡Salto múltiple! Sigue comiendo."
        } else {
            // Terminar turno
            saltoEnCadenaFila = -1
            saltoEnCadenaColumna = -1
            deseleccionar()
            comprobarFinDeJuego()
            if (!juegoTerminado) cambiarTurno()
        }
    }

    private fun cambiarTurno() {
        turnoActual = if (turnoActual == Jugador.BLANCO) Jugador.NEGRO else Jugador.BLANCO
        mensaje = if (turnoActual == Jugador.BLANCO) "Turno de las Blancas ⚪" else "El ordenador está pensando..."
    }

    private fun comprobarFinDeJuego() {
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
            mensaje = "¡GANA EL ORDENADOR! 🏆"
        } else if (!negrasVivas) {
            juegoTerminado = true
            mensaje = "¡ENHORABUENA! ¡HAS GANADO! 🏆"
        }
    }

    fun jugarOrdenador() {
        if (juegoTerminado || turnoActual != Jugador.NEGRO) return

        val movimientos = obtenerMovimientosValidos(Jugador.NEGRO)

        if (movimientos.isEmpty()) {
            juegoTerminado = true
            mensaje = "¡ENHORABUENA! ¡HAS GANADO! 🏆 (El ordenador no puede moverse)"
            return
        }

        // Elegir un movimiento al azar (como 'obtenerMovimientosValidos' ya obliga a comer,
        // si hay capturas, solo habrá capturas en la lista).
        val movimientoElegido = movimientos.random()

        filaSeleccionada = movimientoElegido.filaOrigen
        columnaSeleccionada = movimientoElegido.columnaOrigen

        ejecutarMovimiento(movimientoElegido)

        // Si el ordenador hace un salto múltiple, sigue siendo su turno.
        // Se llama de forma recursiva para hacer todos los saltos de forma instantánea.
        if (!juegoTerminado && turnoActual == Jugador.NEGRO && saltoEnCadenaFila != -1) {
            jugarOrdenador()
        }
    }

    private fun coronarSiProcede(fila: Int, columna: Int): Boolean {
        val pieza = tablero[fila][columna]
        if (pieza == TipoPieza.PEON_BLANCO && fila == 0) {
            tablero[fila][columna] = TipoPieza.DAMA_BLANCA
            return true
        } else if (pieza == TipoPieza.PEON_NEGRO && fila == DIMENSION - 1) {
            tablero[fila][columna] = TipoPieza.DAMA_NEGRA
            return true
        }
        return false
    }

    private fun deseleccionar() {
        filaSeleccionada = -1
        columnaSeleccionada = -1
    }
}