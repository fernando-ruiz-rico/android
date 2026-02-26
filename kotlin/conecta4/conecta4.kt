import kotlin.random.Random

/**
 * ==========================================================================
 * JUEGO: CONECTA 4
 * ==========================================================================
 * Objetivo del juego:
 * Tu objetivo principal es ser el primero en formar una línea continua de cuatro
 * de tus fichas, ya sea en horizontal, vertical o diagonal, dentro del tablero.
 * Jugarás contra el ordenador, que intentará bloquearte y formar su propia línea.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Enumeraciones avanzadas: Uso de `enum class` con propiedades y sobrescritura de `toString()`.
 * 2. Colecciones mutables: Creación y manejo de una matriz bidimensional con `MutableList`.
 * 3. Estructuras de control de flujo: Manejo de rangos e iteraciones inversas (ej. `downTo`).
 * 4. Seguridad contra nulos (Null Safety): Uso de `toIntOrNull()` para manejar entradas de usuario
 * sin provocar caídas del programa por excepciones.
 * 5. Funciones: Separación entre funciones que calculan estados y funciones
 * que modifican el estado global del tablero o interactúan con la consola.
 *
 * Estructura del código RGB: "\u001B[38;2;R;G;Bm"
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

/**
 * Define los niveles de dificultad disponibles para el ordenador.
 *
 * @property descripcion La etiqueta de texto que describe el comportamiento de cada nivel.
 */
enum class Dificultad(val descripcion: String) {
    FACIL("Fácil (Aleatorio)"),
    MEDIO("Medio (Defensivo)"),
    DIFICIL("Difícil (Inteligente)");

    // Sobrescribimos toString para mostrar directamente la descripción al imprimir el enum
    override fun toString() : String = descripcion
}

/**
 * Define los estados posibles de cada casilla en el tablero.
 *
 * @property simbolo La representación visual (con color) que se imprimirá por pantalla.
 */
enum class Ficha(val simbolo: String) {
    VACIO(" . "),
    JUGADOR("$ROJO X $RESET"),
    MAQUINA("$AMARILLO 0 $RESET");

    // Sobrescribimos toString para que al imprimir la ficha salga su representación visual
    override fun toString() : String = simbolo
}

const val FILAS = 6
const val COLUMNAS = 7

const val NO_ENCONTRADO = -1

// Inicialización del tablero como una matriz bidimensional llena de fichas vacías
val tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }

/**
 * Comprueba si es posible colocar una ficha en una columna específica.
 *
 * @param c El índice de la columna que se desea comprobar.
 * @return `true` si la columna está dentro de los límites y su casilla superior está vacía.
 */
fun columnaValida(c: Int): Boolean {
    // Retorna verdadero si la columna existe (entre 0 y COLUMNAS - 1)
    // y si la primera fila de esa columna aún está vacía (no está llena).
    return c >= 0 && c < COLUMNAS && tablero[0][c] == Ficha.VACIO
}

/**
 * Busca la posición más baja (la fila más profunda) que esté vacía en una columna dada.
 *
 * @param c El índice de la columna donde se quiere dejar caer la ficha.
 * @return El índice de la fila libre más baja, o NO_ENCONTRADO si la columna está llena.
 */
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

/**
 * Fija una ficha en el tablero dentro de una columna específica.
 *
 * @param c El índice de la columna.
 * @param ficha El tipo de ficha (JUGADOR o MAQUINA) que se va a colocar.
 */
fun colocarFicha(c:Int, ficha:Ficha) {
    // Calculamos dónde debe "caer" la ficha en esta columna
    val f = obtenerFilaLibre(c)
    // Si la columna no estaba llena, asignamos la ficha a la posición calculada
    if (f != NO_ENCONTRADO) {
        tablero[f][c] = ficha
    }
}

/**
 * Gestiona el turno del jugador humano, pidiendo la entrada por consola
 * y validando que el movimiento sea correcto.
 */
fun hacerMovimientoJugador() {
    var movimientoValido = false

    // Bucle que se repite hasta que introduzcas un movimiento legal
    while (!movimientoValido) {
        print("Tu turno (Columna 0 a ${COLUMNAS - 1}): ")

        // Leemos la entrada, quitamos espacios y la intentamos convertir a número de forma segura
        val columna = readln().trim().toIntOrNull()

        if (columna != null) {
            // Si has introducido un número, comprobamos que la columna se pueda jugar
            if (columnaValida(columna)) {
                colocarFicha(columna, Ficha.JUGADOR)
                movimientoValido = true // Salimos del bucle
            }
            else {
                println("Movimiento no válido. La columna está llena o no existe")
            }
        }
        else {
            // Si la conversión a entero falla (ej. has escrito letras), mostramos un aviso
            println("Por favor, introduce un número.")
        }
    }
}

