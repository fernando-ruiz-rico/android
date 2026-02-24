abstract class MaterialBiblioteca(
    val id:String, val titulo:String, val autor:String, val anio:Int, val copias:Int
) {
    open fun mostrarDetalles() {
        println("$id | $titulo | $autor | $anio | $copias")
    }
}

class Libro(
    id:String, titulo:String, autor:String, anio:Int, copias:Int, numeroPaginas:Int
): MaterialBiblioteca(id, titulo, autor, anio, copias) {
}

class Revista(
    id:String, titulo:String, autor:String, anio:Int, copias:Int, numeroEdicion:Int
): MaterialBiblioteca(id, titulo, autor, anio, copias) {
}

class PeliculaDVD(
    id:String, titulo:String, autor:String, anio:Int, copias:Int, duracionMinutos:Int
): MaterialBiblioteca(id, titulo, autor, anio, copias) {
}

class Biblioteca {
    private val inventario = mutableListOf<MaterialBiblioteca>()

    fun mostrarInventario() {
        println("\n--- INVENTARIO ---")
        for (material in inventario) {
            material.mostrarDetalles()
        }
    }

    fun registrarMaterial(material: MaterialBiblioteca) {
        inventario.add(material)
    }
}

fun pedirTextoValido(mensaje: String): String {
    while(true) {
        print(mensaje)
        val entrada = readln().trim()
        if (entrada.isNotEmpty()) return entrada
        println("El campo no puede estar vacío")
    }
}

fun main() {
    val biblioteca = Biblioteca()

    println("Bienvenido a la biblioteca de Kotlin")

    var continuar = true
    while (continuar) {
        println("\nMENÚ PRINCIPAL")
        println("==============")
        println("1. Inventario: Mostrar")
        println("2. Inventario: Registrar")
        print("Seleccione una opción: ")

        when(readln().trim()) {
            "1" -> biblioteca.mostrarInventario()
            "2" -> {
                println("\n--- TIPO DE MATERIAL ---")
                println("1. Libro | 2. Revista | 3. DVD")
                val tipo = pedirTextoValido("Elija el tipo: ")
                val id = pedirTextoValido("ID: ")
                val titulo = pedirTextoValido("Titulo: ")
                val autor = pedirTextoValido("Autor/Director: ")
                val anio = 2026
                val copias = 1

                when(tipo) {
                    // Libro
                    "1" -> {
                        val paginas = 100
                        biblioteca.registrarMaterial(Libro(id, titulo, autor, anio, copias, paginas))
                    }
                    // Revista
                    "2" -> {
                        val edicion = 1
                        biblioteca.registrarMaterial(Revista(id, titulo, autor, anio, copias, edicion))
                    }
                    // DVD
                    "3" -> {
                        val duracion = 90
                        biblioteca.registrarMaterial(PeliculaDVD(id, titulo, autor, anio, copias, duracion))
                    }
                }
            }
        }
    }
}