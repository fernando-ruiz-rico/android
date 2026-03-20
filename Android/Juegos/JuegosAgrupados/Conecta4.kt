/**
 * ==============================================================================
 * MOTOR DE JUEGO: CONECTA 4
 * ==============================================================================
 * Objetivo del programa:
 * Este motor gestiona las reglas lógicas del clásico juego Conecta 4. 
 * Permite partidas contra un jugador controlado por la máquina con varios niveles
 * de dificultad, evaluando cuándo una columna está llena y comprobando todas 
 * las posibles combinaciones de victoria (horizontal, vertical y diagonal).
 *
 * Qué aprenderás de Kotlin y programación con este código:
 * 1. Matrices Bidimensionales (Listas de Listas): Uso de `MutableList` anidadas
 * para representar un tablero (grid) de filas y columnas.
 * 2. Algoritmos de Búsqueda de Patrones: Bucles anidados complejos para comprobar 
 * victorias en 4 direcciones diferentes.
 * 3. Inteligencia Artificial Básica: Lógica condicional para priorizar ganar, 
 * bloquear al rival o mover al azar según el nivel de dificultad.
 * 4. Enumeraciones con valores: Uso de `enum class` asociando texto (símbolos)
 * a cada estado posible.
 * ==============================================================================
 */
package com.example.myapplication

import kotlin.random.Random

/**
 * Gestor principal de las lógicas, tablero y turnos de Conecta 4.
 */
class JuegoConecta4 {
    /**
     * Define los niveles de inteligencia del oponente controlado por la CPU.
     * @property descripcion Texto amigable para mostrar en la interfaz.
     */
    enum class Dificultad(val descripcion: String) {
        FACIL("Fácil (Aleatorio)"),
        MEDIO("Medio (Defensivo)"),
        DIFICIL("Difícil (Inteligente)");
        override fun toString() : String = descripcion
    }

    /**
     * Representa el contenido de una celda del tablero.
     * @property simbolo Emoji o caracteres que se dibujarán en pantalla.
     */
    enum class Ficha(val simbolo: String) {
        VACIO("  "),
        JUGADOR("🔴"),
        MAQUINA("🟡");
        override fun toString() : String = simbolo
    }

    // Constantes globales de configuración del juego
    companion object {
        const val FILAS = 6
        const val COLUMNAS = 7
        const val NO_ENCONTRADO = -1 // Código de error o ausencia de valor
    }

