enum class Dificultad(val descripcion: String) {
    FACIL("Fácil (Aleatorio)"),
    MEDIO("Medio (Defensivo)"),
    DIFICIL("Difícil (Inteligente)");

    override fun toString() : String = descripcion
}

enum class Ficha(val simbolo: String) {
    VACIO(" . "),
    JUGADOR(" X "),
    MAQUINA(" 0 ");

    override fun toString() : String = simbolo
}

const val FILAS = 6
const val COLUMNAS = 7

const val NO_ENCONTRADO = -1

val tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }

fun columValida(c: Int): Boolean {
    return c >= 0 && c < COLUMNAS && tablero[0][c] == Ficha.VACIO
}

fun colocarFicha(c:Int, ficha:Ficha) {
}

fun hacerMovimientoJugador() {
    var movimientoValido = false

    while (!movimientoValido) {
        print("Tu turno (Columna 0 a ${COLUMNAS - 1}): ")

        val columna = readln().trim().toIntOrNull()

        if (columna != null) {
            if (columValida(columna)) {
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

fun imprimirTablero() {
    println()

    print(" ")
    for (c in 0 until COLUMNAS) {
        print(" $c  ")
    }
    println("\n+---------------------------+")

    for (f in 0 until FILAS) {
        print("|")
        for (c in 0 until COLUMNAS) {
            print("${tablero[f][c]}|")
        }
        println()
    }

    println("+---------------------------+")
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

    imprimirTablero()

    hacerMovimientoJugador()
}