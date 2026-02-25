/**
 * ==============================================================================
 * SISTEMA DE GESTIÓN DE BIBLIOTECA (VERSIÓN INICIAL)
 * ==============================================================================
 * Objetivo del programa:
 * Este código establece las bases para un sistema de gestión de inventario de una
 * biblioteca. Permite registrar diferentes tipos de materiales (libros, revistas
 * y DVDs) y mostrar un listado con los detalles básicos de los elementos guardados.
 * Al ser una versión inicial, algunos datos (como el año o las páginas) se introducen
 * de forma fija para facilitar las pruebas.
 *
 * Qué aprenderás de Kotlin con este código:
 * 1. Programación Orientada a Objetos (POO): Creación de jerarquías de clases.
 * 2. Clases Abstractas: Uso de `abstract class` para definir una plantilla base que
 * no puede ser instanciada directamente.
 * 3. Herencia y Polimorfismo: Uso del operador `:` para heredar de una clase padre
 * y paso de parámetros al constructor de la superclase.
 * 4. Encapsulamiento: Uso de `private` para proteger la lista del inventario y
 * evitar modificaciones externas no deseadas.
 * 5. Estructuras de control: Uso avanzado de `when` para gestionar menús y
 * bucles `while(true)` para la validación de entradas de usuario.
 * ==============================================================================
 */

/**
 * Clase base abstracta que representa cualquier material físico o digital dentro de la biblioteca.
 * Al ser abstracta, no puedes crear un "MaterialBiblioteca" directamente, sino instancias de sus clases hijas.
 *
 * @property id Identificador único del material.
 * @property titulo Título de la obra.
 * @property autor Creador, autor o director de la obra.
 * @property anio Año de publicación o lanzamiento.
 * @property copias Número de copias disponibles en la biblioteca.
 */
abstract class MaterialBiblioteca(
    val id:String, val titulo:String, val autor:String, val anio:Int, val copias:Int
) {
    /**
     * Muestra por consola los detalles básicos del material.
     * La palabra clave `open` permite que las clases hijas puedan sobrescribir (override) esta función en el futuro.
     */
    open fun mostrarDetalles() {
        // Imprime las propiedades separadas por una barra vertical
        println("$id | $titulo | $autor | $anio | $copias")
    }
}

/**
 * Representa un libro dentro del catálogo de la biblioteca.
 * Hereda de [MaterialBiblioteca].
 *
 * @param id Identificador único.
 * @param titulo Título del libro.
 * @param autor Autor del libro.
 * @param anio Año de publicación.
 * @param copias Cantidad de ejemplares.
 * @param numeroPaginas Total de páginas del libro (propiedad específica de esta clase).
 */
class Libro(
    id:String, titulo:String, autor:String, anio:Int, copias:Int, numeroPaginas:Int
): MaterialBiblioteca(id, titulo, autor, anio, copias) {
    // Al ser una versión inicial, aún no se utiliza 'numeroPaginas' en los métodos
}

/**
 * Representa una revista o publicación periódica en la biblioteca.
 * Hereda de [MaterialBiblioteca].
 *
 * @param id Identificador único.
 * @param titulo Nombre de la revista.
 * @param autor Editor o autor principal.
 * @param anio Año de publicación.
 * @param copias Cantidad de ejemplares.
 * @param numeroEdicion Número del volumen o edición específica.
 */
class Revista(
    id:String, titulo:String, autor:String, anio:Int, copias:Int, numeroEdicion:Int
): MaterialBiblioteca(id, titulo, autor, anio, copias) {
    // Al ser una versión inicial, aún no se utiliza 'numeroEdicion' en los métodos
}

/**
 * Representa una película en formato DVD disponible en la biblioteca.
 * Hereda de [MaterialBiblioteca].
 *
 * @param id Identificador único.
 * @param titulo Título de la película.
 * @param autor Director de la película.
 * @param anio Año de estreno.
 * @param copias Cantidad de copias físicas.
 * @param duracionMinutos Tiempo de duración de la película en minutos.
 */
class PeliculaDVD(
    id:String, titulo:String, autor:String, anio:Int, copias:Int, duracionMinutos:Int
): MaterialBiblioteca(id, titulo, autor, anio, copias) {
    // Al ser una versión inicial, aún no se utiliza 'duracionMinutos' en los métodos
}