    // --- ESTADO DEL JUEGO ---
    // Tablero inicializado dinámicamente: una lista de 6 filas, cada una con 7 celdas vacías.
    var tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }
    var dificultadSeleccionada : Dificultad? = null
    var juegoTerminado = false
    var mensaje = "Seleccione dificultad"

    /**
     * Resetea el tablero y prepara todo para una nueva partida.
     */
    fun iniciarPartida(dificultad: Dificultad) {
        tablero = MutableList(FILAS) { MutableList(COLUMNAS) { Ficha.VACIO } }
        dificultadSeleccionada = dificultad
        juegoTerminado = false
        mensaje = "Tu turno (elige columna)"
    }

    /**
     * Comprueba si aún se pueden meter fichas en una columna específica.
     * @param c Índice de la columna a comprobar.
     * @return `true` si es válida y tiene hueco en la fila superior (0).
     */
    fun columnaValida(c: Int): Boolean {
        // La fila 0 es la parte más alta del tablero
        return c >= 0 && c < COLUMNAS && tablero[0][c] == Ficha.VACIO
    }

    /**
     * Busca la fila más baja disponible ("gravedad") en una columna.
     * @param c Columna donde se quiere tirar la ficha.
     * @return El índice de la fila libre o NO_ENCONTRADO si está llena.
     */
    fun obtenerFilaLibre(c:Int): Int {
        var filaEncontrada = NO_ENCONTRADO
        // Recorremos de abajo (FILAS-1) hacia arriba (0)
        for (f in FILAS - 1 downTo 0) {
            if (filaEncontrada == NO_ENCONTRADO && tablero[f][c] == Ficha.VACIO) {
                filaEncontrada = f
            }
        }
        return filaEncontrada
    }

    /**
     * Deposita una ficha en la columna indicada aplicando "gravedad".
     */
    fun colocarFicha(c:Int, ficha:Ficha) {
        val f = obtenerFilaLibre(c)
        if (f != NO_ENCONTRADO) {
            tablero[f][c] = ficha
        }
    }

    /**
     * Escanea todo el tablero buscando 4 fichas iguales seguidas.
     * @param ficha El jugador (rojo o amarillo) que queremos comprobar.
     * @return `true` si hay 4 en raya en cualquier dirección.
     */
    fun comprobarVictoria(ficha: Ficha): Boolean {
        // 1. Comprobación HORIZONTAL (-)
        for (f in 0 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha && tablero[f][c+1] == ficha &&
                    tablero[f][c+2] == ficha && tablero[f][c+3] == ficha) return true
            }
        }
        // 2. Comprobación VERTICAL (|)
        for (f in 0 until FILAS - 3) {
            for (c in 0 until COLUMNAS) {
                if (tablero[f][c] == ficha && tablero[f+1][c] == ficha &&
                    tablero[f+2][c] == ficha && tablero[f+3][c] == ficha) return true
            }
        }
        // 3. Comprobación DIAGONAL ASCENDENTE (/)
        for (f in 3 until FILAS) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha && tablero[f-1][c+1] == ficha &&
                    tablero[f-2][c+2] == ficha && tablero[f-3][c+3] == ficha) return true
            }
        }
        // 4. Comprobación DIAGONAL DESCENDENTE (\)
        for (f in 0 until FILAS - 3) {
            for (c in 0 until COLUMNAS - 3) {
                if (tablero[f][c] == ficha && tablero[f+1][c+1] == ficha &&
                    tablero[f+2][c+2] == ficha && tablero[f+3][c+3] == ficha) return true
            }
        }
        return false
    }

    /**
     * IA: Simula movimientos para ver si tirar en una columna da la victoria.
     * @param ficha Ficha que se va a simular (IA para ganar o Jugador para bloquear).
     */
    fun buscarMovimientoGanador(ficha: Ficha): Int {
        var columnaGanadora = NO_ENCONTRADO
        for (c in 0 until COLUMNAS) {
            if (columnaGanadora == NO_ENCONTRADO && columnaValida(c)) {
                val f = obtenerFilaLibre(c)
                tablero[f][c] = ficha // Colocamos la ficha imaginaria
                if (comprobarVictoria(ficha)) columnaGanadora = c
                tablero[f][c] = Ficha.VACIO // Limpiamos la prueba
            }
        }
        return columnaGanadora
    }

    /**
     * Decide y ejecuta la jugada del ordenador según la dificultad.
     */
    fun hacerMovimientoMaquina(dificultad: Dificultad?) {
        var columnaElegida = NO_ENCONTRADO
        
        // Nivel DIFÍCIL: Prioriza ganar inmediatamente si es posible
        if (dificultad == Dificultad.DIFICIL) columnaElegida = buscarMovimientoGanador(Ficha.MAQUINA)
        
        // Nivel MEDIO/DIFÍCIL: Si no puede ganar, intenta bloquear al jugador
        if (columnaElegida == NO_ENCONTRADO) {
            if (dificultad == Dificultad.DIFICIL || dificultad == Dificultad.MEDIO) {
                columnaElegida = buscarMovimientoGanador(Ficha.JUGADOR)
            }
        }
        
        // Nivel FÁCIL o sin jugada clara: Elige una columna aleatoria válida
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

    /**
     * Gestiona el flujo de un turno completo: jugador -> comprobación -> máquina -> comprobación.
     */
    fun turno(columna: Int) {
        if (juegoTerminado || dificultadSeleccionada == null) return
        
        if (columnaValida(columna)) {
            // Turno Humano
            colocarFicha(columna, Ficha.JUGADOR)
            if (comprobarVictoria(Ficha.JUGADOR)) {
                mensaje = "¡Enhorabuena, has ganado!"
                juegoTerminado = true
                return
            }
            
            // Turno Máquina
            hacerMovimientoMaquina(dificultadSeleccionada)
            if (comprobarVictoria(Ficha.MAQUINA)) {
                mensaje = "Ha ganado el ordenador"
                juegoTerminado = true
            }
        }
    }
}