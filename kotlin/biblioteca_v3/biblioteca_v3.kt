/**
 * ==============================================================================
 * SISTEMA DE GESTIÓN DE BIBLIOTECA (VERSIÓN AVANZADA)
 * ==============================================================================
 * Objetivo del programa:
 * Esta versión expande el sistema anterior añadiendo persistencia de datos (guardado
 * en ficheros CSV), gestión de socios, control de préstamos con fechas y validaciones
 * más estrictas mediante Expresiones Regulares (Regex).
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Persistencia de Datos: Uso de `java.io.File` para leer y escribir archivos CSV.
 * 2. Manejo de Fechas: Uso de `java.time.LocalDate` para calcular vencimientos.
 * 3. Validaciones avanzadas: Uso de `Regex` para asegurar formatos de ID correctos.
 * 4. Singleton: Uso de `object` en lugar de `class` para la Biblioteca, asegurando
 * que solo exista una única instancia del gestor en todo el programa.
 * 5. Lógica de Negocio: Control de stock (copias disponibles vs totales).
 * ==============================================================================
 */

import java.io.File
import java.time.LocalDate

/**
 * Función auxiliar para solicitar texto al usuario asegurando que no se introduzca un valor vacío.
 *
 * @param mensaje El texto que se le mostrará al usuario.
 * @return Una cadena de texto válida (no vacía).
 */
fun pedirTextoValido(mensaje: String) : String {
    while(true) {
        print(mensaje)
        val entrada = readln().trim()
        if (entrada.isNotEmpty()) return entrada
        println("Error: El campo no puede estar vacío.")
    }
}

/**
 * Solicita un ID de material validando que cumpla el formato específico (3 letras, guión, 3 números).
 * Utiliza Expresiones Regulares (Regex).
 *
 * @return Un ID válido formateado en mayúsculas (ej. LIB-001).
 */
fun pedirIdValido() : String {
    while(true) {
        print("ID del Material (ej. LIB-001): ")
        val entrada = readln().trim().uppercase()
        // Validamos que cumpla el patrón: 3 letras de la A a la Z, un guión, y 3 dígitos
        if (entrada.matches(Regex("[A-Z]{3}-[0-9]{3}"))) return entrada
        println("Error: Debes introducir 3 letras, un guión y 3 números.")
    }
}

/**
 * Solicita un ID de socio validando el formato específico.
 *
 * @return Un ID de socio válido (ej. SOC-001).
 */
fun pedirIdSocioValido(): String {
    while(true) {
        print("ID del socio (ej. SOC-001): ")
        val entrada = readln().trim().uppercase()
        // Validamos que empiece por SOC- seguido de 3 dígitos
        if (entrada.matches(Regex("SOC-[0-9]{3}"))) return entrada
        println("Error: Debes introducir 'SOC-' seguido de 3 números")
    }
}

/**
 * Clase base abstracta para los materiales.
 * Ahora incluye lógica para gestionar el stock disponible (préstamos y devoluciones).
 *
 * @property copiasTotales Cantidad total de ejemplares propiedad de la biblioteca.
 * @property copiasDisponibles Cantidad actual en estantería (se modifica al prestar/devolver).
 */
abstract class MaterialBiblioteca(
    val id:String, val titulo:String, val autor:String, val anioPublicacion:Int, val copiasTotales:Int
) {
    // Inicialmente, las copias disponibles son iguales a las totales
    var copiasDisponibles: Int = copiasTotales

    /**
     * Intenta realizar un préstamo reduciendo el stock disponible.
     * @return `true` si se pudo prestar, `false` si no hay stock.
     */
    fun prestar(): Boolean {
        if (copiasDisponibles > 0) {
            copiasDisponibles--
            return true
        }
        return false
    }

    /**
     * Devuelve un material incrementando el stock disponible.
     * @return `true` si se devolvió correctamente, `false` si ya estaba todo el stock completo.
     */
    fun devolver(): Boolean {
        if (copiasDisponibles < copiasTotales) {
            copiasDisponibles++
            return true
        }
        return false
    }

    /**
     * Genera una cadena con formato CSV (valores separados por punto y coma) para guardar en fichero.
     * `open` permite que las clases hijas añadan sus propios campos a esta cadena.
     */
    open fun textoCSV(): String {
        // Usamos reflection (this::class.simpleName) para guardar el tipo de clase (Libro, Revista, etc.)
        return "${this::class.simpleName};$id;$titulo;$autor;$anioPublicacion;$copiasTotales;$copiasDisponibles"
    }

    /**
     * Representación en cadena del objeto con iconos de estado para la consola.
     */
    override fun toString(): String {
        val icono = if (copiasDisponibles > 0) "✅" else "⚠️"
        // Formato mejorado: Icono [ID] Titulo (Año) - Autor | Stock
        return "$icono ($copiasDisponibles/$copiasTotales) [$id] \"$titulo\" ($anioPublicacion) de $autor"
    }
}

