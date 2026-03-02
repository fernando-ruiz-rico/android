import java.time.LocalDate

fun pedirTextoValido(mensaje: String) : String {
    while(true) {
        print(mensaje)
        val entrada = readln().trim()
        if (entrada.isNotEmpty()) return entrada
        println("Error: El campo no puede estar vacío.")
    }
}

fun pedirIdValido() : String {
    while(true) {
        print("ID del Material (ej. LIB-001): ")
        val entrada = readln().trim().uppercase()
        if (entrada.matches(Regex("[A-Z]{3}-[0-9]{3}"))) return entrada
        println("Error: Debes introducir 3 letras, un guión y 3 números.")
    }
}

fun pedirIdSocioValido(): String {
    while(true) {
        print("ID del socio (ej. SOC-001): ")
        val entrada = readln().trim().uppercase()
        if (entrada.matches(Regex("SOC-[0-9]{3}"))) return entrada
        println("Error: Debes introducir 'SOC-' seguido de 3 números")
    }
}

abstract class MaterialBiblioteca(
    val id:String, val titulo:String, val autor:String, val anioPublicacion:Int, val copiasTotales:Int
) {
    var copiasDisponibles: Int = copiasTotales

    fun prestar(): Boolean {
        if (copiasDisponibles > 0) {
            copiasDisponibles--
            return true
        }
        return false
    }

    fun devolver(): Boolean {
        if (copiasDisponibles < copiasTotales) {
            copiasDisponibles++
            return true
        }
        return false
    }

    fun mostraDetalles() {
        println("$id | $titulo | $autor | $anioPublicacion | ($copiasDisponibles / $copiasTotales)")
    }
}

class Libro(id: String, titulo:String, autor:String, anio:Int, copias:Int, val numeroPaginas:Int) :
      MaterialBiblioteca(id, titulo, autor, anio, copias) {
}

class Revista(id:String, titulo:String, autor:String, anio:Int, copias:Int, numeroEdicion:Int) :
      MaterialBiblioteca(id, titulo, autor, anio, copias) {
}

class PeliculaDVD(id:String, titulo:String, autor:String, anio:Int, copias:Int, duracionMinutos:Int) :
      MaterialBiblioteca(id, titulo, autor, anio, copias) {
}

class Socio(val idSocio:String, val nombre:String) {
}

class Prestamo(val idSocio: String, val idMaterial:String, val fechaPrestamo:LocalDate, val fechaVencimiento: LocalDate) {
    fun estaVencido():Boolean {
        val hoy = LocalDate.now()
        return hoy.isAfter(fechaVencimiento)
    }
}

class Biblioteca {
    private val inventario = mutableListOf<MaterialBiblioteca>()
    private val socios = mutableListOf<Socio>()
    private val prestamos = mutableListOf<Prestamo>()

    fun registrarMaterial(material: MaterialBiblioteca) {
        inventario.add(material)
        print("Material '${material.titulo}' añadido.")
    }

    fun registrarSocio(socio: Socio) {
        socios.add(socio)
        print("Socio '${socio.nombre}' añadido.")
    }

    fun mostrarInventario() {
        println("\n--- INVENTARIO ---")
        for (material in inventario) {
            material.mostraDetalles()
        }
    }

    fun mostrarSocios() {
        println("\n--- SOCIOS ---")
        for (socio in socios) {
            println("ID: ${socio.idSocio} | Nombre: ${socio.nombre}")
        }
    }

    fun mostrarPrestamos() {
        println("\n--- PRESTAMOS ---")
        for (prestamo in prestamos) {
            println("El socio '${prestamo.idSocio}' tiene '${prestamo.idMaterial}' (vence el ${prestamo.fechaVencimiento})")
        }
    }

