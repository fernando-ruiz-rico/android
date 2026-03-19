package com.example.myapplication

import kotlin.random.Random

class JuegoConecta4 {
    enum class Dificultad(val descripcion: String) {
        FACIL("Fácil (Aleatorio)"),
        MEDIO("Medio (Defensivo)"),
        DIFICIL("Difícil (Inteligente)");
        override fun toString() : String = descripcion
    }

    enum class Ficha(val simbolo: String) {
        VACIO("  "),
        JUGADOR("🔴"),
        MAQUINA("🟡");
        override fun toString() : String = simbolo
    }

    companion object {
        const val FILAS = 6
        const val COLUMNAS = 7
        const val NO_ENCONTRADO = -1
    }

    var tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }
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
        return c >= 0 && c < COLUMNAS && tablero[0][c] == Ficha.VACIO
    }

    fun obtenerFilaLibre(c:Int): Int {
        var filaEncontrada = NO_ENCONTRADO
        for (f in FILAS - 1 downTo 0) {
            if (filaEncontrada == NO_ENCONTRADO && tablero[f][c] == Ficha.VACIO) {
                filaEncontrada = f
            }
        }
        return filaEncontrada
    }

    fun colocarFicha(c:Int, ficha:Ficha) {
        val f = obtenerFilaLibre(c)
        if (f != NO_ENCONTRADO) {
            tablero[f][c] = ficha
        }
    }

    fun comprobarVictoria(ficha: Ficha): Boolean {
        for (f in 0 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha && tablero[f][c+1] == ficha &&
                    tablero[f][c+2] == ficha && tablero[f][c+3] == ficha) return true
            }
        }
        for (f in 0 until FILAS - 3) {
            for (c in 0 until COLUMNAS) {
                if (tablero[f][c] == ficha && tablero[f+1][c] == ficha &&
                    tablero[f+2][c] == ficha && tablero[f+3][c] == ficha) return true
            }
        }
        for (f in 3 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha && tablero[f-1][c+1] == ficha &&
                    tablero[f-2][c+2] == ficha && tablero[f-3][c+3] == ficha) return true
            }
        }
        for (f in 0 until FILAS - 3) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha && tablero[f+1][c+1] == ficha &&
                    tablero[f+2][c+2] == ficha && tablero[f+3][c+3] == ficha) return true
            }
        }
        return false
    }

    fun buscarMovimientoGanador(ficha: Ficha): Int {
        var columnaGanadora = NO_ENCONTRADO
        for (c in 0 until COLUMNAS) {
            if (columnaGanadora == NO_ENCONTRADO && columnaValida(c)) {
                val f = obtenerFilaLibre(c)
                tablero[f][c] = ficha
                if (comprobarVictoria(ficha)) columnaGanadora = c
                tablero[f][c] = Ficha.VACIO
            }
        }
        return columnaGanadora
    }

    fun hacerMovimientoMaquina(dificultad: Dificultad?) {
        var columnaElegida = NO_ENCONTRADO
        if (dificultad == Dificultad.DIFICIL) columnaElegida = buscarMovimientoGanador(Ficha.MAQUINA)
        if (columnaElegida == NO_ENCONTRADO) {
            if (dificultad == Dificultad.DIFICIL || dificultad == Dificultad.MEDIO) {
                columnaElegida = buscarMovimientoGanador(Ficha.JUGADOR)
            }
        }
        if (columnaElegida == NO_ENCONTRADO) {
            var columnaAzar: Int
            var esValida = false
            while (!esValida) {
                columnaAzar = Random.nextInt(COLUMNAS)
                if (columnaValida(columnaAzar)) {
                    columnaElegida = columnaAzar
                    esValida = true
                }
            }
        }
        colocarFicha(columnaElegida, Ficha.MAQUINA)
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