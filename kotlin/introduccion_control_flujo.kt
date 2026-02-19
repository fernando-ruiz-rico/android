/**
 * Este código se centra en las estructuras de control de flujo condicional en Kotlin:
 * - Uso de 'if' y 'else' tanto como sentencias tradicionales como expresiones (que devuelven un valor).
 * - Uso de 'when' (la versión mejorada de 'switch' en Kotlin), evaluando variables o condiciones.
 * - Importación y uso de utilidades de la biblioteca estándar, como la generación de números aleatorios.
 */

// Importa la clase Random de la biblioteca de Kotlin para poder generar números aleatorios más adelante.
import kotlin.random.Random

fun main() {
    // Declara la variable 'd' como un número entero, pero no le asigna un valor inicial.
    val d: Int
    // Variable booleana que servirá como condición.
    val check = true

    // Estructura if-else tradicional.
    if (check) {
        // Como 'check' es true, 'd' se inicializa con el valor 1.
        d = 1
    } else {
        d = 2
    }
    // Imprime el valor final de 'd' (que será 1).
    println(d)

    val a = 1
    val b = 2
    // En Kotlin, 'if' puede usarse como una "expresión" que devuelve un valor (como el operador ternario en otros lenguajes).
    // Aquí evalúa si 'a > b'. Como es falso, devuelve 'b' y la función println lo imprime directamente (imprime 2).
    println(if (a > b) a else b)

    val obj = "Hello"
    println(obj)

    // Estructura 'when' utilizada como una sentencia (solo ejecuta código, no devuelve nada a una variable).
    // Es el equivalente al 'switch' de otros lenguajes, pero más potente y sin necesidad de usar 'break'.
    when (obj) {
        "1" -> println("One") // Si 'obj' es "1", imprime "One"
        "Hello" -> println("Greeting") // Como 'obj' es "Hello", esta es la línea que se ejecuta.
        else -> println("Unknown") // El 'else' actúa como el 'default', ejecutándose si nada de lo anterior coincide.
    }

    // Estructura 'when' utilizada como una expresión. 
    // En lugar de imprimir directamente, el resultado de la rama que coincida se guarda en la variable 'result'.
    val result = when(obj) {
        "1" -> "One"
        "Hello" -> "Greeting" // Esta rama devuelve la cadena "Greeting".
        // Cuando usas 'when' como expresión, el bloque 'else' es OBLIGATORIO para garantizar que siempre se devuelva algo.
        else -> "Unknown" 
    }

    // Imprime el resultado guardado ("Greeting").
    println(result)

    val trafficLightState = "Red"

    // 'when' sin argumentos. En este formato, actúa como una cadena gigante de if-else if.
    // Evalúa expresiones booleanas completas en cada rama de arriba a abajo.
    val trafficAction = when {
        trafficLightState == "Green" -> "Go"
        trafficLightState == "Yellow" -> "Slow down"
        trafficLightState == "Red" -> "Stop" // Esta condición es verdadera, así que devuelve "Stop".
        else -> "Malfunction"
    }

    println(trafficAction)

    // El mismo ejemplo del semáforo, pero usando 'when' CON argumento.
    // Es más limpio cuando solo quieres comprobar la igualdad exacta de una única variable.
    val trafficAction2 = when(trafficLightState) {
        "Green" -> "Go"
        "Yellow" -> "Slow down"
        "Red" -> "Stop" // Coincide exactamente con el valor de la variable.
        else -> "Malfunction"
    }

    println(trafficAction2)

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Create a simple game where you win if throwing two dice results in the same number. Use if to print You win :) if the dice match or You lose :( otherwise.
    
    // Genera un número aleatorio desde 0 hasta 5 (el 6 no está incluido). 
    // Nota: Para simular un dado real de 1 a 6 sería Random.nextInt(1, 7).
    val firstResult = Random.nextInt(6)
    val secondResult = Random.nextInt(6)
    
    // Comprueba si los dos números generados son idénticos.
    if (firstResult == secondResult) {
        println("You win :)") // Ganas si coinciden.
    }
    else {
        println("You lose :(") 
    }

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Using a when expression, update the following program so that it prints the corresponding actions when you input the names of game console buttons.
    val button = "A"

    // Usa 'when' como expresión directamente dentro de la función 'println'.
    println(
        when(button) {
            "A" -> "Yes" // Como el botón es "A", esta rama devuelve "Yes", y eso es lo que se imprime.
            "B" -> "No"
            "X" -> "Menu"
            "Y" -> "Nothing"
            else -> "There is no such option" // Requerido al ser usado como expresión.
        }
    )
}