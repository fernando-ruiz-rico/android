package com.example.myapplication
import kotlin.random.Random

enum class Dificultad(val descripcion: String) {
    FACIL("Fácil (Aleatorio)"),
    MEDIO("Medio (Defensivo)"),
    DIFICIL("Difícil (Inteligente)");

    // Sobrescribimos toString para mostrar directamente la descripción al imprimir el enum
    override fun toString() : String = descripcion
}

enum class Ficha(val simbolo: String) {
    VACIO("  "),
    JUGADOR("🔴"),
    MAQUINA("🟡");

    // Sobrescribimos toString para que al imprimir la ficha salga su representación visual
    override fun toString() : String = simbolo
}

class Juego {
    val FILAS = 6
    val COLUMNAS = 7
    val NO_ENCONTRADO = -1

    var tablero = MutableList(FILAS) {
        MutableList(COLUMNAS) {
            Ficha.VACIO
        }
    }

    var dificultadSeleccionada : Dificultad? = null
    var juegoTerminado = false
    var mensaje = "Seleccione dificultad"

    fun iniciarPartida(dificultad: Dificultad) {
        tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }
        dificultadSeleccionada = dificultad
        juegoTerminado = false
        mensaje = "Tu turno (elige columna)"
    }

    fun columnaValida(c: Int): Boolean {
        // Retorna verdadero si la columna existe (entre 0 y COLUMNAS - 1)
        // y si la primera fila de esa columna aún está vacía (no está llena).
        return c >= 0 && c < COLUMNAS && tablero[0][c] == Ficha.VACIO
    }

    fun obtenerFilaLibre(c:Int): Int {
        var filaEncontrada = NO_ENCONTRADO

        // Recorremos las filas desde la parte inferior (FILAS - 1) hacia la superior (0).
        // Esto simula la gravedad de la ficha cayendo hasta el fondo.
        for (f in FILAS - 1 downTo 0) {
            if (filaEncontrada == NO_ENCONTRADO && tablero[f][c] == Ficha.VACIO) {
                // En cuanto encontramos el primer hueco vacío desde abajo, guardamos su posición
                filaEncontrada = f
            }
        }
        return filaEncontrada
    }

    fun colocarFicha(c:Int, ficha:Ficha) {
        // Calculamos dónde debe "caer" la ficha en esta columna
        val f = obtenerFilaLibre(c)
        // Si la columna no estaba llena, asignamos la ficha a la posición calculada
        if (f != NO_ENCONTRADO) {
            tablero[f][c] = ficha
        }
    }

    fun comprobarVictoria(ficha: Ficha): Boolean {
        // Búsqueda en dirección HORIZONTAL (-)
        // Limitamos las columnas a COLUMNAS - 3 para no salirnos de los límites al sumar +3
        for (f in 0 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha &&
                    tablero[f][c+1] == ficha &&
                    tablero[f][c+2] == ficha &&
                    tablero[f][c+3] == ficha) return true
            }
        }

        // Búsqueda en dirección VERTICAL (|)
        // Limitamos las filas a FILAS - 3 para no salirnos al buscar hacia abajo
        for (f in 0 until FILAS - 3) {
            for (c in 0 until COLUMNAS) {
                if (tablero[f][c] == ficha &&
                    tablero[f+1][c] == ficha &&
                    tablero[f+2][c] == ficha &&
                    tablero[f+3][c] == ficha) return true
            }
        }

        // Búsqueda en DIAGONAL ASCENDENTE (/)
        // Empezamos desde la fila 3 porque necesitamos espacio hacia arriba para formar 4 en línea
        for (f in 3 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha &&
                    tablero[f-1][c+1] == ficha &&
                    tablero[f-2][c+2] == ficha &&
                    tablero[f-3][c+3] == ficha) return true
            }
        }

        // Búsqueda en DIAGONAL DESCENDENTE (\)
        // Limitamos tanto filas como columnas para evitar salirnos al bajar en diagonal
        for (f in 0 until FILAS - 3) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha &&
                    tablero[f+1][c+1] == ficha &&
                    tablero[f+2][c+2] == ficha &&
                    tablero[f+3][c+3] == ficha) return true
            }
        }

        // Si recorremos todo el tablero y no hay coincidencias, no hay victoria todavía
        return false
    }

    fun buscarMovimientoGanador(ficha: Ficha): Int {
        var columnaGanadora = NO_ENCONTRADO

        // Probamos a dejar caer la ficha virtualmente en todas las columnas
        for (c in 0 until COLUMNAS) {
            if (columnaGanadora == NO_ENCONTRADO && columnaValida(c)) {
                val f = obtenerFilaLibre(c)

                // Realizamos la jugada simulada
                tablero[f][c] = ficha

                // Si esta jugada simulada otorga la victoria, guardamos la columna
                if (comprobarVictoria(ficha)) {
                    columnaGanadora = c
                }

                // Deshacemos la jugada simulada dejando la casilla vacía de nuevo
                tablero[f][c] = Ficha.VACIO
            }
        }

        return columnaGanadora
    }

    fun hacerMovimientoMaquina(dificultad: Dificultad?) {
        var columnaElegida = NO_ENCONTRADO

        // 1. Actitud ofensiva: Si la dificultad es máxima, el ordenador busca ganar inmediatamente
        if (dificultad == Dificultad.DIFICIL) {
            columnaElegida = buscarMovimientoGanador(Ficha.MAQUINA)
        }

        // 2. Actitud defensiva: Si no puede ganar, o si está en nivel Medio/Difícil, busca bloquearte
        if (columnaElegida == NO_ENCONTRADO) {
            if (dificultad == Dificultad.DIFICIL || dificultad == Dificultad.MEDIO) {
                columnaElegida = buscarMovimientoGanador(Ficha.JUGADOR)
            }
        }

        // 3. Actitud aleatoria: Si no hay jugada clara, o está en Fácil, elige al azar
        if (columnaElegida == NO_ENCONTRADO) {
            var columnaAzar: Int
            var esValida = false

            // Sigue probando columnas aleatorias hasta que encuentre una donde quepa una ficha
            while (!esValida) {
                columnaAzar = Random.nextInt(COLUMNAS)
                if (columnaValida(columnaAzar)) {
                    columnaElegida = columnaAzar
                    esValida = true
                }
            }
        }

        // Finalmente, la máquina efectúa su jugada en la columna seleccionada
        colocarFicha(columnaElegida, Ficha.MAQUINA)
    }

    fun obtenerMapaComoTexto(): String {
        var texto = "\n"
        for (c in 0 until COLUMNAS) {
            texto += " $c "
        }

        texto += "\n+--------------------+\n"

        // Recorremos cada fila para dibujar las fichas y los separadores
        for (f in 0 until FILAS) {
            texto += "|" // Borde izquierdo
            for (c in 0 until COLUMNAS) {
                texto += "${tablero[f][c]}|" // La ficha y el separador vertical
            }
            texto += "\n"
        }

        texto += "+--------------------+"

        return texto
    }

    fun turno(columna: Int) {
        if (juegoTerminado || dificultadSeleccionada == null) return

        if (columnaValida(columna)) {
            colocarFicha(columna, Ficha.JUGADOR)

            if (comprobarVictoria(Ficha.JUGADOR)) {
                mensaje = "¡Enhorabuena, has ganado!"
                juegoTerminado = true
                return
            }

            hacerMovimientoMaquina(dificultadSeleccionada)

            if (comprobarVictoria(Ficha.MAQUINA)) {
                mensaje = "Ha ganado el ordenador"
                juegoTerminado = true
            }
        }
    }
}