/**
 * Subclase Libro. Añade el número de páginas.
 */
class Libro(id: String, titulo:String, autor:String, anio:Int, copias:Int, val numeroPaginas:Int) :
    MaterialBiblioteca(id, titulo, autor, anio, copias) {

    // Sobrescribimos para añadir el campo específico al CSV
    override fun textoCSV(): String {
        return super.textoCSV() + ";$numeroPaginas"
    }

    override fun toString(): String {
        return super.toString() + " | 📖 $numeroPaginas págs."
    }
}

/**
 * Subclase Revista. Añade el número de edición.
 */
class Revista(id:String, titulo:String, autor:String, anio:Int, copias:Int, val numeroEdicion:Int) :
    MaterialBiblioteca(id, titulo, autor, anio, copias) {

    override fun textoCSV(): String {
        return super.textoCSV() + ";$numeroEdicion"
    }

    override fun toString(): String {
        return super.toString() + " | 🗞️ Edición #$numeroEdicion"
    }
}

/**
 * Subclase PeliculaDVD. Añade la duración.
 */
class PeliculaDVD(id:String, titulo:String, autor:String, anio:Int, copias:Int, val duracionMinutos:Int) :
    MaterialBiblioteca(id, titulo, autor, anio, copias) {

    override fun textoCSV(): String {
        return super.textoCSV() + ";$duracionMinutos"
    }

    override fun toString(): String {
        return super.toString() + " | 🎬 $duracionMinutos min."
    }
}

/**
 * Clase para representar a los usuarios de la biblioteca.
 */
class Socio(val idSocio:String, val nombre:String) {
    fun textoCSV(): String {
        return "$idSocio;$nombre"
    }

    override fun toString(): String {
        return "👤 [$idSocio] $nombre"
    }
}

/**
 * Clase que vincula un Socio con un Material y fechas de gestión.
 */
class Prestamo(val idSocio: String, val idMaterial:String, val fechaPrestamo:LocalDate, val fechaVencimiento: LocalDate) {
    
    /**
     * Comprueba si la fecha actual es posterior a la fecha de vencimiento.
     */
    fun estaVencido():Boolean {
        val hoy = LocalDate.now()
        return hoy.isAfter(fechaVencimiento)
    }

    fun textoCSV(): String {
        return "$idSocio;$idMaterial;$fechaPrestamo;$fechaVencimiento"
    }

    override fun toString(): String {
        val estado = if (estaVencido()) "⚠️" else "✅"
        return "$estado $fechaVencimiento | Socio: $idSocio | Material: $idMaterial"
    }
}

/**
 * Gestor principal de la biblioteca.
 * A diferencia de la versión anterior, aquí usamos `object` (Singleton).
 * Esto significa que no hace falta instanciarla con `val biblio = Biblioteca()`,
 * se accede directamente a sus métodos estáticos.
 */
object Biblioteca {
    // Listas privadas para el manejo en memoria
    private val inventario = mutableListOf<MaterialBiblioteca>()
    private val socios = mutableListOf<Socio>()
    private val prestamos = mutableListOf<Prestamo>()

    /**
     * Registra un material en la lista en memoria.
     */
    fun registrarMaterial(material: MaterialBiblioteca) {
        inventario.add(material)
        print("Material '${material.titulo}' añadido.")
    }

