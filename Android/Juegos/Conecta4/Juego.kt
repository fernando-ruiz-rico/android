package com.example.myapplication

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
        //if (juegoTerminado || dificultadSeleccionada == null) return

        if (columnaValida(columna)) {
            colocarFicha(columna, Ficha.JUGADOR)
        }
    }
}