/**
 * Ejecuta el turno del ordenador, determinando su jugada según el nivel de dificultad elegido.
 *
 * @param dificultad El nivel de inteligencia que usará la máquina para decidir su movimiento.
 */
fun hacerMovimientoMaquina(dificultad: Dificultad) {
    println("Turno del ordenador...")

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

/**
 * Simula cada jugada posible para comprobar si resultaría en una victoria
 * para la ficha proporcionada.
 *
 * @param ficha La ficha que queremos comprobar (puede ser para ganar o para bloquear).
 * @return El índice de la columna ganadora, o NO_ENCONTRADO si no hay victoria a la vista.
 */
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

/**
 * Dibuja el estado actual del tablero por consola, incluyendo los colores y bordes.
 */
fun imprimirTablero() {
    println()

    // Imprimimos la cabecera con los números de cada columna para guiarte
    print(" ")
    for (c in 0 until COLUMNAS) {
        print(" $c  ")
    }
    // Dibuja el borde superior del tablero
    println("\n$AZUL+---------------------------+$RESET")

    // Recorremos cada fila para dibujar las fichas y los separadores
    for (f in 0 until FILAS) {
        print("$AZUL|$RESET") // Borde izquierdo
        for (c in 0 until COLUMNAS) {
            print("${tablero[f][c]}$AZUL|$RESET") // Imprimimos la ficha y el separador vertical
        }
        println()
    }

    // Dibuja el borde inferior del tablero
    println("$AZUL+---------------------------+$RESET")
}

/**
 * Revisa todo el tablero para verificar si hay una alineación de cuatro fichas iguales.
 *
 * @param ficha El tipo de ficha a buscar (JUGADOR o MAQUINA).
 * @return `true` si se encuentran cuatro fichas conectadas en cualquier dirección.
 */
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

/**
 * Función principal del programa. Gestiona el ciclo completo del juego,
 * la elección de dificultad y la alternancia de turnos.
 */
fun main() {
    println("=================")
    println("=== CONECTA 4 ===")
    println("=================")
    println("Elige dificultad: ")

    // Mostramos dinámicamente las opciones basándonos en los valores del enum
    var numeroOpcion = 1
    for (dificultad in Dificultad.values()) {
        println("$numeroOpcion. $dificultad")
        numeroOpcion++
    }

    var dificultadSeleccionada : Dificultad? = null

    // Bucle para forzar la selección de una dificultad válida por tu parte
    while (dificultadSeleccionada == null) {
        print("Opcion: ")
        val opcion = readln().trim().toIntOrNull()

        // Verificamos que no sea nulo y que esté dentro del rango de opciones posibles
        if (opcion != null && opcion >= 1 && opcion <= Dificultad.values().size) {
            // Asignamos la dificultad seleccionada basándonos en el índice
            dificultadSeleccionada = Dificultad.values()[opcion - 1]
        }
        else {
            println("Opción no válida. Elige un valor entre 1 y ${Dificultad.values().size}")
        }
    }

    println("Estupendo. Has elegido nivel $dificultadSeleccionada")

    // Variables de control para gestionar el flujo del juego
    var turnoJugador = true
    var juegoTerminado = false

    // Bucle principal: alternará turnos hasta que alguien gane
    while (!juegoTerminado) {
        imprimirTablero()

        // Según de quién sea el turno, llamamos a la función de movimiento correspondiente
        if (turnoJugador) {
            hacerMovimientoJugador()
        }
        else {
            hacerMovimientoMaquina(dificultadSeleccionada)
        }

        // Identificamos quién acaba de poner ficha para comprobar si ha ganado
        val fichaActual = if (turnoJugador) Ficha.JUGADOR else Ficha.MAQUINA

        // Comprobamos la victoria tras el último movimiento realizado
        if (comprobarVictoria(fichaActual)) {
            imprimirTablero() // Mostramos cómo queda el tablero al final
            if (turnoJugador) {
                println("¡Enhorabuena, has ganado!")
            }
            else {
                println("Ha ganado el ordenador")
            }
            // Cambiamos el estado para que el bucle while finalice
            juegoTerminado = true
        }

        // Intercambiamos el turno para la siguiente iteración
        turnoJugador = !turnoJugador
    }
}