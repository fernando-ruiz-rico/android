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
        print("ID del socio (ej. SOC-001: ")
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

fun main() {
    println(pedirIdSocioValido())
}