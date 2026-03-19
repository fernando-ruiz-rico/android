package com.example.myapplication

class JuegoInvasores {
    companion object {
        const val ANCHO = 11
        const val ALTO = 16
        const val ALIENS_POR_OLEADA = 3
        const val MAX_VIDAS = 3
        const val PROBABILIDAD_BOMBA = 0.5
        const val PUNTOS_POR_ALIEN = 100
        const val INTERVALO_MOVIMIENTO = 1000L
    }

    class Entidad(var x: Int, var y: Int, val tipo: String) {
        fun mover(accionUsuario:String) {
            when (tipo){
                "NAVE" -> {
                    if (accionUsuario == "IZQUIERDA") x--
                    if (accionUsuario == "DERECHA") x++
                    if (x < 0) x = 0
                    if (x > ANCHO - 1) x = ANCHO - 1
                }
                "ALIEN" -> if (accionUsuario == "TICK") y++
                "BALA" -> if (accionUsuario == "TICK") y--
                "POWERUP" -> if (accionUsuario == "TICK") y++
            }
        }
    }

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

    fun obtenerIcono(tipo: String): String {
        return when (tipo) {
            "NAVE" -> "🚨"
            "ALIEN" -> "👽"
            "BALA" -> "♦️"
            "POWERUP" -> "🧨"
            else -> "?"
        }
    }

    fun hayColision(entidad1: Entidad, entidad2: Entidad): Boolean {
        val mismaColumna = (entidad1.x == entidad2.x)
        val choqueDirecto = (entidad1.y == entidad2.y)
        val cruceEnElAire = (entidad1.y == entidad2.y + 1)
        return mismaColumna && (choqueDirecto || cruceEnElAire)
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
            posicionesUnicas.add((Math.random() * ANCHO).toInt())
        }
        for (x in posicionesUnicas) entidades.add(Entidad(x, 0, "ALIEN"))
    }

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

    fun obtenerMapaComoTexto(): String {
        var mapaTexto = "\n"
        for (y in 0 until ALTO) {
            for (x in 0 until ANCHO) {
                var simbolo = "▫️"
                val entidadAqui = entidades.find({ it.x == x && it.y == y })
                if (entidadAqui != null) simbolo = obtenerIcono(entidadAqui.tipo)
                mapaTexto += "$simbolo"
            }
            mapaTexto += "\n"
        }
        return mapaTexto;
    }

    fun turno(accion:String) {
        if (vidas <= 0) return

        if (accion == "TICK") {
            mensaje = ""
            generarPowerup()
        }

        if (accion == "FUEGO") entidades.add(Entidad(nave.x, nave.y, "BALA"))
        else if (accion == "BOMBA") {
            if (bombasMasivas > 0) {
                bombasMasivas--
                mensaje = "¡BOMBA MASIVA ACTIVADA! Aniquilación total."
                val todosLosAliens = entidades.filter( { it.tipo == "ALIEN" })
                puntos += (todosLosAliens.size * PUNTOS_POR_ALIEN)
                entidades.removeAll(todosLosAliens)
            } else {
                mensaje = "No tienes bombas. Atrapa todas las que puedas."
            }
        }

        for (entidad in entidades) entidad.mover(accion)

        val paraBorrar = mutableListOf<Entidad>()
        var invasionEnEsteTurno = false

        for (entidad in entidades) {
            if (entidad.y < 0 || entidad.y >= ALTO) {
                if (entidad.tipo != "NAVE") paraBorrar.add(entidad)
            }
            if (entidad.tipo == "ALIEN") {
                if (entidad.y >= ALTO - 1) {
                    paraBorrar.add(entidad)
                    invasionEnEsteTurno = true
                } else {
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
                if (hayColision(entidad, nave)) {
                    paraBorrar.add(entidad)
                    bombasMasivas++
                    mensaje = "¡POWERUP! Tienes $bombasMasivas bombas masivas"
                }
            }
        }

        if (invasionEnEsteTurno) {
            vidas--
            mensaje = "¡Cuidado! Los aliens han invadido la base. Pierdes 1 vida."
            if (vidas <= 0) mensaje = "¡GAME OVER! Has perdido todas tus vidas."
        }

        entidades.removeAll(paraBorrar)
        val alienRestantes = entidades.count({ it.tipo == "ALIEN" })

        if (alienRestantes == 0) {
            mensaje = "¡OLEADA $numeroOleada COMPLETADA!"
            numeroOleada++
            generarOleada()
        }
    }
}