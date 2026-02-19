/**
 * Este código sirve como demostración de las estructuras de control de flujo más comunes:
 * - Bucles (for, while, do-while) para iterar sobre rangos y colecciones.
 * - Condicionales (if, when) para tomar decisiones lógicas como en el juego "FizzBuzz".
 * - Creación y recorrido de listas de solo lectura.
 */
fun main() {
    // Bucle 'for' que recorre un rango de números del 1 al 5 (ambos incluidos).
    for (number in 1..5) {
        print(number)
    }
    // Imprime un salto de línea vacío para separar el resultado en la consola.
    println()

    // Crea una lista de solo lectura (inmutable) llamada 'cakes' con cadenas de texto.
    val cakes = listOf("carrot", "cheese", "chocolate")
    // Bucle 'for' que recorre cada elemento de la lista 'cakes'.
    for (cake in cakes) {
        // Imprime el mensaje utilizando interpolación de cadenas ($cake) para insertar el valor de la variable.
        println("It's a $cake cake")
    }

    // Variable mutable (var) que sirve como contador de las tartas comidas.
    var cakesEaten = 0
    // Bucle 'while' que se repite siempre y cuando se hayan comido menos de 3 tartas.
    while (cakesEaten < 3) {
        println("Eat a cake")
        // Incrementa el contador en 1 en cada iteración.
        cakesEaten++
    }

    // Variable mutable que sirve como contador de las tartas horneadas.
    var cakesBaked = 0
    // Bucle 'do-while'. La diferencia con 'while' es que este bloque de código siempre se ejecuta al menos una vez antes de comprobar la condición.
    do {
        println("Bake a cake")
        cakesBaked++
    // Repite el bucle mientras el número de tartas horneadas sea menor al número de tartas comidas.
    } while(cakesBaked < cakesEaten)

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // You have a program that counts pizza slices until there's a whole pizza with 8 slices. Refactor this program in two ways: Use a while loop. Use a do-while loop.
    
    // --- CÓDIGO ORIGINAL SIN REFACTORIZAR ---
    // Aquí se incrementa e imprime manualmente cada porción sin usar bucles.
    var pizzaSlices = 0
    pizzaSlices++
    println("Pizza slices: $pizzaSlices :(")
    pizzaSlices++
    println("Pizza slices: $pizzaSlices :(")
    pizzaSlices++
    println("Pizza slices: $pizzaSlices :(")
    pizzaSlices++
    println("Pizza slices: $pizzaSlices :(")
    pizzaSlices++
    println("Pizza slices: $pizzaSlices :(")
    pizzaSlices++
    println("Pizza slices: $pizzaSlices :(")
    pizzaSlices++
    println("Pizza slices: $pizzaSlices :(")
    pizzaSlices++
    println("There are $pizzaSlices slices of pizza. We have a whole pizza :)")

    // --- REFACTORIZACIÓN 1: Usando un bucle while ---
    // Reinicia el contador a 0.
    pizzaSlices = 0
    // Cuenta y muestra las primeras 7 porciones (con cara triste).
    while(pizzaSlices < 7) {
        pizzaSlices++
        println("Pizza slices: $pizzaSlices :(")
    }
    // Añade la octava porción fuera del bucle para mostrar el mensaje de éxito.
    pizzaSlices++
    println("There are $pizzaSlices slices of pizza. We have a whole pizza :)")

    // --- REFACTORIZACIÓN 2: Usando un bucle do-while ---
    // Reinicia el contador a 0.
    pizzaSlices = 0
    pizzaSlices++ // Se incrementa la primera porción antes de entrar al bucle.
    // El bucle imprime el estado y suma porciones hasta llegar a 8.
    do {
        println("Pizza slices: $pizzaSlices :(")
        pizzaSlices++
    } while(pizzaSlices < 8)
    // Al salir del bucle (cuando ya son 8), se imprime el mensaje final de éxito.
    println("There are $pizzaSlices slices of pizza. We have a whole pizza :)")

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Write a program that simulates the Fizz buzz game. Your task is to print numbers from 1 to 100 incrementally, replacing any number divisible by three with the word "fizz", and any number divisible by five with the word "buzz". Any number divisible by both 3 and 5 must be replaced with the word "fizzbuzz".
    
    // Bucle 'for' que itera sobre los números del 1 al 100.
    for (number in 1..100) {
        // La función println imprimirá el resultado devuelto por el bloque 'when'.
        println(
            // La estructura 'when' (similar a switch o múltiples if-else) evalúa las condiciones de arriba a abajo.
            when {
                // Si el número es divisible por 15 (múltiplo de 3 y de 5 al mismo tiempo), devuelve "fizzbuzz".
                number % 15 == 0 -> "fizzbuzz"
                // Si solo es divisible por 3, devuelve "fizz".
                number % 3 == 0 -> "fizz"
                // Si solo es divisible por 5, devuelve "buzz".
                number % 5 == 0 -> "buzz"
                // Si no cumple ninguna de las condiciones anteriores, devuelve el número original como texto.
                else -> "$number"
            }
        )
    }

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // You have a list of words. Use for and if to print only the words that start with the letter l.
    
    // Crea una lista inmutable con diferentes palabras.
    val words = listOf("dinosaur", "limousine", "magazine", "language")
    // Recorre cada palabra dentro de la lista 'words'.
    for (word in words) {
        // Comprueba si la palabra actual empieza por la letra "l".
        if (word.startsWith("l")) {
            // Solo si la condición es verdadera, se imprime la palabra.
            println(word)
        }
    }
}