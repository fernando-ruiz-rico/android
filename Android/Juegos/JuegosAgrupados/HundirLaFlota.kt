/**
 * ==============================================================================
 * MOTOR DE JUEGO: HUNDIR LA FLOTA (Batalla Naval)
 * ==============================================================================
 * Objetivo del programa:
 * Este motor crea y gestiona un tablero de agua y barcos ocultos. Se encarga
 * de generar aleatoriamente la posición de los barcos asegurando que no se 
 * pisen entre sí ni se salgan del tablero. También controla los impactos y
 * la munición restante del jugador.
 *
 * Qué aprenderás de Kotlin y programación con este código:
 * 1. Generación procedimental controlada: Cómo usar bucles `while` con 
 * verificaciones cruzadas para asegurar un posicionamiento aleatorio válido.
 * 2. Límites de matrices (Bounds checking): Uso de funciones `.coerceAtLeast` 
 * y `.coerceAtMost` para evitar el temido error "IndexOutOfBoundsException".
 * 3. Cálculos funcionales en Colecciones: Uso de `.sumOf` sobre los valores 
 * de un Enum para calcular dinámicamente cuántos aciertos totales se requieren.
 * ==============================================================================
 */
package com.example.myapplication

import kotlin.random.Random

/**
 * Gestor del océano, los barcos y los disparos del usuario.
 */
class JuegoHundirFlota {
    /**
     * Tipos de navíos disponibles con su correspondiente tamaño ocupado en la matriz.
     */
    enum class TipoBarco(val longitud: Int) {
        PORTAAVIONES(5),
        ACORAZADO(4),
        CRUCERO(3),
        SUBMARINO(3),
        DESTRUCTOR(2)
    }

    /**
     * Estados posibles de cada cuadrícula del océano de 10x10.
     */
    enum class EstadoCasilla(val simbolo:String) {
        AGUA("🟦"),
        BARCO("🚢"), // Barco oculto
        TOCADO("💥"), // Barco acertado
        FALLO("⚪");   // Agua disparada
        override fun toString(): String = simbolo
    }

    companion object {
        const val DIMENSION = 10
        const val MUNICION_MAXIMA = 50
    }

    // El océano donde ocurre todo: Matriz 10x10 de EstadoCasilla.
    var oceano = MutableList(DIMENSION) { MutableList(DIMENSION) { EstadoCasilla.AGUA } }
    
    // Calcula el total de impactos necesarios sumando las longitudes de todos los barcos
    val impactosNecesarios = TipoBarco.values().sumOf({ it.longitud })

    // Estado de la partida actual
    var aciertos = 0
    var misilesRestantes = MUNICION_MAXIMA
    var juegoTerminado = true
    var mensaje = "Pulsa iniciar para jugar"

    /**
     * Resetea el tablero y recoloca toda la flota al azar.
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
     * Verifica si es matemáticamente posible colocar un barco en unas coordenadas
     * sin que se salga del mapa ni pise otro barco ya existente.
     *
     * @param fila Coordenada vertical inicial.
     * @param columna Coordenada horizontal inicial.
     * @param longitud Tamaño del barco.
     * @param horizontal Orientación (true = Derecha, false = Abajo).
     * @return `true` si el espacio (incluyendo un margen) está libre.
     */
    fun esPosicionValida(fila:Int, columna:Int, longitud:Int, horizontal:Boolean): Boolean {
        // 1. Verificación de límites de la matriz (Out of bounds)
        val anchoBarco = if (horizontal) longitud else 1
        val altoBarco = if (horizontal) 1 else longitud

        if (fila + altoBarco > DIMENSION || columna + anchoBarco > DIMENSION) return false

        // 2. Verificación de colisión (Se mira una casilla más alrededor para que no se toquen)
        // coerceAtLeast(0) evita mirar filas negativas.
        // coerceAtMost(...) evita mirar más allá del borde de la matriz.
        val filaInicio = (fila - 1).coerceAtLeast(0)
        val columnaInicio = (columna - 1).coerceAtLeast(0)
        val filaFin = (fila + altoBarco).coerceAtMost(DIMENSION - 1)
        val columnaFin = (columna + anchoBarco).coerceAtMost(DIMENSION - 1)

        for (i in filaInicio..filaFin) {
            for (j in columnaInicio..columnaFin) {
                // Si encontramos un barco cerca, la posición es inválida
                if (oceano[i][j] != EstadoCasilla.AGUA) return false
            }
        }
        return true
    }

    /**
     * Dibuja los datos del barco directamente en la matriz `oceano`.
     */
    fun colocarBarcoEnMatriz(fila:Int, columna:Int, longitud:Int, horizontal:Boolean) {
        for (i in 0 until longitud) {
            if (horizontal) oceano[fila][columna + i] = EstadoCasilla.BARCO
            else oceano[fila + i][columna] = EstadoCasilla.BARCO
        }
    }

    /**
     * Busca obstinadamente (con un bucle while) una posición válida y aleatoria
     * para ubicar el barco pasado por parámetro.
     */
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

    /**
     * Llama al colocador aleatorio para todos los tipos de barcos definidos.
     */
    fun colocarFlotaCompleta() {
        for (barco in TipoBarco.values()) colocarBarcoAleatorio(barco)
    }

    /**
     * Evalúa las condiciones de victoria o derrota.
     */
    fun comprobarFinDeJuego() {
        if (aciertos == impactosNecesarios) {
            juegoTerminado = true
            mensaje = "¡ENHORABUENA! HAS GANADO"
        } else if (misilesRestantes == 0) {
            juegoTerminado = true
            mensaje = "¡MUNICIÓN AGOTADA! HAS PERDIDO"
        }
    }

    /**
     * Procesa un disparo del jugador en las coordenadas dadas.
     */
    fun turno(fila:Int, columna:Int) {
        if (juegoTerminado) return
        val estado = oceano[fila][columna]

        // Evita perder misiles pulsando dos veces el mismo sitio
        if (estado == EstadoCasilla.TOCADO || estado == EstadoCasilla.FALLO) {
            mensaje = "Ya has disparado ahí"
            return
        }

        misilesRestantes--

        // Evalúa el resultado del impacto
        if (estado == EstadoCasilla.BARCO) {
            oceano[fila][columna] = EstadoCasilla.TOCADO
            aciertos++
            mensaje = "¡IMPACTO CONFIRMADO!"
        } else {
            oceano[fila][columna] = EstadoCasilla.FALLO
            mensaje = "HAS FALLADO"
        }
        
        // Verifica si la partida ha acabado tras este disparo
        comprobarFinDeJuego()
    }
}