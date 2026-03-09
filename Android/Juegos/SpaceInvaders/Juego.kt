/**
 * ==============================================================================
 * MOTOR DEL JUEGO: INVASORES DEL ESPACIO
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo contiene la lógica principal del juego. Define las reglas, las
 * entidades que participan (nave, aliens, balas, powerups) y cómo interactúan
 * entre sí en un tablero bidimensional mediante un sistema de turnos.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Clases de datos: Definición de una clase `Entidad` para gestionar objetos.
 * 2. Colecciones mutables: Uso de `mutableListOf` y `mutableSetOf`.
 * 3. Funciones de orden superior: Uso de `any`, `filter`, `map`, `find`, `count`.
 * 4. Control de flujo: Uso de `when` para múltiples condiciones y bucles anidados.
 * ==============================================================================
 */
package com.example.myapplication

// Constantes globales que definen los parámetros fundamentales del juego
const val ANCHO = 11
const val ALTO = 16
const val ALIENS_POR_OLEADA = 3
const val MAX_VIDAS = 3
const val PROBABILIDAD_BOMBA = 0.5
const val PUNTOS_POR_ALIEN = 100

/**
 * Representa cualquier objeto físico dentro del tablero de juego.
 *
 * @property x Coordenada horizontal (columna).
 * @property y Coordenada vertical (fila).
 * @property tipo Cadena que define qué es esta entidad ("NAVE", "ALIEN", etc.).
 */
class Entidad(var x: Int, var y: Int, val tipo: String) {

    /**
     * Función (método) para mover este objeto concreto.
     * Recibe la acción del usuario ("IZQUIERDA", "FUEGO", etc.)
     */
    fun mover(accionUsuario:String) {
        // Usamos 'when' para decidir qué hacer según el tipo de objeto
        when (tipo){
            "NAVE" -> {
                // La nave obedece al usuario
                if (accionUsuario == "IZQUIERDA") x-- // Restar x es ir a la izquierda
                if (accionUsuario == "DERECHA") x++   // Sumar x es ir a la derecha

                // Límites: Impedimos que la nave se salga del mapa (ancho 0 a 11)
                if (x < 0) x = 0
                if (x > ANCHO - 1) x = ANCHO - 1
            }
            "ALIEN" -> {
                y++ // Sumar y es bajar (el eje Y crece hacia abajo en consola)
            }
            "BALA" -> {
                y-- // Restar y es subir
            }
            "POWERUP" -> {
                y++
            }
        }
    }
}

/**
 * Gestor principal del estado del juego.
 * Mantiene la lista de entidades, la puntuación, las vidas y ejecuta la lógica de cada turno.
 */
class Juego {
    // Lista principal que contiene todos los objetos activos en el tablero
    val entidades = mutableListOf<Entidad>()

    // Referencia directa a la nave del jugador
    val nave = Entidad(ANCHO / 2, ALTO - 1, "NAVE")

    // Variables de estado de la partida
    var puntos = 0
    var vidas = MAX_VIDAS
    var numeroOleada = 1
    var bombasMasivas = 0

    // Mensaje de feedback para el usuario
    var mensaje = "¡Bienvenido a Invasores del Espacio!"

    init {
        // Estado inicial al crear el juego
        entidades.add(nave)
        generarOleada()
    }

    /**
     * Convierte el tipo de entidad (String) en un emoji visual.
     */
    fun obtenerIcono(tipo: String): String {
        return when (tipo) {
            "NAVE" -> "🚨"
            "ALIEN" -> "👽"
            "BALA" -> "♦️"
            "POWERUP" -> "🧨"
            else -> "?"
        }
    }

    /**
     * Comprueba si dos entidades chocan en la misma casilla o se cruzan.
     */
    fun hayColision(entidad1: Entidad, entidad2: Entidad): Boolean {
        val mismaColumna = (entidad1.x == entidad2.x)
        val choqueDirecto = (entidad1.y == entidad2.y)
        val cruceEnElAire = (entidad1.y == entidad2.y + 1)

        return mismaColumna && (choqueDirecto || cruceEnElAire)
    }

    /**
     * Restablece todos los valores a su estado inicial.
     */
    fun reiniciar() {
        entidades.clear()
        nave.x = ANCHO / 2
        nave.y = ALTO - 1
        entidades.add(nave)

        puntos = 0
        vidas = MAX_VIDAS
        numeroOleada = 1
        bombasMasivas = 0

        generarOleada()

        mensaje = "¡Partida reiniciada!"
    }

    /**
     * Genera un nuevo grupo de enemigos en la parte superior del mapa.
     */
    fun generarOleada() {
        // Usamos un Set para asegurar que no se repitan las posiciones X
        val posicionesUnicas = mutableSetOf<Int>()

        while (posicionesUnicas.size < ALIENS_POR_OLEADA) {
            val xAleatoria = (Math.random() * ANCHO).toInt()
            posicionesUnicas.add(xAleatoria)
        }

        // Un bucle for para crear enemigos
        for (x in posicionesUnicas) {
            entidades.add(Entidad(x, 0, "ALIEN"))
        }
    }

