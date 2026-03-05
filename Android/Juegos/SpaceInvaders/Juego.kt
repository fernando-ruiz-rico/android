package com.example.myapplication

const val ANCHO = 12
const val ALTO = 16
const val ALIENS_POR_OLEADA = 3
const val MAX_VIDAS = 3
const val PROBABILIDAD_BOMBA = 0.5
const val PUNTOS_POR_ALIEN = 100

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
            "POWERUP" -> {
                y++
            }
        }
    }
}

fun obtenerIcono(tipo: String): String {
    return when (tipo) {
        "NAVE" -> "A"
        "ALIEN" -> "V"
        "BALA" -> "|"
        "POWERUP" -> "B"
        else -> "?"
    }
}

fun hayColision(entidad1: Entidad, entidad2: Entidad): Boolean {
    val mismaColumna = (entidad1.x == entidad2.x)
    val choqueDirecto = (entidad1.y == entidad2.y)
    val cruceEnElAire = (entidad1.y == entidad2.y + 1)

    return mismaColumna && (choqueDirecto || cruceEnElAire)
}

class Juego {
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

    fun generarOleada() {
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

    fun generarPowerup(entidades: MutableList<Entidad>) {
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

    fun obtenerMapaComoTexto(): String {
        var mapaTexto = ""

        // Bucle anidado: Recorremos cada fila (y) y cada columna (x)
        for (y in 0 until ALTO) {
            for (x in 0 until ANCHO) {
                var simbolo = "." // Por defecto dibujamos un punto (espacio vacío)

                // Buscamos: ¿Hay alguna entidad en esta coordenada (x, y)?
                // 'find' devuelve el objeto si lo encuentra, o null si no hay nada.
                val entidadAqui = entidades.find({ it.x == x && it.y == y })

                // Si encontramos algo (no es null)...
                if (entidadAqui != null) {
                    // Llamamos a la función correspondiente para saber qué dibujo poner (A, V, |)
                    simbolo = obtenerIcono(entidadAqui.tipo)
                }

                // Imprimimos el símbolo sin salto de línea
                mapaTexto += "$simbolo "
            }
            mapaTexto += "\n" // Al terminar la fila, hacemos un salto de línea
        }

        return mapaTexto;
    }
}