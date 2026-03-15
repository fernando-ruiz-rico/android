/**
 * ==============================================================================
 * MOTOR DEL JUEGO: CONECTA 4
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo contiene la inteligencia y las reglas matemáticas del juego.
 * Gestiona un tablero matricial, simula la gravedad de las fichas, comprueba
 * victorias en todas las direcciones y contiene una Inteligencia Artificial (IA)
 * capaz de tomar decisiones ofensivas o defensivas.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Clases Enum: Agrupación de valores constantes con propiedades (Fichas y Dificultad).
 * 2. Matrices (Arrays 2D): Uso de listas anidadas (`MutableList` dentro de `MutableList`).
 * 3. Algoritmia clásica: Búsqueda de patrones en matrices mediante bucles anidados.
 * 4. IA mediante simulación: Ejecución de movimientos temporales para prever el futuro
 * (técnica básica de "backtracking" o evaluación de estados).
 * ==============================================================================
 */

package com.example.myapplication
import kotlin.random.Random

/**
 * Define los niveles de dificultad que puede tener la IA del juego.
 * * @param descripcion Texto explicativo del nivel seleccionado.
 */
enum class Dificultad(val descripcion: String) {
    FACIL("Fácil (Aleatorio)"),
    MEDIO("Medio (Defensivo)"),
    DIFICIL("Difícil (Inteligente)");

    /**
     * Sobrescribimos toString para mostrar directamente la descripción al imprimir el enum.
     * * @return El String con el texto descriptivo del nivel.
     */
    override fun toString() : String = descripcion
}

/**
 * Representa los estados posibles de cada celda en el tablero.
 * * @param simbolo El carácter o emoji que se dibujará en pantalla para representar esta ficha.
 */
enum class Ficha(val simbolo: String) {
    VACIO("  "),
    JUGADOR("🔴"),
    MAQUINA("🟡");

    /**
     * Sobrescribimos toString para que al imprimir la ficha salga su representación visual (emoji).
     * * @return El String correspondiente al símbolo asignado.
     */
    override fun toString() : String = simbolo
}

/**
 * Gestor principal del estado y las reglas del juego Conecta 4.
 */
class Juego {
    // Constantes de las dimensiones clásicas de un tablero de Conecta 4
    val FILAS = 6
    val COLUMNAS = 7
    val NO_ENCONTRADO = -1

    // Matriz bidimensional que representa el tablero. 
    // Se inicializa entera con la ficha "VACIO".
    var tablero = MutableList(FILAS) {
        MutableList(COLUMNAS) {
            Ficha.VACIO
        }
    }

    // Variables que controlan el estado de la partida actual
    var dificultadSeleccionada : Dificultad? = null
    var juegoTerminado = false
    var mensaje = "Seleccione dificultad"

