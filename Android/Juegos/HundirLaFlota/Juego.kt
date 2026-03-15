/**
 * ==============================================================================
 * MOTOR DEL JUEGO: HUNDIR LA FLOTA (BATTLESHIP)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo contiene la lógica principal del clásico juego "Hundir la Flota".
 * Se encarga de gestionar el tablero (océano), posicionar los barcos de forma
 * aleatoria y validar los disparos del jugador, llevando el conteo de la munición.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Matrices bidimensionales: Uso avanzado para representar coordenadas (x,y).
 * 2. Funciones de extensión matemáticas: Uso de `coerceAtLeast` y `coerceAtMost`
 * para evitar salirnos de los límites de la matriz al comprobar casillas adyacentes.
 * 3. Búsqueda y validación: Algoritmos para comprobar colisiones antes de colocar objetos.
 * ==============================================================================
 */

package com.example.myapplication

import kotlin.random.Random

/**
 * Define los tipos de embarcaciones que componen la flota.
 * * @param longitud El número de casillas consecutivas que ocupa el barco en el tablero.
 */
enum class TipoBarco(val longitud: Int) {
    PORTAAVIONES(5),
    ACORAZADO(4),
    CRUCERO(3),
    SUBMARINO(3),
    DESTRUCTOR(2)
}

/**
 * Representa los diferentes estados en los que puede estar una coordenada del tablero.
 * * @param simbolo El emoji que representa visualmente el estado de la casilla.
 */
enum class EstadoCasilla(val simbolo:String) {
    AGUA("🟦"),
    BARCO("🚢"),
    TOCADO("💥"),
    FALLO("⚪");

    /**
     * Sobrescribimos toString para facilitar la impresión del emoji en la interfaz.
     * * @return El String que contiene el emoji asignado al estado.
     */
    override fun toString(): String = simbolo
}

/**
 * Clase principal que gestiona el estado y las reglas de la partida.
 */
class Juego {
    // Dimensiones del tablero (10x10 es el estándar clásico)
    val DIMENSION = 10
    // Límite de disparos que el jugador puede fallar/acertar
    val MUNICION_MAXIMA = 50

    // Matriz bidimensional que representa el área de juego
    var oceano = MutableList(DIMENSION) {
        MutableList(DIMENSION) {
            EstadoCasilla.AGUA
        }
    }
    
    // Calcula automáticamente cuántos aciertos se necesitan para ganar sumando la longitud de todos los barcos
    val impactosNecesarios = TipoBarco.values().sumOf({ it.longitud })

    // Variables de estado de la partida actual
    var aciertos = 0
    var misilesRestantes = MUNICION_MAXIMA
    var juegoTerminado = true

    // Mensaje de feedback para guiar al usuario
    var mensaje = "Pulsa iniciar para jugar"

    /**
     * Reinicia todas las variables y el tablero para comenzar una nueva partida.
     */
    fun iniciarPartida() {
        oceano = MutableList(DIMENSION) { MutableList(DIMENSION) { EstadoCasilla.AGUA } }
        colocarFlotaCompleta()
        aciertos = 0
        misilesRestantes = MUNICION_MAXIMA
        juegoTerminado = false
        mensaje = "Toca una casilla"
    }

