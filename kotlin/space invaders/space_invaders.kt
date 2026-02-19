/**
 * JUEGO: INVASORES DEL ESPACIO
 * * Conceptos clave:
 * 1. class: La clase principal que nos permitirá crear objetos (naves, balas, etc.).
 * 2. mutableListOf: Una lista que puede crecer (añadir balas) y encogerse (borrar aliens).
 * 3. when: Una forma más limpia de escribir muchos 'if/else'.
 * 4. readln(): Detiene el programa para leer lo que escribe el usuario.
 * 5. Bucle while: Repite el juego una y otra vez hasta que ganamos o perdemos.
 */

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
                if (x > 11) x = 11
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

// --- 3. PROGRAMA PRINCIPAL ---
fun main() {
    // Constantes: El tamaño del mapa
    val ancho = 12
    val alto = 12

    // LISTA PRINCIPAL: Aquí guardamos TODAS LAS ENTIDADES (Nave, Aliens y Balas).
    // Usamos 'mutableListOf' porque durante el juego añadiremos y borraremos entidades.
    val entidades = mutableListOf<Entidad>()

    // CREACIÓN DE LA NAVE
    // La colocamos en el centro (ancho/2) y en la última fila (alto-1)
    val nave = Entidad(ancho / 2, alto - 1, "NAVE")
    entidades.add(nave)

    // CREACIÓN DE ALIENS
    // Un bucle for para crear 3 enemigos
    for (i in 1..3) {
        // Posición x aleatoria entre 0 y 11
        val xAleatoria = (Math.random() * ancho).toInt()
        entidades.add(Entidad(xAleatoria, 0, "ALIEN"))
    }

    // DIBUJAR
    // Llamamos a la función específica que pinta el mapa en la consola
    dibujarJuego(entidades, ancho, alto)
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

// --- FUNCIÓN PARA DIBUJAR EN PANTALLA ---
// Recibe la lista de objetos y el tamaño del mapa
fun dibujarJuego(lista: List<Entidad>, ancho: Int, alto: Int) {
    println("\n\n") // Imprime líneas vacías para limpiar visualmente

    // Bucle anidado: Recorremos cada fila (y) y cada columna (x)
    for (y in 0 until alto) {
        for (x in 0 until ancho) {
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