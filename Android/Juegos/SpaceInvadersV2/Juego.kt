/**
 * ==============================================================================
 * MOTOR DEL JUEGO: INVASORES DEL ESPACIO (VERSIÓN CON CORRUTINAS)
 * ==============================================================================
 * Objetivo del programa:
 * Este archivo contiene toda la lógica de negocio (el "cerebro") del juego.
 * Gestiona las posiciones de las entidades, las colisiones, la generación de oleadas,
 * y cómo reacciona el mundo al paso del tiempo (TICK) o a las acciones del usuario.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Clases y Objetos: Definición de una clase `Entidad` que encapsula estado y comportamiento.
 * 2. Colecciones: Uso intensivo de `mutableListOf` y funciones de orden superior
 * como `filter`, `any`, `count` y `removeAll`.
 * 3. Estructuras de control: Uso de `when` para simplificar condicionales múltiples.
 * 4. Separación de responsabilidades: El motor de juego es independiente de cómo
 * se dibuja en la pantalla (eso es tarea de MainActivity.kt).
 * ==============================================================================
 */

package com.example.myapplication

// Constantes globales del juego que definen las reglas y dimensiones
const val ANCHO = 11
const val ALTO = 16
const val ALIENS_POR_OLEADA = 3
const val MAX_VIDAS = 3
const val PROBABILIDAD_BOMBA = 0.5
const val PUNTOS_POR_ALIEN = 100
const val INTERVALO_MOVIMIENTO = 1000L // Constante para el tiempo en milisegundos (1 segundo)

/**
 * Clase que representa cualquier objeto físico dentro del tablero de juego.
 * Puede ser la nave del jugador, un alien, una bala o un powerup.
 *
 * @property x Coordenada horizontal (columna).
 * @property y Coordenada vertical (fila).
 * @property tipo Cadena de texto que define qué es esta entidad ("NAVE", "ALIEN", etc.).
 */
class Entidad(var x: Int, var y: Int, val tipo: String) {

    /**
     * Función (método) para mover este objeto concreto según la acción recibida.
     *
     * @param accionUsuario La orden que dicta el movimiento ("IZQUIERDA", "DERECHA", o "TICK" del reloj).
     */
    fun mover(accionUsuario:String) {
        // Usamos 'when' para decidir qué hacer según el tipo de objeto
        when (tipo){
            "NAVE" -> {
                // La nave obedece instantáneamente al usuario
                if (accionUsuario == "IZQUIERDA") x-- // Restar x es ir a la izquierda
                if (accionUsuario == "DERECHA") x++   // Sumar x es ir a la derecha

                // Límites: Impedimos que la nave se salga del mapa (ancho 0 a 11)
                if (x < 0) x = 0
                if (x > ANCHO - 1) x = ANCHO - 1
            }
            "ALIEN" -> {
                // Los aliens solo se mueven hacia abajo cuando pasa el tiempo (TICK)
                if (accionUsuario == "TICK") y++ // Sumar y es bajar
            }
            "BALA" -> {
                // Las balas solo se mueven hacia arriba cuando pasa el tiempo (TICK)
                if (accionUsuario == "TICK") y-- // Restar y es subir
            }
            "POWERUP" -> {
                // Los powerups caen hacia abajo por gravedad en cada TICK
                if (accionUsuario == "TICK") y++
            }
        }
    }
}

/**
 * Gestor principal del estado del juego.
 * Mantiene la lista de entidades, la puntuación, las vidas y ejecuta la lógica de cada turno.
 */
class Juego {
    // Lista principal que contiene todos los objetos que están actualmente en el mapa
    val entidades = mutableListOf<Entidad>()

    // Referencia directa a la nave del jugador para facilitar su control
    val nave = Entidad(ANCHO / 2, ALTO - 1, "NAVE")

    // Variables de estado de la partida
    var puntos = 0
    var vidas = MAX_VIDAS
    var numeroOleada = 1
    var bombasMasivas = 0

    // Mensaje de feedback que se mostrará en pantalla al usuario
    var mensaje = "¡Bienvenido a Invasores del Espacio!"

