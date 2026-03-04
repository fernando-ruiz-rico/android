/**
 * ==============================================================================
 * SISTEMA DE GESTIÓN DE BIBLIOTECA (VERSIÓN INTERMEDIA)
 * ==============================================================================
 * Objetivo del programa:
 * Esta versión introduce conceptos más avanzados que la básica, como el manejo del
 * tiempo y validaciones estrictas de formato, aunque todavía no guarda los datos
 * en disco (los datos se pierden al cerrar el programa).
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Manejo de Fechas: Uso de la clase `LocalDate` para calcular vencimientos.
 * 2. Expresiones Regulares (Regex): Validación de formatos específicos (como IDs).
 * 3. Lógica de Estado: Control de stock (disponible vs total) dentro de los objetos.
 * 4. Relaciones entre objetos: Cómo la clase 'Prestamo' vincula un 'Socio' y un 'Material'.
 * ==============================================================================
 */

import java.time.LocalDate

/**
 * Función auxiliar genérica para asegurar que el usuario no introduce texto vacío.
 */
fun pedirTextoValido(mensaje: String) : String {
    while(true) {
        print(mensaje)
        val entrada = readln().trim()
        // Si la entrada tiene contenido, la retornamos. Si no, repetimos el bucle.
        if (entrada.isNotEmpty()) return entrada
        println("Error: El campo no puede estar vacío.")
    }
}

/**
 * Solicita un ID validando que cumpla el formato: 3 letras, guión, 3 números.
 * Ejemplo válido: LIB-001
 */
fun pedirIdValido() : String {
    while(true) {
        print("ID del Material (ej. LIB-001): ")
        val entrada = readln().trim().uppercase() // Convertimos a mayúsculas automáticamente
        
        // Uso de Regex (Expresiones Regulares):
        // [A-Z]{3} -> Busca exactamente 3 letras de la A a la Z.
        // -        -> Busca un guión literal.
        // [0-9]{3} -> Busca exactamente 3 números del 0 al 9.
        if (entrada.matches(Regex("[A-Z]{3}-[0-9]{3}"))) return entrada
        
        println("Error: Debes introducir 3 letras, un guión y 3 números.")
    }
}

/**
 * Solicita un ID de socio validando que empiece por "SOC-" seguido de números.
 */
fun pedirIdSocioValido(): String {
    while(true) {
        print("ID del socio (ej. SOC-001): ")
        val entrada = readln().trim().uppercase()
        // Validamos el formato específico para socios
        if (entrada.matches(Regex("SOC-[0-9]{3}"))) return entrada
        println("Error: Debes introducir 'SOC-' seguido de 3 números")
    }
}

/**
 * Clase base abstracta. Define las propiedades y comportamientos comunes.
 * Gestiona internamente cuántas copias quedan disponibles.
 */
abstract class MaterialBiblioteca(
    val id:String, val titulo:String, val autor:String, val anioPublicacion:Int, val copiasTotales:Int
) {
    // Variable mutable para rastrear el stock actual en tiempo real
    var copiasDisponibles: Int = copiasTotales

    /**
     * Intenta reducir el stock en 1.
     * Retorna true si la operación fue exitosa, false si no había stock.
     */
    fun prestar(): Boolean {
        if (copiasDisponibles > 0) {
            copiasDisponibles--
            return true
        }
        return false
    }

    /**
     * Intenta aumentar el stock en 1.
     * Retorna true si se pudo devolver, false si el stock ya estaba completo.
     */
    fun devolver(): Boolean {
        if (copiasDisponibles < copiasTotales) {
            copiasDisponibles++
            return true
        }
        return false
    }

    override fun toString(): String {
        // Asignamos un icono visual dependiendo de si hay stock o no
        val icono = if (copiasDisponibles > 0) "🟢" else "🔴"
        return "$icono ($copiasDisponibles/$copiasTotales) [$id] \"$titulo\" ($anioPublicacion) de $autor"
    }
}

/**
 * Subclase específica para Libros (añade número de páginas).
 */
class Libro(id: String, titulo:String, autor:String, anio:Int, copias:Int, val numeroPaginas:Int) :
    MaterialBiblioteca(id, titulo, autor, anio, copias) {

    override fun toString(): String {
        return super.toString() + " | 📖 $numeroPaginas págs."
    }
}

/**
 * Subclase específica para Revistas (añade número de edición).
 */
class Revista(id:String, titulo:String, autor:String, anio:Int, copias:Int, val numeroEdicion:Int) :
    MaterialBiblioteca(id, titulo, autor, anio, copias) {

    override fun toString(): String {
        return super.toString() + " | 🗞️ Edición #$numeroEdicion"
    }
}

/**
 * Subclase específica para DVDs (añade duración).
 */
class PeliculaDVD(id:String, titulo:String, autor:String, anio:Int, copias:Int, val duracionMinutos:Int) :
    MaterialBiblioteca(id, titulo, autor, anio, copias) {

    override fun toString(): String {
        return super.toString() + " | 🎬 $duracionMinutos min."
    }
}

/**
 * Clase sencilla para almacenar datos de los miembros de la biblioteca.
 */
