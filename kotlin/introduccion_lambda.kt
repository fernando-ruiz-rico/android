/**
 * Este código introduce las Funciones Lambda y las funciones de orden superior en Kotlin:
 * - Una lambda es una función anónima (sin nombre) que se puede guardar en una variable o pasar como parámetro.
 * - .filter { } : Recorre una colección y devuelve una NUEVA lista solo con los elementos que cumplan una condición.
 * - .map { } : Recorre una colección, transforma cada elemento según una regla, y devuelve una NUEVA lista con los resultados.
 */

// Aquí tienes la versión clásica de una función. Está comentada para que sirva de comparación.
/*fun upperCaseString(text: String): String {
    return text.uppercase()
}*/

// Esta es la versión Lambda de la función anterior. 
// Se guarda directamente en una variable. La flecha '->' separa los parámetros del cuerpo de la función.
val upperCaseString = { text: String -> text.uppercase() }

fun main() {
    // Llama a la variable que contiene la lambda como si fuera una función normal.
    println(upperCaseString("hello world"))

    // Crea una lista inmutable con números positivos y negativos.
    val numbers = listOf(1, -2, 3, -4, 5, -6)
    println(numbers)

    // Usa la función de orden superior '.filter' pasándole una lambda directamente entre los paréntesis.
    // 'x' representa cada número individual de la lista. Solo se guardan los que son mayores que 0.
    val positives = numbers.filter({ x -> x > 0})
    println(positives)

    // Hace lo mismo que arriba, pero filtrando los que son menores que 0.
    val negatives = numbers.filter({ x -> x < 0})
    println(negatives)

    // En lugar de escribir la lambda directamente dentro de .filter, la guardamos primero en una variable.
    val isPositive = { x: Int -> x > 0 }
    // Y ahora le pasamos esa variable a la función .filter. Esto hace que el código sea muy legible.
    val positives2 = numbers.filter(isPositive)
    println(positives2)

    // Hacemos el mismo proceso para los números negativos.
    val isNegative = { x: Int -> x < 0 }
    val negatives2 = numbers.filter(isNegative)
    println(negatives2)

    // Usa '.map' para transformar todos los elementos de la lista. 
    // En este caso, multiplica cada número 'x' por 2 y devuelve una nueva lista con esos resultados.
    val doubled = numbers.map({ x -> x * 2 })
    println(doubled)

    // Igual que hicimos con filter, extraemos la lambda a una variable para reutilizarla.
    val isDoubled = { x: Int -> x * 2 }
    val doubled2 = numbers.map(isDoubled)
    println(doubled2)

    // Multiplica cada elemento por 3 usando una lambda inline (en la misma línea).
    val tripled = numbers.map({ x -> x * 3 })
    println(tripled)

    // Extrae la lambda a una variable y la aplica a la función .map.
    val isTripled = { x: Int -> x * 3 }
    val tripled2 = numbers.map(isTripled)
    println(tripled2)

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // You have a list of actions supported by a web service, a common prefix for all requests, and an ID of a particular resource. To request an action title over the resource with ID: 5, you need to create the following URL: https://example.com/book-info/5/title. Use a lambda expression to create a list of URLs from the list of actions.
    
    // Lista de acciones (strings) que queremos convertir en URLs completas.
    val actions = listOf("title", "year", "author")
    val prefix = "https://example.com/book-info"
    val id = 5
    // El objetivo es generar algo como esto:
    // https://example.com/book-info/5/title
    // https://example.com/book-info/5/year
    // https://example.com/book-info/5/author
    println(actions)
    
    // Usa .map para coger cada string (le damos el nombre 'action') e interpolarlo dentro de una nueva cadena de texto.
    val urls = actions.map({ action -> "$prefix/$id/$action" })
    println(urls)

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Given a list of words of varying lengths, use a lambda expression with the .filter function to create a new list. This new list should only contain words that have a length greater than 5 characters. Finally, print the resulting list.
    
    // Lista inicial con palabras de diferentes tamaños.
    val words = listOf("sun", "bat", "bread", "platypus", "light", "developer")
    println(words)
    
    // Usa .filter para evaluar cada palabra ('word'). 
    // Comprueba la propiedad '.length' (longitud) del string y solo se queda con las que superan los 5 caracteres.
    val longWords = words.filter({word -> word.length > 5})
    println(longWords)
}