    /**
     * Intenta generar un item de ayuda (POWERUP) aleatoriamente.
     */
    fun generarPowerup() {
        // Comprobamos si ya hay un powerup activo
        val hayPowerupEnPantalla = entidades.any({ it.tipo == "POWERUP"})

        if (!hayPowerupEnPantalla && Math.random() < PROBABILIDAD_BOMBA) {
            // Buscamos dónde hay aliens para no solapar el powerup
            val posicionesAliens = entidades.filter( { it.tipo == "ALIEN"} ).map({ it.x })

            var xAleatoria:Int
            // Buscamos una columna libre de aliens
            do {
                xAleatoria = (Math.random() * ANCHO).toInt()
            } while(xAleatoria in posicionesAliens)

            entidades.add(Entidad(xAleatoria, 0, "POWERUP"))
        }
    }

    /**
     * Genera una representación en formato texto de todo el tablero.
     */
    fun obtenerMapaComoTexto(): String {
        var mapaTexto = ""

        // Bucle anidado: Recorremos cada fila (y) y cada columna (x)
        for (y in 0 until ALTO) {
            for (x in 0 until ANCHO) {
                var simbolo = "▫️" // Por defecto dibujamos un cuadrado blanco pequeño (espacio vacío)

                // Buscamos: ¿Hay alguna entidad en esta coordenada (x, y)?
                // 'find' devuelve el objeto si lo encuentra, o null si no hay nada.
                val entidadAqui = entidades.find({ it.x == x && it.y == y })

                // Si encontramos algo (no es null)...
                if (entidadAqui != null) {
                    // Llamamos a la función correspondiente para saber qué dibujo poner (A, V, |)
                    simbolo = obtenerIcono(entidadAqui.tipo)
                }

                // Imprimimos el símbolo sin salto de línea
                mapaTexto += "$simbolo"
            }
            mapaTexto += "\n" // Al terminar la fila, hacemos un salto de línea
        }

        return mapaTexto;
    }

    /**
     * Función central que procesa la lógica de un turno del juego.
     *
     * @param accion La orden enviada por el usuario o el sistema.
     */
    fun turno(accion:String) {
        // Bloqueo de acciones si el jugador está sin vidas
        if (vidas <= 0) return

        mensaje = ""
        generarPowerup()

        // Procesamiento de acciones del jugador
        if (accion == "FUEGO") {
            entidades.add(Entidad(nave.x, nave.y, "BALA"))
        }
        else if (accion == "BOMBA") {
            if (bombasMasivas > 0) {
                bombasMasivas--
                mensaje = "¡BOMBA MASIVA ACTIVADA! Aniquilación total."

                // Destruimos todos los aliens de golpe
                val todosLosAliens = entidades.filter( { it.tipo == "ALIEN" })
                puntos += (todosLosAliens.size * PUNTOS_POR_ALIEN)
                entidades.removeAll(todosLosAliens)
            }
            else {
                mensaje = "No tienes bombas. Atrapa todas las que puedas para poder lanzarlas."
            }
        }

        // Movimiento general de todas las entidades
        for (entidad in entidades) {
            entidad.mover(accion)
        }

        // Lista temporal para guardar las entidades que deben ser eliminadas (balas que chocan, aliens que llegan al final, etc.)
        val paraBorrar = mutableListOf<Entidad>()

        var invasionEnEsteTurno = false

        // Verificamos colisiones y condiciones de fin de juego
        for (entidad in entidades) {
            // Limpieza: si salen del mapa por arriba o por abajo
            if (entidad.y < 0 || entidad.y >= ALTO) {
                if (entidad.tipo != "NAVE") paraBorrar.add(entidad)
            }

            // Verificamos si un alien ha llegado a la última fila (donde está la nave del jugador)
            if (entidad.tipo == "ALIEN") {
                if (entidad.y >= ALTO - 1) {
                    paraBorrar.add(entidad)
                    invasionEnEsteTurno = true
                }
                else {
                    // Verificamos si una bala ha chocado con un alien
                    for (otra in entidades) {
                        if (entidad.tipo == "ALIEN" && otra.tipo == "BALA") {
                            if (hayColision(entidad, otra)) {
                                paraBorrar.add(entidad)
                                paraBorrar.add(otra)
                                puntos += PUNTOS_POR_ALIEN
                                mensaje = "¡BOOM! Alien eliminado."
                            }
                        }
                    }
                }
            }

            // Verificamos si la nave ha recogido un powerup
            if (entidad.tipo == "POWERUP") {
                if (hayColision(entidad, nave)) {
                    paraBorrar.add(entidad)
                    bombasMasivas++
                    mensaje = "¡POWERUP! Tienes $bombasMasivas bombas masivas"
                }
            }
        }

        // Consecuencias de la invasión alienígena
        if (invasionEnEsteTurno) {
            vidas--
            mensaje = "¡Cuidado! Los aliens han invadido la base. Pierdes 1 vida."

            if (vidas <= 0) {
                mensaje = "¡GAME OVER! Has perdido todas tus vidas. Pulsa REINICIAR"
            }
        }

        // Eliminamos todas las entidades que deben ser borradas (aliens eliminados, balas que han salido del mapa, etc.)
        entidades.removeAll(paraBorrar)

        // Contamos cuántos aliens quedan en la lista
        val alienRestantes = entidades.count({ it.tipo == "ALIEN" })

        // Si no quedan aliens, el jugador gana la ronda y avanza
        if (alienRestantes == 0) {
            mensaje = "¡OLEADA $numeroOleada COMPLETADA!"
            numeroOleada++
            generarOleada()
        }
    }
}