    init {
        // Al crear el juego, metemos la nave en la lista y generamos los primeros enemigos
        entidades.add(nave)
        generarOleada()
    }

    /**
     * Convierte el tipo de entidad (String) en un emoji visual para mostrar en pantalla.
     *
     * @param tipo El tipo de entidad.
     * @return Un String con el emoji correspondiente.
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
     * Comprueba si dos entidades se están superponiendo o se han cruzado en el mismo turno.
     *
     * @param entidad1 Primera entidad a comprobar.
     * @param entidad2 Segunda entidad a comprobar.
     * @return `true` si hay colisión, `false` en caso contrario.
     */
    fun hayColision(entidad1: Entidad, entidad2: Entidad): Boolean {
        val mismaColumna = (entidad1.x == entidad2.x)
        val choqueDirecto = (entidad1.y == entidad2.y)
        val cruceEnElAire = (entidad1.y == entidad2.y + 1)

        return mismaColumna && (choqueDirecto || cruceEnElAire)
    }

    /**
     * Restablece todos los valores a su estado inicial para comenzar una nueva partida.
     */
    fun reiniciar() {
        // Limpiamos la lista y volvemos a colocar la nave en su posición inicial
        entidades.clear()
        nave.x = ANCHO / 2
        nave.y = ALTO - 1
        entidades.add(nave)

        // Reseteamos contadores
        puntos = 0
        vidas = MAX_VIDAS
        numeroOleada = 1
        bombasMasivas = 0

        generarOleada()

        mensaje = "¡Partida reiniciada!"
    }

    /**
     * Genera un nuevo grupo de enemigos en la parte superior del mapa.
     * Asegura que no se generen dos aliens en la misma columna usando un Set.
     */
    fun generarOleada() {
        val posicionesUnicas = mutableSetOf<Int>()

        // Buscamos coordenadas X aleatorias hasta tener las necesarias para la oleada
        while (posicionesUnicas.size < ALIENS_POR_OLEADA) {
            val xAleatoria = (Math.random() * ANCHO).toInt()
            posicionesUnicas.add(xAleatoria)
        }

        // Un bucle for para crear enemigos y añadirlos a la lista de entidades
        for (x in posicionesUnicas) {
            entidades.add(Entidad(x, 0, "ALIEN"))
        }
    }

    /**
     * Intenta generar un item de ayuda (POWERUP) en la parte superior de la pantalla.
     * Solo se genera si hay suerte (probabilidad) y si no hay ya otro en pantalla.
     */
    fun generarPowerup() {
        // Comprobamos si ya existe algún powerup usando la función de orden superior 'any'
        val hayPowerupEnPantalla = entidades.any({ it.tipo == "POWERUP"})

        if (!hayPowerupEnPantalla && Math.random() < PROBABILIDAD_BOMBA) {
            // Obtenemos las columnas donde hay aliens para no poner el powerup encima de ellos
            val posicionesAliens = entidades.filter( { it.tipo == "ALIEN"} ).map({ it.x })

            var xAleatoria:Int
            // Bucle do-while: generamos coordenadas X hasta que encontremos una que esté libre de aliens
            do {
                xAleatoria = (Math.random() * ANCHO).toInt()
            } while(xAleatoria in posicionesAliens)

            entidades.add(Entidad(xAleatoria, 0, "POWERUP"))
        }
    }

    /**
     * Genera una representación en formato texto de todo el tablero.
     * Recorre cada cuadrícula comprobando si hay una entidad ahí.
     *
     * @return Un bloque de texto multilínea listo para ser mostrado en un componente Text de Compose.
     */
    fun obtenerMapaComoTexto(): String {
        var mapaTexto = ""

        // Bucle anidado: Recorremos cada fila (y) y cada columna (x) de arriba a abajo, izquierda a derecha
        for (y in 0 until ALTO) {
            for (x in 0 until ANCHO) {
                var simbolo = ". " // Por defecto dibujamos un punto (espacio vacío)

                // Buscamos: ¿Hay alguna entidad en esta coordenada (x, y)?
                // 'find' devuelve el objeto si lo encuentra, o null si no hay nada.
                val entidadAqui = entidades.find({ it.x == x && it.y == y })

                // Si encontramos algo (no es null)...
                if (entidadAqui != null) {
                    // Llamamos a la función correspondiente para saber qué dibujo poner
                    simbolo = obtenerIcono(entidadAqui.tipo)
                }

                // Imprimimos el símbolo sin salto de línea
                mapaTexto += "$simbolo"
            }
            mapaTexto += "\n" // Al terminar la fila completa, hacemos un salto de línea
        }

        return mapaTexto;
    }

