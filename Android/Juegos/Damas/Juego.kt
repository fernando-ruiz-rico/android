/**
 * ==============================================================================
 * MOTOR DEL JUEGO: DAMAS
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo contiene la lógica principal del juego de mesa "Damas".
 * Gestiona la matriz del tablero, las reglas de movimiento (diagonales, capturas),
 * la coronación de piezas y contiene un bot (inteligencia artificial) básico que 
 * prioriza los movimientos de captura frente a los movimientos normales.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Data classes: Uso para estructurar información compleja (posibles movimientos).
 * 2. Colecciones y filtrado: Uso de `filter`, `random` y `isNotEmpty` en listas.
 * 3. Matemáticas simples: Uso de la función valor absoluto (`abs()`) para 
 * validar fácilmente los desplazamientos diagonales.
 * ==============================================================================
 */

package com.example.myapplication

import kotlin.math.abs

/**
 * Define los jugadores que participan en la partida.
 * @param texto Representación en texto legible para la interfaz gráfica.
 */
enum class Jugador(val texto: String) {
    BLANCO("Blancas ⚪"),
    NEGRO("Negras ⚫")
}

/**
 * Representa los diferentes tipos de piezas que pueden existir en el tablero.
 * @param simbolo El emoji que representa visualmente la pieza.
 * @param jugador El propietario de la pieza (nulo si la casilla está vacía).
 */
enum class TipoPieza(val simbolo: String, val jugador:Jugador?) {
    VACIO("", null),
    PEON_BLANCO("⚪", Jugador.BLANCO),
    PEON_NEGRO("⚫", Jugador.NEGRO),
    DAMA_BLANCA("♕", Jugador.BLANCO),
    DAMA_NEGRA("♛", Jugador.NEGRO)
}

/**
 * Estructura de datos que almacena toda la información de un movimiento potencial de la IA.
 */
data class MovimientoPosible(
    val filaOrigen: Int,
    val columnaOrigen: Int,
    val filaDestino: Int,
    val columnaDestino: Int,
    val esCaptura: Boolean // Bandera para dar prioridad si el movimiento come una pieza rival
)

/**
 * Clase principal que gestiona el estado y las reglas de la partida de Damas.
 */
class JuegoDamas {
    // Dimensión estándar del tablero de damas
    val DIMENSION = 8

    // Matriz bidimensional que representa el tablero de juego, rellenada con VACIO inicialmente
    var tablero = MutableList(8) {
        MutableList(8) {
            TipoPieza.VACIO
        }
    }

    // Variables de estado de la partida actual
    var turnoActual = Jugador.BLANCO
    var juegoTerminado = false
    var mensaje = "Turno de las ${turnoActual.texto}"

    // Coordenadas de la pieza que el jugador humano tiene seleccionada actualmente (-1 indica que no hay ninguna)
    var filaSeleccionada = -1
    var columnaSeleccionada = -1

    init {
        // Preparamos el tablero automáticamente al crear la instancia
        iniciarPartida()
    }

