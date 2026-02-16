fun main() {
    // Colecciones
    val readOnlyShapes = listOf("triangle", "square", "circle")
    // readOnlyShapes.add("rectangle") // Error
    println(readOnlyShapes)

    val shapes = mutableListOf("triangle", "square", "circle")
    shapes.add("rectangle")
    println(shapes)

    println("The first item in the list is: ${shapes[0]}")
    println("The first item in the list is: ${shapes.first()}")
    println("The last item in the list is: ${shapes.last()}")
    println("The list has: ${shapes.count()} items")
    print("circle is in shapes? ")
    println("Circle".lowercase() in shapes)

    shapes.add("rectangle")
    println(shapes)

    val readOnlyFruit = setOf("apple", "banana", "cherry", "cherry")
    // readOnlyFruit.add("dragonfruit") // Error
    println(readOnlyFruit)

    val mutableSetOf = mutableSetOf("apple", "banana", "cherry", "cherry")
    mutableSetOf.add("dragonfruit") // Error
    println(mutableSetOf)

    val readOnlyJuiceMenu = mapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
    // readOnlyJuiceMenu["coconut"] = 150 // Error
    println(readOnlyJuiceMenu)

    val juiceMenu = mutableMapOf("apple" to 100, "kiwi" to 190, "orange" to 100)
    juiceMenu["coconut"] = 150
    println(juiceMenu)

    // You have a list of “green” numbers and a list of “red” numbers. Complete the code to print how many numbers there are in total.
    val greenNumbers = listOf(1, 4, 23)
    println(greenNumbers)
    val redNumbers = listOf(17, 2)
    println(redNumbers)

    val totalCount = greenNumbers.count() + redNumbers.count()
    println(totalCount)

    // You have a set of protocols supported by your server. A user requests to use a particular protocol. Complete the program to check whether the requested protocol is supported or not (isSupported must be a Boolean value).
    val SUPPORTED = setOf("HTTP", "HTTPS", "FTP", "SMTP")
    println(SUPPORTED)
    val requested = "smtp"
    val isSupported = requested.uppercase() in SUPPORTED
    println("Support for $requested: $isSupported")

    // Define a map that relates integer numbers from 1 to 3 to their corresponding spelling. Use this map to spell the given number.
    val number2word = mapOf(1 to "one", 2 to "two", 3 to "three")
    println(number2word)
    val n = 2
    println("$n is spelt as '${number2word[n]}'")

    // Create a mutable list called inventory with: "Sword", "Shield", "Potion". Add a "Bow". Remove the "Potion". Finally, print the inventory and how many items it has using .count().
    val inventory = mutableListOf("Sword", "Shield", "Potion")
    println(inventory)
    inventory.add("Bow")
    println(inventory)
    inventory.remove("Potion")
    println(inventory)
    println("You have ${inventory.count()} items")
}