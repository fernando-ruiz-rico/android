import javax.transaction.xa.Xid
import kotlin.random.Random
import kotlin.random.nextInt

const val DIMENSION = 10
const val MUNICION_MAXIMA = 1

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

fun colocarFlotaCompleta() {
    for (barco in TipoBarco.values()) {
        colocarBarcoAleatorio(barco)
    }
}

fun colocarBarcoAleatorio(barco: TipoBarco) {
    var colocado = false
    while(!colocado) {
        val fila = Random.nextInt(DIMENSION)
        val columna = Random.nextInt(DIMENSION)
        val horizontal = Random.nextBoolean()

        if (esPosicionValida(fila, columna, barco.longitud, horizontal)) {
            colocarBarcoEnMatriz(fila, columna, barco.longitud, horizontal)
            colocado = true
        }
    }
}

fun esPosicionValida(fila:Int, columna:Int, longitud:Int, horizontal:Boolean): Boolean {
    val anchoBarco = if (horizontal) longitud else 1
    val altoBarco = if (horizontal) 1 else longitud

    if (fila + altoBarco > DIMENSION || columna + anchoBarco > DIMENSION) {
        return false
    }

    val filaInicio = (fila - 1).coerceAtLeast(0)
    val columnaInicio = (columna - 1).coerceAtLeast(0)
    val filaFin = (fila + altoBarco).coerceAtMost(DIMENSION - 1)
    val columnaFin = (columna + anchoBarco).coerceAtMost(DIMENSION - 1)

    for (i in filaInicio..filaFin) {
        for (j in columnaInicio..columnaFin) {
            if (Juego.oceano[i][j] != EstadoCasilla.AGUA) {
                return false
            }
        }
    }

    return true
}

fun colocarBarcoEnMatriz(fila:Int, columna:Int, longitud:Int, horizontal:Boolean) {
    for (i in 0 until longitud) {
        if (horizontal) {
            Juego.oceano[fila][columna + i] = EstadoCasilla.BARCO
        }
        else {
            Juego.oceano[fila + i][columna] = EstadoCasilla.BARCO
        }
    }
}

fun main() {
    println("--- HUNDIR LA FLOTA ---")

    colocarFlotaCompleta()

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