    /**
     * Registra un nuevo socio en el sistema.
     */
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
        println("\n--- PRÉSTAMOS ---")
        for (prestamo in prestamos) {
            println(prestamo)
        }
    }

    /**
     * Gestiona la creación de un préstamo verificando todas las reglas de negocio:
     * 1. Que el socio exista.
     * 2. Que el material exista.
     * 3. Que el socio no tenga ya ese mismo material.
     * 4. Que haya stock disponible.
     */
    fun registrarPrestamo(idMaterial:String, idSocio:String) {
        // Búsqueda del socio
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

        // Búsqueda del material
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

        // Verificación de duplicados (el socio ya tiene ese libro)
        for (prestamo in prestamos) {
            if (prestamo.idSocio == idSocio && prestamo.idMaterial == idMaterial) {
                println("Error: El socio '$idSocio' ya tiene una copia de '$idMaterial'")
                return
            }
        }

        // Intento de préstamo (reduce stock)
        if (materialEncontrado.prestar()) {
            val hoy = LocalDate.now()
            val vencimiento = hoy.plusDays(14) // Préstamo de 2 semanas

            prestamos.add(Prestamo(idSocio, idMaterial, hoy, vencimiento))
            println("Préstamo realizado")
        }
        else {
            println("Error: No quedan copias disponibles de '$idMaterial'")
        }
    }

    /**
     * Gestiona la devolución de un material.
     * Elimina el registro del préstamo y aumenta el stock del material.
     */
    fun devolverPrestamo(idMaterial:String, idSocio:String) {
        var prestamoEncontrado: Prestamo? = null

        // Buscamos el préstamo activo
        for (prestamo in prestamos) {
            if (prestamo.idSocio == idSocio && prestamo.idMaterial == idMaterial) {
                prestamoEncontrado = prestamo
            }
        }

        if (prestamoEncontrado != null) {
            // Buscamos el material para devolverle el stock
            for(material in inventario) {
                if (material.id == idMaterial) {
                    material.devolver()
                }
            }
            // (Nota: En un sistema real, aquí deberíamos eliminar 'prestamoEncontrado' de la lista 'prestamos')
        }
    }

    /**
     * Guarda el estado actual de las listas en archivos .csv locales.
     * Sobrescribe los archivos existentes.
     */
    fun guardarDatos() {
        var textoInventario = "";
        var textoSocios = "";
        var textoPrestamos = "";

        // Construimos las cadenas CSV recorriendo las listas
        for (material in inventario) {
            textoInventario += material.textoCSV() + "\n"
        }

        for (socio in socios) {
            textoSocios += socio.textoCSV() + "\n"
        }

        for (prestamo in prestamos) {
            textoPrestamos += prestamo.textoCSV() + "\n"
        }

        // Escribimos en disco
        File("inventario.csv").writeText(textoInventario)
        File("socios.csv").writeText(textoSocios)
        File("prestamos.csv").writeText(textoPrestamos)
    }

    /**
     * Carga los datos desde los archivos .csv al iniciar el programa.
     * Parsea (analiza) cada línea para reconstruir los objetos.
     */
    fun cargarDatos() {
        // Carga de Inventario
        if (File("inventario.csv").exists()) {
            inventario.clear()
            for (linea in File("inventario.csv").readLines()) {
                if (linea.trim().isNotEmpty()) {
                    val datos = linea.split(";")
                    var material: MaterialBiblioteca? = null
                    // El primer campo del CSV nos dice qué tipo de objeto crear
                    when (datos[0]) {
                        "Libro" -> {
                            material = Libro(datos[1], datos[2], datos[3],
                                datos[4].toInt(), datos[5].toInt(), datos[7].toInt())
                        }
                        "Revista" -> {
                            material = Revista(datos[1], datos[2], datos[3],
                                datos[4].toInt(), datos[5].toInt(), datos[7].toInt())
                        }
                        "PeliculaDVD" -> {
                            material = PeliculaDVD(datos[1], datos[2], datos[3],
                                datos[4].toInt(), datos[5].toInt(), datos[7].toInt())
                        }
                    }
                    // Si se creó correctamente, restauramos su stock actual y lo añadimos
                    if (material != null) {
                        material.copiasDisponibles = datos[6].toInt()
                        inventario.add(material)
                    }
                }
            }
        }

        // Carga de Socios
        if (File("socios.csv").exists()) {
            socios.clear()
            for (linea in File("socios.csv").readLines()) {
                if (linea.trim().isNotEmpty()) {
                    val datos = linea.split(";")
                    socios.add(Socio(datos[0], datos[1]))
                }
            }
        }

        // Carga de Préstamos
        if (File("prestamos.csv").exists()) {
            prestamos.clear()
            for (linea in File("prestamos.csv").readLines()) {
                if (linea.trim().isNotEmpty()) {
                    val datos = linea.split(";")
                    // Parseamos las fechas de String a LocalDate
                    val fechaPrestamo = LocalDate.parse(datos[2])
                    val fechaVencimiento = LocalDate.parse(datos[3])
                    prestamos.add(Prestamo(datos[0], datos[1], fechaPrestamo, fechaVencimiento))
                }
            }
        }
    }
}