    /**
     * Función central que procesa la lógica de un "instante" del juego.
     * Resuelve movimientos, colisiones, daños y condiciones de victoria/derrota.
     *
     * @param accion La orden que desencadena este turno ("TICK", "FUEGO", "IZQUIERDA", etc.).
     */
    fun turno(accion:String) {
        // Si el jugador está muerto, el motor de juego ignora cualquier acción
        if (vidas <= 0) return

        // Solo borramos el mensaje y creamos powerups en los saltos de tiempo (TICK)
        // para que no se generen al disparar o al mover la nave horizontalmente
        if (accion == "TICK") {
            mensaje = ""
            generarPowerup()
        }

        // --- 1. PROCESAR ACCIONES DEL USUARIO ---
        if (accion == "FUEGO") {
            // Crea una bala exactamente en la misma posición de la nave
            entidades.add(Entidad(nave.x, nave.y, "BALA"))
        }
        else if (accion == "BOMBA") {
            // Lógica del arma especial
            if (bombasMasivas > 0) {
                bombasMasivas--
                mensaje = "¡BOMBA MASIVA ACTIVADA! Aniquilación total."

                // Filtramos a todos los aliens y los eliminamos de golpe
                val todosLosAliens = entidades.filter( { it.tipo == "ALIEN" })
                puntos += (todosLosAliens.size * PUNTOS_POR_ALIEN)
                entidades.removeAll(todosLosAliens)
            }
            else {
                mensaje = "No tienes bombas. Atrapa todas las que puedas para poder lanzarlas."
            }
        }

        // --- 2. ACTUALIZAR POSICIONES ---
        // Le pasamos la acción a cada entidad para que decida si debe moverse o no
        for (entidad in entidades) {
            entidad.mover(accion)
        }

        // --- 3. RESOLUCIÓN DE COLISIONES Y LÍMITES ---
        // Lista temporal para guardar las entidades que deben ser eliminadas este turno
        val paraBorrar = mutableListOf<Entidad>()
        var invasionEnEsteTurno = false

        for (entidad in entidades) {
            // Si la entidad se sale por arriba o por abajo (y no es la nave), se marca para borrar
            if (entidad.y < 0 || entidad.y >= ALTO) {
                if (entidad.tipo != "NAVE") paraBorrar.add(entidad)
            }

            // Lógica específica para los enemigos
            if (entidad.tipo == "ALIEN") {
                // Verificamos si un alien ha llegado a la última fila (base del jugador)
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

            // Lógica específica para los objetos de ayuda
            if (entidad.tipo == "POWERUP") {
                if (hayColision(entidad, nave)) {
                    paraBorrar.add(entidad) // Borramos el powerup porque ha sido recogido
                    bombasMasivas++
                    mensaje = "¡POWERUP! Tienes $bombasMasivas bombas masivas"
                }
            }
        }

        // --- 4. APLICAR CONSECUENCIAS ---
        if (invasionEnEsteTurno) {
            vidas--
            mensaje = "¡Cuidado! Los aliens han invadido la base. Pierdes 1 vida."

            if (vidas <= 0) {
                mensaje = "¡GAME OVER! Has perdido todas tus vidas."
            }
        }

        // Eliminamos efectivamente de la memoria todas las entidades marcadas
        entidades.removeAll(paraBorrar)

        // Contamos cuántos aliens quedan vivos en la lista
        val alienRestantes = entidades.count({ it.tipo == "ALIEN" })

        // Si no quedan aliens, el jugador ha limpiado la oleada actual
        if (alienRestantes == 0) {
            mensaje = "¡OLEADA $numeroOleada COMPLETADA!"
            numeroOleada++
            generarOleada()
        }
    }
}