    fun registrarPrestamo(idMaterial:String, idSocio:String) {
        var socioEncontrado: Socio? = null
        for (socio in socios) {
            if (socio.idSocio == idSocio) {
                socioEncontrado = socio
            }
        }
        if (socioEncontrado == null) {
            println("Error: No existe ningún socio con el ID '$idSocio'")
            return
        }

        var materialEncontrado: MaterialBiblioteca? = null
        for (material in inventario) {
            if (material.id == idMaterial) {
                materialEncontrado = material
            }
        }
        if (materialEncontrado == null) {
            println("Error: No existe ningún material con el ID '$idMaterial'")
            return
        }

        for (prestamo in prestamos) {
            if (prestamo.idSocio == idSocio && prestamo.idMaterial == idMaterial) {
                println("Error: El socio '$idSocio' ya tiene una copia de '$idMaterial'")
                return
            }
        }

        if (materialEncontrado.prestar()) {
            val hoy = LocalDate.now()
            val vencimiento = hoy.plusDays(14)

            prestamos.add(Prestamo(idSocio, idMaterial, hoy, vencimiento))
            println("Préstamo realizado")
        }
        else {
            println("Error: No quedan copias disponibles de '$idMaterial'")
        }
    }

    fun devolverPrestamo(idMaterial:String, idSocio:String) {
        var prestamoEncontrado: Prestamo? = null

        for (prestamo in prestamos) {
            if (prestamo.idSocio == idSocio && prestamo.idMaterial == idMaterial) {
                prestamoEncontrado = prestamo
            }
        }

        if (prestamoEncontrado != null) {
            for(material in inventario) {
                if (material.id == idMaterial) {
                    material.devolver()
                }
            }
        }
    }
}

fun main() {
    val biblioteca = Biblioteca()

    var continuar = true
    while (continuar) {
        println("\n--- GESTIÓN DE BIBLIOTECA ---")
        println("1. Inventario: Mostrar")
        println("2. Inventario: Registrar")
        println("3. Socios: Mostrar")
        println("4. Socios: Registrar")
        println("5. Préstamos: Mostrar")
        println("6. Préstamos: Registrar")
        println("7. Préstamos: Devolver")
        println("8. Salir")
        print("Selecciona una opción: ")

        when(readln().trim()) {
            "1" -> {
                biblioteca.mostrarInventario()
            }
            "2" -> {
                println("\n--- TIPO DE MATERIAL ---")
                println("1. Libros | 2. Revistas | 3. DVD")
                val tipo = pedirTextoValido("Elija el tipo: ")
                val id = pedirIdValido()
                val titulo = pedirTextoValido("Titulo: ")
                val autor = pedirTextoValido("Autor/Director: ")
                val anio = pedirTextoValido("Anio: ").toIntOrNull() ?: 2025
                val copias = pedirTextoValido("Copias: ").toIntOrNull() ?: 1

                when(tipo) {
                    "1" -> {
                        val paginas = pedirTextoValido("Paginas: ").toIntOrNull() ?: 100
                        biblioteca.registrarMaterial(Libro(id, titulo, autor, anio, copias, paginas))
                    }
                    "2" -> {
                        val edicion = pedirTextoValido("Edicion: ").toIntOrNull() ?: 1
                        biblioteca.registrarMaterial(Revista(id, titulo, autor, anio, copias, edicion))
                    }
                    "3" -> {
                        val duracion = pedirTextoValido("Duracion: ").toIntOrNull() ?: 90
                        biblioteca.registrarMaterial(PeliculaDVD(id, titulo, autor, anio, copias, duracion))
                    }
                }
            }
            "3" -> {
                biblioteca.mostrarSocios()
            }
            "4" -> {
                val id = pedirIdSocioValido()
                val nombre = pedirTextoValido("Nombre: ")
                biblioteca.registrarSocio(Socio(id, nombre))
            }
            "5" -> {
                biblioteca.mostrarPrestamos()
            }
            "6" -> {
                val idMaterial = pedirIdValido()
                val idSocio = pedirIdSocioValido()
                biblioteca.registrarPrestamo(idMaterial, idSocio)
            }
            "7" -> {
                val idMaterial = pedirIdValido()
                val idSocio = pedirIdSocioValido()
                biblioteca.devolverPrestamo(idMaterial, idSocio)
            }
            "8" -> {
                continuar = false
            }
        }
    }
}