    /**
     * Prepara el tablero y las variables para empezar un juego nuevo.
     *
     * @param dificultad El nivel de dificultad seleccionado para el comportamiento de la IA.
     */
    fun iniciarPartida(dificultad: Dificultad) {
        // Reiniciamos la matriz a su estado original (vacío)
        tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }
        dificultadSeleccionada = dificultad
        juegoTerminado = false
        mensaje = "Tu turno (elige columna)"
    }

    /**
     * Comprueba si es posible echar una ficha en una columna concreta.
     *
     * @param c Índice de la columna a comprobar.
     * @return `true` si la columna es válida y tiene espacio disponible en la parte superior, `false` en caso contrario.
     */
    fun columnaValida(c: Int): Boolean {
        // Retorna verdadero si la columna existe (entre 0 y COLUMNAS - 1)
        // y si la casilla más alta (fila 0) de esa columna aún está vacía.
        return c >= 0 && c < COLUMNAS && tablero[0][c] == Ficha.VACIO
    }

    /**
     * Simula la gravedad: busca la posición más baja disponible en una columna.
     *
     * @param c Índice de la columna donde se quiere dejar caer la ficha.
     * @return El índice de la fila libre más baja, o NO_ENCONTRADO (-1) si la columna está llena.
     */
    fun obtenerFilaLibre(c:Int): Int {
        var filaEncontrada = NO_ENCONTRADO

        // Recorremos las filas desde la parte inferior (FILAS - 1) hacia la superior (0).
        for (f in FILAS - 1 downTo 0) {
            if (filaEncontrada == NO_ENCONTRADO && tablero[f][c] == Ficha.VACIO) {
                // En cuanto encontramos el primer hueco vacío desde abajo, guardamos su posición
                filaEncontrada = f
            }
        }
        return filaEncontrada
    }

    /**
     * Ejecuta el movimiento físico de soltar la ficha en el tablero virtual.
     *
     * @param c Índice de la columna donde se suelta la ficha.
     * @param ficha El tipo de ficha (JUGADOR o MAQUINA) que se va a colocar.
     */
    fun colocarFicha(c:Int, ficha:Ficha) {
        // Calculamos dónde debe "caer" la ficha en esta columna
        val f = obtenerFilaLibre(c)
        
        // Si hay un hueco disponible, registramos la ficha en esa celda de la matriz
        if (f != NO_ENCONTRADO) {
            tablero[f][c] = ficha
        }
    }

    /**
     * Algoritmo central del juego: Busca 4 fichas iguales conectadas.
     * Escanea todo el tablero en las 4 direcciones posibles.
     *
     * @param ficha El tipo de ficha a buscar (JUGADOR o MAQUINA) para comprobar si ha ganado.
     * @return `true` si encuentra al menos 4 fichas de ese tipo conectadas, `false` si no.
     */
    fun comprobarVictoria(ficha: Ficha): Boolean {
        // 1. Búsqueda en dirección HORIZONTAL (-)
        // Limitamos las columnas a (COLUMNAS - 3) para no salirnos de los límites al comprobar c+1, c+2, c+3
        for (f in 0 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha &&
                    tablero[f][c+1] == ficha &&
                    tablero[f][c+2] == ficha &&
                    tablero[f][c+3] == ficha) return true
            }
        }

        // 2. Búsqueda en dirección VERTICAL (|)
        // Limitamos las filas a (FILAS - 3) para no salirnos al buscar hacia abajo
        for (f in 0 until FILAS - 3) {
            for (c in 0 until COLUMNAS) {
                if (tablero[f][c] == ficha &&
                    tablero[f+1][c] == ficha &&
                    tablero[f+2][c] == ficha &&
                    tablero[f+3][c] == ficha) return true
            }
        }

        // 3. Búsqueda en DIAGONAL ASCENDENTE (/)
        // Empezamos desde la fila 3 porque necesitamos espacio hacia arriba para formar 4 en línea
        for (f in 3 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha &&
                    tablero[f-1][c+1] == ficha &&
                    tablero[f-2][c+2] == ficha &&
                    tablero[f-3][c+3] == ficha) return true
            }
        }

        // 4. Búsqueda en DIAGONAL DESCENDENTE (\)
        // Limitamos tanto filas como columnas para evitar salirnos al bajar en diagonal hacia la derecha
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
     * Inteligencia Artificial: Simula movimientos para ver si llevan a la victoria.
     *
     * @param ficha El tipo de ficha con la que se simula el movimiento para encontrar la victoria.
     * @return El índice de la columna ganadora si existe, o NO_ENCONTRADO (-1) si no hay victoria en 1 turno.
     */
    fun buscarMovimientoGanador(ficha: Ficha): Int {
        var columnaGanadora = NO_ENCONTRADO

        // Probamos a dejar caer la ficha virtualmente en todas las columnas
        for (c in 0 until COLUMNAS) {
            if (columnaGanadora == NO_ENCONTRADO && columnaValida(c)) {
                val f = obtenerFilaLibre(c)

                // 1. Realizamos la jugada simulada temporalmente
                tablero[f][c] = ficha

                // 2. Evaluamos el estado: si esta jugada da la victoria, guardamos la columna
                if (comprobarVictoria(ficha)) {
                    columnaGanadora = c
                }

                // 3. Deshacemos la jugada simulada dejando la casilla vacía de nuevo (Backtracking)
                tablero[f][c] = Ficha.VACIO
            }
        }

        return columnaGanadora
    }

    /**
     * Orquesta el comportamiento del ordenador según el nivel de dificultad.
     *
     * @param dificultad El nivel de inteligencia seleccionado (puede ser null si no se ha elegido).
     */
    fun hacerMovimientoMaquina(dificultad: Dificultad?) {
        var columnaElegida = NO_ENCONTRADO

        // COMPORTAMIENTO 1: Actitud ofensiva (Solo nivel Difícil)
        // El ordenador analiza si tiene algún movimiento con el que pueda ganar ya mismo.
        if (dificultad == Dificultad.DIFICIL) {
            columnaElegida = buscarMovimientoGanador(Ficha.MAQUINA)
        }

        // COMPORTAMIENTO 2: Actitud defensiva (Niveles Medio y Difícil)
        // Si la máquina no puede ganar, analiza si el JUGADOR está a punto de ganar y le bloquea.
        if (columnaElegida == NO_ENCONTRADO) {
            if (dificultad == Dificultad.DIFICIL || dificultad == Dificultad.MEDIO) {
                columnaElegida = buscarMovimientoGanador(Ficha.JUGADOR)
            }
        }

        // COMPORTAMIENTO 3: Actitud aleatoria (Nivel Fácil o sin jugadas obvias)
        // Si no hay jugada ganadora ni bloqueos necesarios, elige una columna al azar.
        if (columnaElegida == NO_ENCONTRADO) {
            var columnaAzar: Int
            var esValida = false

            // Sigue probando columnas aleatorias generadas al vuelo hasta que encuentre una no llena
            while (!esValida) {
                columnaAzar = Random.nextInt(COLUMNAS)
                if (columnaValida(columnaAzar)) {
                    columnaElegida = columnaAzar
                    esValida = true
                }
            }
        }

        // Finalmente, ejecutamos el movimiento calculado
        colocarFicha(columnaElegida, Ficha.MAQUINA)
    }

    /**
     * Traduce la matriz bidimensional a un String formateado con bordes y emojis.
     *
     * @return Una cadena de texto con la representación visual del tablero lista para imprimirse.
     */
    fun obtenerMapaComoTexto(): String {
        var texto = "\n"
        
        // Cabecera numerada de columnas
        for (c in 0 until COLUMNAS) {
            texto += " $c "
        }

        texto += "\n+--------------------+\n"

        // Recorremos cada fila para dibujar las fichas y los separadores
        for (f in 0 until FILAS) {
            texto += "|" // Borde izquierdo
            for (c in 0 until COLUMNAS) {
                // Al concatenar la ficha, Kotlin llama automáticamente al override `toString()` (los emojis)
                texto += "${tablero[f][c]}|" // La ficha y el separador vertical
            }
            texto += "\n"
        }

        texto += "+--------------------+"

        return texto
    }

    /**
     * Función principal que se ejecuta cada vez que el jugador pulsa un botón.
     * Encapsula un turno completo (movimiento jugador -> comprobación -> movimiento máquina -> comprobación).
     *
     * @param columna La columna elegida por el jugador humano para intentar soltar su ficha.
     */
    fun turno(columna: Int) {
        // Bloqueo de seguridad: ignorar toques si el juego ha terminado
        if (juegoTerminado || dificultadSeleccionada == null) return

        if (columnaValida(columna)) {
            // 1. Turno del Jugador
            colocarFicha(columna, Ficha.JUGADOR)

            if (comprobarVictoria(Ficha.JUGADOR)) {
                mensaje = "¡Enhorabuena, has ganado!"
                juegoTerminado = true
                return // Cortamos la función, la máquina ya no mueve
            }

            // 2. Turno de la Máquina
            hacerMovimientoMaquina(dificultadSeleccionada)

            if (comprobarVictoria(Ficha.MAQUINA)) {
                mensaje = "Ha ganado el ordenador"
                juegoTerminado = true
            }
        }
    }
}