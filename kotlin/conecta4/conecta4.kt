import kotlin.random.Random

/* * Estructura del código RGB: "\u001B[38;2;R;G;Bm"
* 1. "\u001B"  -> Es el código de "Escape". Le dice a la consola: "¡Atención, viene un comando!"
* 2. "["       -> Abre el comando.
* 3. "38;2;"   -> Le indica a la consola que vamos a usar un color de texto en formato RGB.
* 4. "R;G;B"   -> Son los valores de Rojo (Red), Verde (Green) y Azul (Blue). Van de 0 a 255.
* 5. "m"       -> Cierra el comando.
 */

val RESET = "\u001B[0m"
val ROJO = "\u001B[38;2;255;0;0m"
val AMARILLO = "\u001B[38;2;255;255;0m"
val AZUL = "\u001B[38;2;0;100;255m"

enum class Dificultad(val descripcion: String) {
    FACIL("Fácil (Aleatorio)"),
    MEDIO("Medio (Defensivo)"),
    DIFICIL("Difícil (Inteligente)");

    override fun toString() : String = descripcion
}

enum class Ficha(val simbolo: String) {
    VACIO(" . "),
    JUGADOR("$ROJO X $RESET"),
    MAQUINA("$AMARILLO 0 $RESET");

    override fun toString() : String = simbolo
}

const val FILAS = 6
const val COLUMNAS = 7

const val NO_ENCONTRADO = -1

val tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }

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

fun hacerMovimientoJugador() {
    var movimientoValido = false

    while (!movimientoValido) {
        print("Tu turno (Columna 0 a ${COLUMNAS - 1}): ")

        val columna = readln().trim().toIntOrNull()

        if (columna != null) {
            if (columnaValida(columna)) {
                colocarFicha(columna, Ficha.JUGADOR)
                movimientoValido = true
            }
            else {
                println("Movimiento no válido. La columna está llena o no existe")
            }
        }
        else {
            println("Por favor, introduce un número.")
        }
    }
}

fun hacerMovimientoMaquina(dificultad: Dificultad) {
    println("Turno del ordenador...")

    var columnaElegida = NO_ENCONTRADO

    if (dificultad == Dificultad.DIFICIL) {
        columnaElegida = buscarMovimientoGanador(Ficha.MAQUINA)
    }

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

fun buscarMovimientoGanador(ficha: Ficha): Int {
    var columnaGanadora = NO_ENCONTRADO

    for (c in 0 until COLUMNAS) {
        if (columnaGanadora == NO_ENCONTRADO && columnaValida(c)) {
            val f = obtenerFilaLibre(c)

            tablero[f][c] = ficha

            if (comprobarVictoria(ficha)) {
                columnaGanadora = c
            }

            tablero[f][c] = Ficha.VACIO
        }
    }

    return columnaGanadora
}

fun imprimirTablero() {
    println()

    print(" ")
    for (c in 0 until COLUMNAS) {
        print(" $c  ")
    }
    println("\n$AZUL+---------------------------+$RESET")

    for (f in 0 until FILAS) {
        print("$AZUL|$RESET")
        for (c in 0 until COLUMNAS) {
            print("${tablero[f][c]}$AZUL|$RESET")
        }
        println()
    }

    println("$AZUL+---------------------------+$RESET")
}

fun comprobarVictoria(ficha: Ficha): Boolean {
    // HORIZONTAL (-)
    for (f in 0 until FILAS) {
        for (c in 0 until COLUMNAS - 3) {
            if (tablero[f][c] == ficha &&
                tablero[f][c+1] == ficha &&
                tablero[f][c+2] == ficha &&
                tablero[f][c+3] == ficha) return true
        }
    }

    // VERTICAL (|)
    for (f in 0 until FILAS - 3) {
        for (c in 0 until COLUMNAS) {
            if (tablero[f][c] == ficha &&
                tablero[f+1][c] == ficha &&
                tablero[f+2][c] == ficha &&
                tablero[f+3][c] == ficha) return true
        }
    }

    // DIAGONAL ASCENDENTE (/)
    for (f in 3 until FILAS) {
        for (c in 0 until COLUMNAS - 3) {
            if (tablero[f][c] == ficha &&
                tablero[f-1][c+1] == ficha &&
                tablero[f-2][c+2] == ficha &&
                tablero[f-3][c+3] == ficha) return true
        }
    }

    // DIAGONAL DESCENDENTE (\)
    for (f in 0 until FILAS - 3) {
        for (c in 0 until COLUMNAS - 3) {
            if (tablero[f][c] == ficha &&
                tablero[f+1][c+1] == ficha &&
                tablero[f+2][c+2] == ficha &&
                tablero[f+3][c+3] == ficha) return true
        }
    }

    return false
}

fun main() {
    println("=================")
    println("=== CONECTA 4 ===")
    println("=================")
    println("Elige dificultad: ")

    var numeroOpcion = 1
    for (dificultad in Dificultad.values()) {
        println("$numeroOpcion. $dificultad")
        numeroOpcion++
    }

    var dificultadSeleccionada : Dificultad? = null

    while (dificultadSeleccionada == null) {
        print("Opcion: ")
        val opcion = readln().trim().toIntOrNull()

        if (opcion != null && opcion >= 1 && opcion <= Dificultad.values().size) {
            dificultadSeleccionada = Dificultad.values()[opcion - 1]
        }
        else {
            println("Opción no válida. Elige un valor entre 1 y ${Dificultad.values().size}")
        }
    }

    println("Estupendo. Has elegido nivel $dificultadSeleccionada")

    var turnoJugador = true
    var juegoTerminado = false

    while (!juegoTerminado) {
        imprimirTablero()

        if (turnoJugador) {
            hacerMovimientoJugador()
        }
        else {
            hacerMovimientoMaquina(dificultadSeleccionada)
        }

        val fichaActual = if (turnoJugador) Ficha.JUGADOR else Ficha.MAQUINA

        if (comprobarVictoria(fichaActual)) {
            imprimirTablero()
            if (turnoJugador) {
                println("¡Enhorabuena, has ganado!")
            }
            else {
                println("Ha ganado el ordenador")
            }
            juegoTerminado = true
        }

        turnoJugador = !turnoJugador
    }
}