    /**
     * Coloca las piezas en sus posiciones iniciales y reinicia las variables.
     */
    fun iniciarPartida() {
        for (fila in 0 until 8) {
            for (columna in 0 until 8) {
                // Las piezas solo se colocan en las casillas oscuras
                if ((fila + columna) % 2 != 0) {
                    // Filas 0 a 2 para las negras (arriba)
                    if (fila in 0..2) {
                        tablero[fila][columna] = TipoPieza.PEON_NEGRO
                    }
                    // Filas 5 a 7 para las blancas (abajo)
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

    /**
     * Procesa la interacción del usuario cuando toca una casilla del tablero.
     *
     * @param filaClic Coordenada vertical (Y) tocada.
     * @param columnaClic Coordenada horizontal (X) tocada.
     */
    fun turno(filaClic:Int, columnaClic:Int) {
        // Bloqueo: No hacer nada si el juego terminó o si es el turno de la IA
        if (juegoTerminado || turnoActual == Jugador.NEGRO) return

        val piezaClic = tablero[filaClic][columnaClic]

        // Caso 1: El jugador no tiene ninguna pieza seleccionada todavía
        if (filaSeleccionada == -1 && columnaSeleccionada == -1) {
            // Comprobamos si tocó una pieza de su color
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

        // Caso 2: El jugador toca la misma pieza ya seleccionada (la deselecciona)
        if (filaClic == filaSeleccionada && columnaClic == columnaSeleccionada) {
            deseleccionar()
            mensaje = "Turno de las ${turnoActual.texto}"
            return
        }

        // Caso 3: El jugador toca OTRA pieza de su propio equipo (cambia la selección)
        if (piezaClic != TipoPieza.VACIO && piezaClic.jugador == turnoActual) {
            filaSeleccionada = filaClic
            columnaSeleccionada = columnaClic
            mensaje = "Pieza seleccionada. Elije destino."
            return
        }

        // Caso 4: El jugador tiene una pieza seleccionada y toca una casilla vacía (intento de mover)
        if (piezaClic == TipoPieza.VACIO) {
            // Evaluamos si las reglas permiten este movimiento
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

    /**
     * Alterna el jugador activo y actualiza los mensajes.
     */
    fun cambiarTurno() {
        if (juegoTerminado) return

        turnoActual = if (turnoActual == Jugador.BLANCO) Jugador.NEGRO else Jugador.BLANCO
        mensaje = "Turno de las ${turnoActual.texto}"
    }

    /**
     * Convierte un peón en dama (Reina) si alcanza el extremo opuesto del tablero.
     */
    fun coronarSiProcede(fila:Int, columna:Int) {
        val pieza = tablero[fila][columna]
        // Peón blanco llega a la fila superior (0)
        if (pieza == TipoPieza.PEON_BLANCO && fila == 0) {
            tablero[fila][columna] = TipoPieza.DAMA_BLANCA
        }
        // Peón negro llega a la fila inferior (7)
        else if (pieza == TipoPieza.PEON_NEGRO && fila == DIMENSION - 1) {
            tablero[fila][columna] = TipoPieza.DAMA_NEGRA
        }
    }

    /**
     * Lógica de Inteligencia Artificial sencilla para el turno del ordenador (Fichas Negras).
     */
    fun jugarOrdenador() {
        if (juegoTerminado) return

        // Lista para recopilar todas las jugadas legales en este turno
        val movimientosPosibles = mutableListOf<MovimientoPosible>()

        // Representan movimientos diagonales: Abajo-Derecha, Abajo-Izquierda, Arriba-Derecha, Arriba-Izquierda
        val direcciones = listOf(Pair(1,1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))

        // Escaneamos el tablero para encontrar todas las piezas de la IA
        for (fila in 0 until DIMENSION) {
            for (columna in 0 until DIMENSION) {
                val pieza = tablero[fila][columna]

                if (pieza.jugador == Jugador.NEGRO) {
                    val esDama = (pieza  == TipoPieza.DAMA_NEGRA)

                    // Para cada pieza negra, verificamos las 4 direcciones diagonales
                    for (direccion in direcciones) {
                        val dirFila = direccion.first
                        val dirColumna = direccion.second

                        // Un peón normal negro solo puede bajar (dirFila > 0), las damas pueden ir en cualquier dirección
                        if (esDama || dirFila > 0) {
                            // 1. Probamos un movimiento simple de 1 casilla
                            val filaDest1 = fila + dirFila
                            val colDest1 = columna + dirColumna

                            val dentroDelTablero1 = (filaDest1 in 0 until DIMENSION && colDest1 in 0 until DIMENSION)

                            // Si está libre, es un movimiento válido (no captura)
                            if (dentroDelTablero1 && tablero[filaDest1][colDest1] == TipoPieza.VACIO) {
                                movimientosPosibles.add(MovimientoPosible(fila, columna, filaDest1, colDest1, esCaptura = false))
                            }

                            // 2. Probamos un movimiento de captura saltando 2 casillas
                            val filaDest2 = fila + (dirFila * 2)
                            val colDest2 = columna + (dirColumna * 2)

                            val dentroDelTablero2 = (filaDest2 in 0 until DIMENSION && colDest2 in 0 until DIMENSION)

                            // Verificamos si podemos saltar sobre una pieza blanca y caer en vacío
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

        // Condición de derrota para la IA: se ha quedado sin movimientos legales (bloqueada o sin fichas)
        if (movimientosPosibles.isEmpty()) {
            juegoTerminado = true
            mensaje = "¡Gana el jugador humano!"
            return
        }

        // Filtramos para ver si existe algún movimiento que coma pieza (Tienen prioridad por las reglas de las Damas)
        val movimientosDeCaptura = movimientosPosibles.filter({ it.esCaptura })

        // Si existen capturas, elige una al azar; si no, elige un movimiento normal al azar
        val movimientoElegido = if (movimientosDeCaptura.isNotEmpty()) movimientosDeCaptura.random() else movimientosPosibles.random()

        // Seleccionamos "virtualmente" la pieza y procesamos su movimiento
        filaSeleccionada = movimientoElegido.filaOrigen
        columnaSeleccionada = movimientoElegido.columnaOrigen

        intentarMovimiento(movimientoElegido.filaDestino, movimientoElegido.columnaDestino)

        cambiarTurno()
    }

    /**
     * Aplica el cambio en la matriz moviendo una pieza de su origen a su destino.
     */
    fun efectuarMovimiento(filaDestino:Int, columnaDestino:Int) {
        tablero[filaDestino][columnaDestino] = tablero[filaSeleccionada][columnaSeleccionada]
        tablero[filaSeleccionada][columnaSeleccionada] = TipoPieza.VACIO
        deseleccionar() // Limpiamos la selección
        coronarSiProcede(filaDestino, columnaDestino)
        comprobarFinDejuego()
    }

    /**
     * Verifica si el movimiento hacia las coordenadas de destino cumple las reglas geométricas de las Damas.
     * Si es legal, realiza el movimiento.
     *
     * @return `true` si el movimiento era válido, `false` si era ilegal.
     */
    fun intentarMovimiento(filaDestino:Int, columnaDestino:Int): Boolean {
        val pieza = tablero[filaSeleccionada][columnaSeleccionada]
        val esDama = (pieza == TipoPieza.DAMA_BLANCA || pieza == TipoPieza.DAMA_NEGRA)

        // Calculamos cuántas casillas nos estamos desplazando en ambos ejes
        val diferenciaFila = filaDestino - filaSeleccionada
        val diferenciaColumna = columnaDestino - columnaSeleccionada

        // Comprobamos la restricción de retroceso (los peones no pueden ir hacia atrás)
        val direccioncorrecta = esDama ||
                (pieza == TipoPieza.PEON_BLANCO && diferenciaFila < 0) || // Blanca debe subir (fila menor)
                (pieza == TipoPieza.PEON_NEGRO && diferenciaFila > 0)     // Negra debe bajar (fila mayor)

        // Movimiento simple: Avanzar 1 casilla en diagonal
        if (abs(diferenciaFila) == 1 && abs(diferenciaColumna) == 1 && direccioncorrecta) {
            efectuarMovimiento(filaDestino, columnaDestino)
            return true
        }

        // Movimiento de captura: Avanzar 2 casillas en diagonal (saltar)
        if (abs(diferenciaFila) == 2 && abs(diferenciaColumna) == 2 && direccioncorrecta) {
            // Localizamos la casilla intermedia (sobre la que estamos saltando)
            val filaMedia = filaSeleccionada + (diferenciaFila / 2)
            val columnaMedia = columnaSeleccionada + (diferenciaColumna / 2)
            val piezaComida = tablero[filaMedia][columnaMedia]

            // Si hay una pieza ahí y es del enemigo, es una captura válida
            if (piezaComida != TipoPieza.VACIO && piezaComida.jugador != turnoActual) {
                tablero[filaMedia][columnaMedia] = TipoPieza.VACIO // Destruimos la pieza enemiga
                efectuarMovimiento(filaDestino, columnaDestino)
                return true
            }
        }

        // Si no cumple ninguna regla anterior, el movimiento fracasa
        return false
    }

    /**
     * Escanea el tablero completo en busca de fichas sobrevivientes para determinar si alguien ha ganado.
     */
    fun comprobarFinDejuego() {
        var blancasVivas = false
        var negrasVivas = false

        for (fila in 0 until DIMENSION) {
            for (columna in 0 until DIMENSION) {
                if (tablero[fila][columna].jugador == Jugador.BLANCO) blancasVivas = true
                if (tablero[fila][columna].jugador == Jugador.NEGRO) negrasVivas = true
            }
        }

        // Si ya no quedan piezas de un equipo, el contrario es el ganador
        if (!blancasVivas) {
            juegoTerminado = true
            mensaje = "¡Has perdido! :-)"
        }
        else if (!negrasVivas) {
            juegoTerminado = true
            mensaje = "¡Has ganado! :-("
        }
    }

    /**
     * Limpia las variables de estado cuando se suelta una ficha o cambia un turno.
     */
    fun deseleccionar() {
        filaSeleccionada = -1
        columnaSeleccionada = -1
    }
}