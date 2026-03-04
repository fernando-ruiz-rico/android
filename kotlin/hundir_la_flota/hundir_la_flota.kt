import kotlin.random.Random

const val DIMENSION = 10
const val MUNICION_MAXIMA = 5

enum class TipoBarco(val longitud: Int) {
    PORTAAVIONES(5),
    ACORAZADO(4),
    CRUCERO(3),
    SUBMARINO(3),
    DESTRUCTOR(2)
}

enum class EstadoCasilla(val simbolo:String, r:Int, g:Int, b:Int) {
    AGUA("~", 0, 180, 255),
    BARCO("B", 160, 32, 240),
    TOCADO("X", 255, 0, 0),
    FALLO("o", 180, 180, 180);

    private val codigoColor:String = "\u001B[38;2;${r};${g};${b}m"
    private val resetColor:String = "\u001B[0m"

    override fun toString(): String = "$codigoColor$simbolo$resetColor"
}

object Juego {
    val oceano = MutableList(DIMENSION) {
        MutableList(DIMENSION) {
            EstadoCasilla.AGUA
        }
    }

    val impactosNecesarios = TipoBarco.values().sumOf({ it.longitud })
}

fun imprimirOceano(revelarTodo: Boolean) {
    for ((indexFila, fila) in Juego.oceano.withIndex()) {
        print("$indexFila| ")
        for (casilla in fila) {
            print("$casilla ")
        }
        println("|$indexFila")
    }
}

fun pedirCoordenada(mensaje: String): Int {
    while(true) {
        print("$mensaje (0 - ${DIMENSION - 1}): ")
        val coordenada = readln().trim().toIntOrNull()
        if (coordenada != null && coordenada in 0 until DIMENSION) {
            return coordenada
        }
        println("Error: Introduce un número entero válido (entre 0 y ${DIMENSION - 1})")
    }
}

fun realizarDisparo():Boolean {
    var disparoValido = false
    var fila = -1
    var columna = -1

    while(!disparoValido) {
        fila = pedirCoordenada("Fila")
        columna = pedirCoordenada("Columna")

        val estadoActual = Juego.oceano[fila][columna]

        if (estadoActual == EstadoCasilla.TOCADO || estadoActual == EstadoCasilla.FALLO) {
            println("Error: Ya has disparado ahí. Elige otra casilla.")
        }
        else {
            disparoValido = true
        }
    }

    Juego.oceano[fila][columna] = EstadoCasilla.TOCADO

    return false
}

fun main() {
    println("--- HUNDIR LA FLOTA ---")

    var aciertos = 0
    var misilesRestantes = MUNICION_MAXIMA
    var juegoTerminado = false

    while(!juegoTerminado) {
        imprimirOceano(false)
        println("----")
        println("Misiles: $misilesRestantes | Aciertos: $aciertos/${Juego.impactosNecesarios}")

        var impacto = realizarDisparo()
        misilesRestantes--
    }
}