class Socio(val idSocio:String, val nombre:String) {
    override fun toString(): String {
        return "👤 [$idSocio] $nombre"
    }
}

/**
 * Clase que representa la acción de prestar un material a un socio.
 * Incluye lógica de fechas utilizando `LocalDate`.
 */
class Prestamo(val idSocio: String, val idMaterial:String, val fechaPrestamo:LocalDate, val fechaVencimiento: LocalDate) {
    
    /**
     * Comprueba si la fecha actual es posterior a la fecha límite de devolución.
     */
    fun estaVencido():Boolean {
        val hoy = LocalDate.now() // Obtiene la fecha de hoy del sistema
        return hoy.isAfter(fechaVencimiento) // Retorna true si hoy es después del vencimiento
    }

    override fun toString(): String {
        val estado = if (estaVencido()) "⚠️" else "✅"
        return "$estado $fechaVencimiento | Socio: $idSocio | Material: $idMaterial"
    }
}

/**
 * Clase gestora (Controller). Contiene las listas y la lógica principal.
 */
class Biblioteca {
    // Listas para almacenar los objetos en memoria
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
            println(material)
        }
    }

    fun mostrarSocios() {
        println("\n--- SOCIOS ---")
        for (socio in socios) {
            println(socio)
        }
    }

    fun mostrarPrestamos() {
        println("\n--- PRESTAMOS ---")
        for (prestamo in prestamos) {
            println(prestamo)
        }
    }

    /**
     * Lógica compleja para realizar un préstamo. Verifica:
     * 1. Existencia del socio.
     * 2. Existencia del material.
     * 3. Que el socio no tenga ya ese material prestado.
     * 4. Que haya stock disponible.
     */
    fun registrarPrestamo(idMaterial:String, idSocio:String) {
        // 1. Buscar socio
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

        // 2. Buscar material
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

        // 3. Verificar duplicados (el socio ya tiene este libro)
        for (prestamo in prestamos) {
            if (prestamo.idSocio == idSocio && prestamo.idMaterial == idMaterial) {
                println("Error: El socio '$idSocio' ya tiene una copia de '$idMaterial'")
                return
            }
        }

        // 4. Intentar realizar el préstamo (reducir stock)
        if (materialEncontrado.prestar()) {
            val hoy = LocalDate.now()
            val vencimiento = hoy.plusDays(14) // Calculamos fecha futura (2 semanas)

            prestamos.add(Prestamo(idSocio, idMaterial, hoy, vencimiento))
            println("Préstamo realizado")
        }
        else {
            println("Error: No quedan copias disponibles de '$idMaterial'")
        }
    }

    /**
     * Proceso de devolución de un material.
     */
    fun devolverPrestamo(idMaterial:String, idSocio:String) {
        var prestamoEncontrado: Prestamo? = null

        // Buscamos si existe un préstamo activo con esos IDs
        for (prestamo in prestamos) {
            if (prestamo.idSocio == idSocio && prestamo.idMaterial == idMaterial) {
                prestamoEncontrado = prestamo
            }
        }

        // Si existe el préstamo, buscamos el material en el inventario para restaurar su stock
        if (prestamoEncontrado != null) {
            for(material in inventario) {
                if (material.id == idMaterial) {
                    material.devolver() // Aumenta copiasDisponibles
                }
            }
        }
    }
}

/**
 * Función principal (Main Loop).
 */
fun main() {
    // Instanciamos la clase Biblioteca
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
            // Mostrar inventario
            "1" -> {
                biblioteca.mostrarInventario()
            }
            // Registrar nuevo material
            "2" -> {
                println("\n--- TIPO DE MATERIAL ---")
                println("1. Libros | 2. Revistas | 3. DVD")
                val tipo = pedirTextoValido("Elija el tipo: ")
                
                // Pedimos datos comunes
                val id = pedirIdValido()
                val titulo = pedirTextoValido("Titulo: ")
                val autor = pedirTextoValido("Autor/Director: ")
                // Intentamos convertir a Int, si falla usa valor por defecto (Elvis operator ?:)
                val anio = pedirTextoValido("Anio: ").toIntOrNull() ?: 2025
                val copias = pedirTextoValido("Copias: ").toIntOrNull() ?: 1

                // Creamos el objeto específico según la elección
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
            // Mostrar socios
            "3" -> {
                biblioteca.mostrarSocios()
            }
            // Registrar socio
            "4" -> {
                val id = pedirIdSocioValido()
                val nombre = pedirTextoValido("Nombre: ")
                biblioteca.registrarSocio(Socio(id, nombre))
            }
            // Mostrar préstamos
            "5" -> {
                biblioteca.mostrarPrestamos()
            }
            // Registrar préstamo
            "6" -> {
                val idMaterial = pedirIdValido()
                val idSocio = pedirIdSocioValido()
                biblioteca.registrarPrestamo(idMaterial, idSocio)
            }
            // Devolver préstamo
            "7" -> {
                val idMaterial = pedirIdValido()
                val idSocio = pedirIdSocioValido()
                biblioteca.devolverPrestamo(idMaterial, idSocio)
            }
            // Salir
            "8" -> {
                continuar = false
            }
        }
    }
}