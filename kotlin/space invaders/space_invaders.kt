class Entidad(var x: Int, var y: Int, val tipo:String) {
    fun mover(accionUsuario:String) {
        when (tipo){
            "NAVE" -> {
                if (accionUsuario == "IZQUIERDA") x--
                if (accionUsuario == "DERECHA") x++

                if (x < 0) x = 0
                if (x > 11) x = 11
            }
            "ALIEN" -> {
                y++
            }
            "BALA" -> {
                y--
            }
        }
    }
}

fun main() {
    val ancho = 12
    val alto = 12

    val entidades = mutableListOf<Entidad>()

    val nave = Entidad(ancho / 2,alto - 1,"NAVE")
    entidades.add(nave)

    for (i in 1..3) {
        val xAleatoria = (Math.random() * ancho).toInt()
        entidades.add(Entidad(xAleatoria,0,"ALIEN"))
    }

    dibujarJuego(entidades, 12,12)
}

fun obtenerIcono(tipo: String): String {
    return when (tipo) {
        "NAVE" -> "A"
        "ALIEN" -> "V"
        "BALA" -> "|"
        else -> "?"
    }
}

fun dibujarJuego(lista: List<Entidad>, ancho: Int, alto: Int) {
    println("\n\n")

    for (y in 0 until alto) {
        for (x in 0 until ancho) {
            var simbolo = "."

            val entidadAqui = lista.find({ it.x == x && it.y == y })

            if (entidadAqui != null) {
                simbolo = obtenerIcono(entidadAqui.tipo)
            }

            print("$simbolo ")
        }
        println()
    }
}