/**
 * Gestor principal que administra la colección de materiales y las operaciones sobre ellos.
 */
class Biblioteca {
    // Lista mutable privada para almacenar el catálogo.
    // Al ser privada, ninguna otra parte del código puede borrar o modificar la lista directamente.
    private val inventario = mutableListOf<MaterialBiblioteca>()

    /**
     * Recorre la lista de inventario e imprime los detalles de cada material almacenado.
     */
    fun mostrarInventario() {
        println("\n--- INVENTARIO ---")
        // Iteramos sobre cada elemento guardado en la lista 'inventario'
        for (material in inventario) {
            // Gracias al polimorfismo, llama al método adecuado sin importar si es Libro, Revista o DVD
            material.mostrarDetalles()
        }
    }

    /**
     * Añade un nuevo material a la lista del inventario.
     *
     * @param material El objeto (Libro, Revista o PeliculaDVD) a registrar.
     */
    fun registrarMaterial(material: MaterialBiblioteca) {
        // Añadimos el objeto a la colección interna
        inventario.add(material)
    }
}

/**
 * Función auxiliar para solicitar texto al usuario asegurando que no se introduzca un valor vacío.
 *
 * @param mensaje El texto que se le mostrará al usuario para pedirle el dato.
 * @return Una cadena de texto válida (no vacía) introducida por el usuario.
 */
fun pedirTextoValido(mensaje: String): String {
    // Bucle infinito que solo se romperá cuando el usuario introduzca un texto válido
    while(true) {
        print(mensaje)
        // Leemos la entrada del usuario y eliminamos los espacios en blanco de los extremos con trim()
        val entrada = readln().trim()

        // Si la cadena no está vacía, devolvemos el valor y salimos de la función
        if (entrada.isNotEmpty()) return entrada

        // Si estaba vacía, mostramos el error y el bucle vuelve a empezar
        println("El campo no puede estar vacío")
    }
}

/**
 * Punto de entrada principal de la aplicación.
 * Presenta un menú interactivo en consola para gestionar la biblioteca.
 */
fun main() {
    // Instanciamos nuestro gestor principal
    val biblioteca = Biblioteca()

    println("Bienvenido a la biblioteca de Kotlin")

    // Variable de control para mantener el menú ejecutándose
    var continuar = true

    // Bucle principal del programa
    while (continuar) {
        println("\nMENÚ PRINCIPAL")
        println("==============")
        println("1. Inventario: Mostrar")
        println("2. Inventario: Registrar")
        print("Seleccione una opción: ")

        // Evaluamos la opción elegida por el usuario
        when(readln().trim()) {
            // Opción 1: Muestra los elementos registrados
            "1" -> biblioteca.mostrarInventario()

            // Opción 2: Proceso de registro de un nuevo material
            "2" -> {
                println("\n--- TIPO DE MATERIAL ---")
                println("1. Libro | 2. Revista | 3. DVD")

                // Pedimos los datos comunes utilizando la función de validación
                val tipo = pedirTextoValido("Elija el tipo: ")
                val id = pedirTextoValido("ID: ")
                val titulo = pedirTextoValido("Titulo: ")
                val autor = pedirTextoValido("Autor/Director: ")

                // En esta versión inicial, se asumen valores por defecto para el año y las copias
                val anio = 2026
                val copias = 1

                // Dependiendo del tipo de material elegido, creamos la instancia correspondiente
                when(tipo) {
                    // Creación de un Libro
                    "1" -> {
                        val paginas = 100 // Valor fijo en esta versión inicial
                        biblioteca.registrarMaterial(Libro(id, titulo, autor, anio, copias, paginas))
                    }
                    // Creación de una Revista
                    "2" -> {
                        val edicion = 1 // Valor fijo en esta versión inicial
                        biblioteca.registrarMaterial(Revista(id, titulo, autor, anio, copias, edicion))
                    }
                    // Creación de un DVD
                    "3" -> {
                        val duracion = 90 // Valor fijo en esta versión inicial
                        biblioteca.registrarMaterial(PeliculaDVD(id, titulo, autor, anio, copias, duracion))
                    }
                }
            }
        }
    }
}