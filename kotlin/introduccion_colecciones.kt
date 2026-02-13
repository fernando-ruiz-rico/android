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
}