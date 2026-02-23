/**
 * JUEGO: INVASORES DEL ESPACIO
 * * Conceptos clave:
 * 1. class: La clase principal que nos permitirá crear objetos (naves, balas, etc.).
 * 2. mutableListOf: Una lista que puede crecer (añadir balas) y encogerse (borrar aliens).
 * 3. when: Una forma más limpia de escribir muchos 'if/else'.
 * 4. readln(): Detiene el programa para leer lo que escribe el usuario.
 * 5. Bucle while: Repite el juego una y otra vez hasta que ganamos o perdemos.
 */

// Constantes: El tamaño del mapa
const val ANCHO = 12
const val ALTO = 6
const val ALIENS_POR_OLEADA = 3

// --- CLASE 'ENTIDAD' PARA DEFINIR LOS OBJETOS ---
// Una clase define cómo son los objetos. Aquí decimos que toda entidad tiene:
// - x, y: Coordenadas (posición en el mapa). Son 'var' porque cambian al moverse.
// - tipo: Un texto ("NAVE", "ALIEN", etc.) para saber qué es. Es 'val' porque no cambia.
class Entidad(var x: Int, var y: Int, val tipo: String) {

    // Función (método) para mover este objeto concreto.
    // Recibe la acción del usuario ("IZQUIERDA", "FUEGO", etc.)
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
        }
    }
}

// --- FUNCIÓN AUXILIAR (LÓGICA VISUAL) ---
// De momento, esta función no pertenece a la clase.
// Simplemente traduce un TIPO en un DIBUJO.
fun obtenerIcono(tipo: String): String {
    return when (tipo) {
        "NAVE" -> "A"
        "ALIEN" -> "V"
        "BALA" -> "|"
        else -> "?"
    }
}

// --- FUNCIÓN PARA LEER EL TECLADO ---
// Pausa el juego, lee la letra que introduce el usuario y devuelve la acción correspondiente.
fun leerAccion(): String {
    print("Mover [i/d], Fuego [f], Salir [x] > ")
    val entrada = readln()

    return when (entrada.lowercase()) {
        "i" -> "IZQUIERDA"
        "d" -> "DERECHA"
        "f" -> "FUEGO"
        "x" -> "SALIR"
        else -> ""
    }
}

// --- FUNCIÓN PARA DETECTAR COLISIONES ---
// Recibe un Alien y una Bala, y devuelve un Boolean (true si chocan, false si no).
fun hayColision(alien: Entidad, bala: Entidad): Boolean {
    val mismaColumna = (alien.x == bala.x)
    val choqueDirecto = (alien.y == bala.y)
    val cruceEnElAire = (alien.y == bala.y + 1)

    return mismaColumna && (choqueDirecto || cruceEnElAire)
}

// --- CREACIÓN DE ALIENS ---
fun generarOleada(entidades: MutableList<Entidad>) {
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

// --- FUNCIÓN PARA DIBUJAR EN PANTALLA ---
// Recibe la lista de objetos y el tamaño del mapa
fun dibujarJuego(lista: List<Entidad>, puntos: Int, vidas: Int, numOleada: Int) {
    println("\n\n") // Imprime líneas vacías para limpiar visualmente
    println("=== PUNTUACIÓN: $puntos | VIDAS: $vidas  | OLEADA: $numOleada ===")

    // Bucle anidado: Recorremos cada fila (y) y cada columna (x)
    for (y in 0 until ALTO) {
        for (x in 0 until ANCHO) {
            var simbolo = "." // Por defecto dibujamos un punto (espacio vacío)

            // Buscamos: ¿Hay alguna entidad en esta coordenada (x, y)?
            // 'find' devuelve el objeto si lo encuentra, o null si no hay nada.
            val entidadAqui = lista.find({ it.x == x && it.y == y })

            // Si encontramos algo (no es null)...
            if (entidadAqui != null) {
                // Llamamos a la función correspondiente para saber qué dibujo poner (A, V, |)
                simbolo = obtenerIcono(entidadAqui.tipo)
            }

            // Imprimimos el símbolo sin salto de línea
            print("$simbolo ")
        }
        println() // Al terminar la fila, hacemos un salto de línea
    }
}

// --- FUNCIÓN PRINCIPAL ---
// Es la función que se llama al iniciar el programa y contiene la lógica principal del juego.
fun main() {
    // LISTA PRINCIPAL: Aquí guardamos TODAS LAS ENTIDADES (Nave, Aliens y Balas).
    // Usamos 'mutableListOf' porque durante el juego añadiremos y borraremos entidades.
    val entidades = mutableListOf<Entidad>()

    // CREACIÓN DE LA NAVE
    // La colocamos en el centro (ancho/2) y en la última fila (alto-1)
    val nave = Entidad(ANCHO / 2, ALTO - 1, "NAVE")
    entidades.add(nave)

    generarOleada(entidades)

    var puntos = 0
    var vidas = 3
    var numeroOleada = 1


    // BUCLE PRINCIPAL DEL JUEGO: Se repite hasta que el jugador gana o pierde o decide salir.
    while (true) {

        // Llamamos a la función específica que pinta el mapa en la consola
        dibujarJuego(entidades, puntos, vidas, numeroOleada)

        // Leemos la acción del usuario (mover izquierda/derecha, disparar o salir)
        val accion = leerAccion()

        // Si el usuario decide salir, terminamos el juego
        if (accion == "SALIR") {
            return
        } else {
            // Si el usuario dispara, creamos una bala donde esté la nave
            if (accion == "FUEGO") {
                entidades.add(Entidad(nave.x, nave.y, "BALA"))
            }

            // Movemos todas las entidades según su tipo y la acción del usuario
            for (entidad in entidades) {
                entidad.mover(accion)
            }

            // Lista temporal para guardar las entidades que deben ser eliminadas (balas que chocan, aliens que llegan al final, etc.)
            val paraBorrar = mutableListOf<Entidad>()

            var invasionEnEsteTurno = false

            // Verificamos colisiones y condiciones de fin de juego
            for (entidad in entidades) {
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
                                    puntos += 100
                                    println("¡BOOM! Alien eliminado. Ganas 100 puntos.")
                                }
                            }
                        }
                    }
                }
            }

            if (invasionEnEsteTurno) {
                vidas--
                println("¡Cuidado! Los aliens han invadido la base. Pierdes 1 vida.")

                if (vidas <= 0) {
                    dibujarJuego(entidades, puntos, vidas, numeroOleada)
                    println("¡GAME OVER! Has perdido todas tus vidas.")
                    return
                }
            }

            // Eliminamos todas las entidades que deben ser borradas (aliens eliminados, balas que han salido del mapa, etc.)
            entidades.removeAll(paraBorrar)

            // Contamos cuántos aliens quedan en la lista
            val alienRestantes = entidades.count({ it.tipo == "ALIEN" })

            // Si no quedan aliens, el jugador gana
            if (alienRestantes == 0) {
                numeroOleada++
                println("¡OLEADA COMPLETADA! Preparando oleada núm. $numeroOleada")
                generarOleada(entidades)
            }
        }
    }
}
