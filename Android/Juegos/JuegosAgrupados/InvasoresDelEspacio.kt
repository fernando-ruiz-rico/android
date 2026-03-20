/**
 * ==============================================================================
 * MOTOR DE JUEGO: INVASORES DEL ESPACIO
 * ==============================================================================
 * Objetivo del programa:
 * Un simulador de "Space Invaders" basado en cuadrícula. En lugar de físicas 
 * fluidas continuas, usa un sistema discreto basado en fotogramas (Ticks).
 * Los elementos (aliens, nave, balas) interactúan moviéndose por coordenadas X,Y
 * y desaparecen al colisionar.
 *
 * Qué aprenderás de Kotlin y programación con este código:
 * 1. Arquitectura de Entidades (Entity Component System simple): Cómo tratar 
 * a todos los actores del juego (balas, enemigos, jugador) como un solo tipo 
 * de objeto (`Entidad`) que se guarda en una misma lista `entidades`.
 * 2. Colecciones dinámicas: Uso de funciones lambda para buscar (`.find`),
 * filtrar (`.filter`), mapear (`.map`), contar (`.count`) y limpiar elementos 
 * antiguos (`.removeAll`) en listas dinámicas.
 * 3. Detección de colisiones (Hitboxes lógicos): Cálculo para detectar si
 * un objeto cruza o se superpone a otro en una matriz simulada.
 * ==============================================================================
 */
package com.example.myapplication

/**
 * Motor central de lógica para el shooter de aliens.
 */
class JuegoInvasores {
    companion object {
        const val ANCHO = 11
        const val ALTO = 16
        const val ALIENS_POR_OLEADA = 3
        const val MAX_VIDAS = 3
        const val PROBABILIDAD_BOMBA = 0.5
        const val PUNTOS_POR_ALIEN = 100
        const val INTERVALO_MOVIMIENTO = 1000L // Velocidad a la que caen (1 segundo)
    }

    /**
     * Representa cualquier objeto vivo dentro del mapa.
     * @property x Coordenada horizontal (columna).
     * @property y Coordenada vertical (fila).
     * @property tipo Clasificación del objeto para aplicar lógicas diferentes (ej: "BALA", "ALIEN").
     */
    class Entidad(var x: Int, var y: Int, val tipo: String) {
        
        /**
         * Lógica de movimiento individual dependiendo del tipo de objeto y la acción enviada.
         */
        fun mover(accionUsuario:String) {
            when (tipo){
                "NAVE" -> {
                    // Controles manuales del jugador con restricción a los bordes
                    if (accionUsuario == "IZQUIERDA") x--
                    if (accionUsuario == "DERECHA") x++
                    if (x < 0) x = 0
                    if (x > ANCHO - 1) x = ANCHO - 1
                }
                // Movimientos autónomos que reaccionan al "pulso" del juego
                "ALIEN" -> if (accionUsuario == "TICK") y++
                "BALA" -> if (accionUsuario == "TICK") y--
                "POWERUP" -> if (accionUsuario == "TICK") y++
            }
        }
    }

    // Lista global que maneja TODOS los objetos renderizados y simulados simultáneamente
    val entidades = mutableListOf<Entidad>()
    val nave = Entidad(ANCHO / 2, ALTO - 1, "NAVE")

    var puntos = 0
    var vidas = MAX_VIDAS
    var numeroOleada = 1
    var bombasMasivas = 0
    var mensaje = "¡Bienvenido a Invasores del Espacio!"

    init {
        entidades.add(nave)
        generarOleada()
    }

    /**
     * Mapea el tipo de entidad lógica a un símbolo gráfico visible en UI.
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
     * Comprueba si dos entidades ocupan el mismo espacio físico real o simulado (se cruzaron).
     */
    fun hayColision(entidad1: Entidad, entidad2: Entidad): Boolean {
        val mismaColumna = (entidad1.x == entidad2.x)
        val choqueDirecto = (entidad1.y == entidad2.y)
        // Evita que la bala "atraviese" al alien al moverse a la vez en direcciones contrarias
        val cruceEnElAire = (entidad1.y == entidad2.y + 1) 
        
        return mismaColumna && (choqueDirecto || cruceEnElAire)
    }

    /**
     * Resetea completamente el mapa y las variables de juego.
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
     * Spawnea enemigos en la parte superior del tablero usando un Set (conjunto)
     * para asegurar que dos aliens no salgan jamás desde la misma coordenada.
     */
    fun generarOleada() {
        val posicionesUnicas = mutableSetOf<Int>()
        while (posicionesUnicas.size < ALIENS_POR_OLEADA) {
            posicionesUnicas.add((Math.random() * ANCHO).toInt())
        }
        for (x in posicionesUnicas) entidades.add(Entidad(x, 0, "ALIEN"))
    }