    /**
     * Comprueba si un barco puede ser colocado en una posición específica sin salirse 
     * del tablero ni chocar/pegarse a otros barcos ya colocados.
     *
     * @param fila Coordenada vertical inicial donde se quiere colocar la proa del barco.
     * @param columna Coordenada horizontal inicial donde se quiere colocar la proa del barco.
     * @param longitud El tamaño del barco en número de casillas.
     * @param horizontal Booleano que indica si el barco se extiende hacia la derecha (true) o hacia abajo (false).
     * @return `true` si el espacio (y su perímetro) está libre y dentro de los límites, `false` en caso contrario.
     */
    fun esPosicionValida(fila:Int, columna:Int, longitud:Int, horizontal:Boolean): Boolean {
        // Calculamos el tamaño que ocupará visualmente
        val anchoBarco = if (horizontal) longitud else 1
        val altoBarco = if (horizontal) 1 else longitud

        // 1. Comprobamos que el barco entero cabe dentro de los límites del mapa
        if (fila + altoBarco > DIMENSION || columna + anchoBarco > DIMENSION) {
            return false
        }

        // 2. Comprobamos que no choca con otros barcos y dejamos 1 casilla de margen (perímetro de seguridad)
        // coerceAtLeast(0) evita que busquemos en índices negativos (-1) si el barco está en el borde superior o izquierdo
        val filaInicio = (fila - 1).coerceAtLeast(0)
        val columnaInicio = (columna - 1).coerceAtLeast(0)
        
        // coerceAtMost evita que busquemos fuera del mapa si el barco está pegado al borde inferior o derecho
        val filaFin = (fila + altoBarco).coerceAtMost(DIMENSION - 1)
        val columnaFin = (columna + anchoBarco).coerceAtMost(DIMENSION - 1)

        // Escaneamos el área calculada
        for (i in filaInicio..filaFin) {
            for (j in columnaInicio..columnaFin) {
                // Si encontramos algo distinto a AGUA, la posición es inválida
                if (oceano[i][j] != EstadoCasilla.AGUA) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Escribe físicamente el barco en la matriz cambiando el estado de las casillas afectadas a BARCO.
     *
     * @param fila Coordenada vertical inicial de colocación.
     * @param columna Coordenada horizontal inicial de colocación.
     * @param longitud El número de casillas que ocupa el barco.
     * @param horizontal Dirección de dibujo: `true` para extender a la derecha, `false` para extender hacia abajo.
     */
    fun colocarBarcoEnMatriz(fila:Int, columna:Int, longitud:Int, horizontal:Boolean) {
        for (i in 0 until longitud) {
            if (horizontal) {
                oceano[fila][columna + i] = EstadoCasilla.BARCO
            }
            else {
                oceano[fila + i][columna] = EstadoCasilla.BARCO
            }
        }
    }

    /**
     * Genera posiciones y orientaciones aleatorias en bucle hasta encontrar un hueco válido para un barco.
     *
     * @param barco El tipo de embarcación (enum) que se desea posicionar en el tablero.
     */
    fun colocarBarcoAleatorio(barco: TipoBarco) {
        var colocado = false
        // Bucle infinito que se detiene cuando logramos encajar el barco
        while(!colocado) {
            val fila = Random.nextInt(DIMENSION)
            val columna = Random.nextInt(DIMENSION)
            val horizontal = Random.nextBoolean()

            // Validamos las coordenadas generadas al azar
            if (esPosicionValida(fila, columna, barco.longitud, horizontal)) {
                // Si es válida, lo dibujamos en la matriz y salimos del bucle
                colocarBarcoEnMatriz(fila, columna, barco.longitud, horizontal)
                colocado = true
            }
        }
    }

    /**
     * Método auxiliar que recorre todos los tipos de barcos definidos y los coloca en el tablero.
     */
    fun colocarFlotaCompleta() {
        for (barco in TipoBarco.values()) {
            colocarBarcoAleatorio(barco)
        }
    }

    /**
     * Evalúa si se cumplen las condiciones de victoria (flota destruida) o derrota (sin munición).
     */
    fun comprobarFinDeJuego() {
        // Victoria: Si la cantidad de impactos acerta la suma total de casillas de barco
        if (aciertos == impactosNecesarios) {
            juegoTerminado = true
            mensaje = "¡ENHORABUENA! HAS GANADO"
        }
        // Derrota: Si se gasta el último misil y no has ganado todavía
        else if (misilesRestantes == 0) {
            juegoTerminado = true
            mensaje = "¡MUNICIÓN AGOTADA! HAS PERDIDO"
        }
    }

    /**
     * Procesa el intento de ataque del jugador en una casilla concreta.
     *
     * @param fila La coordenada vertical (Y) donde el jugador ha lanzado el misil.
     * @param columna La coordenada horizontal (X) donde el jugador ha lanzado el misil.
     */
    fun turno(fila:Int, columna:Int) {
        // Bloqueo de toques si el juego ha finalizado
        if (juegoTerminado) return

        val estado = oceano[fila][columna]

        // Validación para evitar gastar munición en casillas ya reveladas
        if (estado == EstadoCasilla.TOCADO || estado == EstadoCasilla.FALLO) {
            mensaje = "Ya has disparado ahí"
            return
        }

        // Se resta una unidad al inventario de misiles
        misilesRestantes--

        // Evaluamos si el disparo ha impactado un barco o agua
        if (estado == EstadoCasilla.BARCO) {
            oceano[fila][columna] = EstadoCasilla.TOCADO
            aciertos++
            mensaje = "¡IMPACTO CONFIRMADO!"
        }
        else {
            oceano[fila][columna] = EstadoCasilla.FALLO
            mensaje = "HAS FALLADO"
        }

        // Revisamos si este último movimiento termina la partida
        comprobarFinDeJuego()
    }
}