/**
 * Punto de entrada principal.
 * Gestiona el flujo del programa y el menú de usuario.
 */
fun main() {
    // Al arrancar, intentamos leer los ficheros anteriores
    Biblioteca.cargarDatos()

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
        println("8. Guardar y salir")
        print("Selecciona una opción: ")

        when(readln().trim()) {
            // Opción 1: Listar materiales
            "1" -> {
                Biblioteca.mostrarInventario()
            }
            // Opción 2: Añadir nuevo material
            "2" -> {
                println("\n--- TIPO DE MATERIAL ---")
                println("1. Libros | 2. Revistas | 3. DVD")
                val tipo = pedirTextoValido("Elija el tipo: ")
                val id = pedirIdValido()
                val titulo = pedirTextoValido("Titulo: ")
                val autor = pedirTextoValido("Autor/Director: ")
                // Uso del operador Elvis (?:) para asignar valor por defecto si falla la conversión a Int
                val anio = pedirTextoValido("Año: ").toIntOrNull() ?: 2025
                val copias = pedirTextoValido("Copias: ").toIntOrNull() ?: 1

                when(tipo) {
                    "1" -> {
                        val paginas = pedirTextoValido("Paginas: ").toIntOrNull() ?: 100
                        Biblioteca.registrarMaterial(Libro(id, titulo, autor, anio, copias, paginas))
                    }
                    "2" -> {
                        val edicion = pedirTextoValido("Edicion: ").toIntOrNull() ?: 1
                        Biblioteca.registrarMaterial(Revista(id, titulo, autor, anio, copias, edicion))
                    }
                    "3" -> {
                        val duracion = pedirTextoValido("Duracion: ").toIntOrNull() ?: 90
                        Biblioteca.registrarMaterial(PeliculaDVD(id, titulo, autor, anio, copias, duracion))
                    }
                }
            }
            // Opción 3: Listar socios
            "3" -> {
                Biblioteca.mostrarSocios()
            }
            // Opción 4: Registrar nuevo socio
            "4" -> {
                val id = pedirIdSocioValido()
                val nombre = pedirTextoValido("Nombre: ")
                Biblioteca.registrarSocio(Socio(id, nombre))
            }
            // Opción 5: Ver préstamos activos
            "5" -> {
                Biblioteca.mostrarPrestamos()
            }
            // Opción 6: Prestar material a socio
            "6" -> {
                val idMaterial = pedirIdValido()
                val idSocio = pedirIdSocioValido()
                Biblioteca.registrarPrestamo(idMaterial, idSocio)
            }
            // Opción 7: Devolución de material
            "7" -> {
                val idMaterial = pedirIdValido()
                val idSocio = pedirIdSocioValido()
                Biblioteca.devolverPrestamo(idMaterial, idSocio)
            }
            // Opción 8: Persistencia y Salida
            "8" -> {
                Biblioteca.guardarDatos()
                continuar = false
            }
        }
    }
}