    /**
     * Da una probabilidad a generar un premio en el cielo asegurándose 
     * de no pisar un Alien existente usando map y contains (in).
     */
    fun generarPowerup() {
        val hayPowerupEnPantalla = entidades.any({ it.tipo == "POWERUP"})
        if (!hayPowerupEnPantalla && Math.random() < PROBABILIDAD_BOMBA) {
            val posicionesAliens = entidades.filter( { it.tipo == "ALIEN"} ).map({ it.x })
            var xAleatoria:Int
            do {
                xAleatoria = (Math.random() * ANCHO).toInt()
            } while(xAleatoria in posicionesAliens)
            entidades.add(Entidad(xAleatoria, 0, "POWERUP"))
        }
    }

    /**
     * Renderiza todo el motor lógico a un formato de Texto (String) listo 
     * para ser dibujado en pantalla como arte ASCII / Emoji.
     */
    fun obtenerMapaComoTexto(): String {
        var mapaTexto = "\n"
        for (y in 0 until ALTO) {
            for (x in 0 until ANCHO) {
                var simbolo = "▫️" // Vacío por defecto
                val entidadAqui = entidades.find({ it.x == x && it.y == y })
                if (entidadAqui != null) simbolo = obtenerIcono(entidadAqui.tipo)
                mapaTexto += "$simbolo"
            }
            mapaTexto += "\n"
        }
        return mapaTexto;
    }

    /**
     * Procesa un paso o acción completa (sea de usuario o del sistema TICK).
     */
    fun turno(accion:String) {
        if (vidas <= 0) return

        // TICK es el "latido" del juego: Generado por un temporizador (timer)
        if (accion == "TICK") {
            mensaje = ""
            generarPowerup()
        }

        // Acciones del jugador
        if (accion == "FUEGO") entidades.add(Entidad(nave.x, nave.y, "BALA"))
        else if (accion == "BOMBA") {
            // Lógica de bomba especial que elimina toda la oleada
            if (bombasMasivas > 0) {
                bombasMasivas--
                mensaje = "¡BOMBA MASIVA ACTIVADA! Aniquilación total."
                val todosLosAliens = entidades.filter( { it.tipo == "ALIEN" })
                puntos += (todosLosAliens.size * PUNTOS_POR_ALIEN)
                entidades.removeAll(todosLosAliens) // Borra en bloque
            } else {
                mensaje = "No tienes bombas. Atrapa todas las que puedas."
            }
        }

        // 1. Movemos todas las entidades (Balas arriba, aliens abajo, nave lados)
        for (entidad in entidades) entidad.mover(accion)

        // Lista de cosas a destruir después de este turno para evitar ConcurrentModificationException
        val paraBorrar = mutableListOf<Entidad>()
        var invasionEnEsteTurno = false

        // 2. Comprobación de colisiones de las entidades movidas
        for (entidad in entidades) {
            
            // Si cualquier cosa sale por arriba o por abajo (excepto nuestra nave), la borramos para liberar memoria
            if (entidad.y < 0 || entidad.y >= ALTO) {
                if (entidad.tipo != "NAVE") paraBorrar.add(entidad)
            }
            
            if (entidad.tipo == "ALIEN") {
                // El alien tocó el borde inferior de la pantalla = invasión = pierdes vida
                if (entidad.y >= ALTO - 1) {
                    paraBorrar.add(entidad)
                    invasionEnEsteTurno = true
                } else {
                    // Buscar choque contra balas usando un bucle anidado
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
            if (entidad.tipo == "POWERUP") {
                // Choque de jugador con powerup
                if (hayColision(entidad, nave)) {
                    paraBorrar.add(entidad)
                    bombasMasivas++
                    mensaje = "¡POWERUP! Tienes $bombasMasivas bombas masivas"
                }
            }
        }

        // Aplicamos daño
        if (invasionEnEsteTurno) {
            vidas--
            mensaje = "¡Cuidado! Los aliens han invadido la base. Pierdes 1 vida."
            if (vidas <= 0) mensaje = "¡GAME OVER! Has perdido todas tus vidas."
        }

        // 3. Ejecutar Limpieza (Garbage Collection Manual)
        entidades.removeAll(paraBorrar)
        
        // Comprobar si hay que generar nueva oleada
        val alienRestantes = entidades.count({ it.tipo == "ALIEN" })

        if (alienRestantes == 0) {
            mensaje = "¡OLEADA $numeroOleada COMPLETADA!"
            numeroOleada++
            generarOleada()
        }
    }
}