/**
 * Este código explora las Colecciones en Kotlin: Listas (List), Conjuntos (Set) y Mapas/Diccionarios (Map).
 * 
 * El principal objetivo aquí es observar la diferencia entre colecciones de solo lectura (inmutables)
 * y colecciones mutables (que pueden ser modificadas añadiendo, eliminando o actualizando elementos).
 */
fun main() {
    // Colecciones
    
    // Crea una Lista de solo lectura. En las listas el orden importa y admiten elementos duplicados.
    val readOnlyShapes = listOf("triangle", "square", "circle")
    // readOnlyShapes.add("rectangle") // Error: Una lista 'listOf' no tiene el método add() porque es inmutable.
    println(readOnlyShapes)

    // Crea una Lista mutable, lo que permite modificar su contenido después de su creación.
    val shapes = mutableListOf("triangle", "square", "circle")
    // Añade un nuevo elemento al final de la lista.
    shapes.add("rectangle")
    println(shapes)

    // Accede al primer elemento usando su índice numérico (las listas empiezan a contar desde la posición 0).
    println("The first item in the list is: ${shapes[0]}")
    // Funciones útiles integradas en las colecciones para obtener el primer y último elemento de forma legible.
    println("The first item in the list is: ${shapes.first()}")
    println("The last item in the list is: ${shapes.last()}")
    // Obtiene la cantidad total de elementos dentro de la lista.
    println("The list has: ${shapes.count()} items")
    
    print("circle is in shapes? ")
    // Comprueba si un elemento existe dentro de la lista usando el operador 'in'.
    // Convierte "Circle" a minúsculas ("circle") para que coincida exactamente con la capitalización del elemento de la lista.
    println("Circle".lowercase() in shapes)

    // Añade otro "rectangle". Como mencionamos, las listas permiten guardar elementos duplicados sin problemas.
    shapes.add("rectangle")
    println(shapes)

    // Crea un Set (Conjunto) de solo lectura. Los Sets NO permiten elementos duplicados y no garantizan un orden específico.
    // Fíjate que "cherry" está escrito dos veces en el código, pero el Set filtrará uno y solo guardará tres elementos.
    val readOnlyFruit = setOf("apple", "banana", "cherry", "cherry")
    // readOnlyFruit.add("dragonfruit") // Error: setOf crea un Set inmutable, no se pueden añadir cosas después.
    println(readOnlyFruit)

    // Crea un Set mutable. 
    val fruit = mutableSetOf("apple", "banana", "cherry", "cherry")
    // Al ser un 'mutableSetOf', el método add() es perfectamente válido y añadirá el dragonfruit.
    fruit.add("dragonfruit") 
    // Al imprimir, veremos el Set sin el "cherry" duplicado y con el nuevo "dragonfruit".
    println(fruit)

    // Crea un Map (Mapa o Diccionario) de solo lectura. Asocia una "clave" única con un "valor".
    // La palabra clave 'to' se usa para vincular visualmente la clave (ej. "apple") con su valor (ej. 100).
    val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
    // readOnlyJuiceMenu["coconut"] = 150 // Error: Es un Map inmutable, no se pueden añadir o cambiar los pares clave-valor.
    println(readOnlyJuiceMenu)

    // Crea un Map mutable, permitiendo añadir, eliminar o actualizar los valores asociados a las claves.
    val juiceMenu = mutableMapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
    // Añade un nuevo par clave-valor ("coconut" -> 150) usando la sintaxis de corchetes.
    juiceMenu["coconut"] = 150
    println(juiceMenu)

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // You have a list of “green” numbers and a list of “red” numbers. Complete the code to print how many numbers there are in total.
    
    // Crea dos listas inmutables de números enteros.
    val greenNumbers = listOf(1, 4, 23)
    println(greenNumbers)
    val redNumbers = listOf(17, 2)
    println(redNumbers)

    // Suma la cantidad total de elementos (.count()) de la primera lista con los de la segunda lista.
    val totalCount = greenNumbers.count() + redNumbers.count()
    // Imprime el total (3 + 2 = 5).
    println(totalCount)

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // You have a set of protocols supported by your server. A user requests to use a particular protocol. Complete the program to check whether the requested protocol is supported or not (isSupported must be a Boolean value).
    
    // Crea un Set inmutable (por convención en mayúsculas al ser una constante) con los protocolos soportados.
    val SUPPORTED = setOf("HTTP", "HTTPS", "FTP", "SMTP")
    println(SUPPORTED)
    
    // El usuario solicita el protocolo "smtp" escrito en minúsculas.
    val requested = "smtp"
    // Transforma el texto a mayúsculas ("SMTP") y comprueba si existe dentro del Set usando 'in'. El resultado es un booleano (true).
    val isSupported = requested.uppercase() in SUPPORTED
    println("Support for $requested: $isSupported")

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Define a map that relates integer numbers from 1 to 3 to their corresponding spelling. Use this map to spell the given number.
    
    // Crea un Map inmutable donde las claves son números enteros (Int) y los valores son texto (String).
    val number2word = mapOf(1 to "one", 2 to "two", 3 to "three")
    println(number2word)
    
    val n = 2
    // Accede al valor que corresponde a la clave '2' (guardada en 'n') usando corchetes, lo que devolverá "two".
    println("$n is spelt as '${number2word[n]}'")

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Create a mutable list called inventory with: "Sword", "Shield", "Potion". Add a "Bow". Remove the "Potion". Finally, print the inventory and how many items it has using .count().
    
    // Crea una lista mutable para el inventario del jugador.
    val inventory = mutableListOf("Sword", "Shield", "Potion")
    println(inventory)
    
    // Añade el elemento "Bow" (Arco) al final de la lista.
    inventory.add("Bow")
    println(inventory)
    
    // Busca y elimina el elemento exacto "Potion" (Poción) de la lista.
    inventory.remove("Potion")
    println(inventory)
    
    // Muestra la cantidad total de objetos que quedan en la lista del inventario tras las modificaciones.
    println("You have ${inventory.count()} items")
}