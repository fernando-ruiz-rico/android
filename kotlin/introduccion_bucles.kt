fun main() {
    // 1..5
    for (number in 1..5) {
        print(number)
    }
    println()

    val cakes = listOf("carrot", "cheese", "chocolate")
    for (cake in cakes) {
        println("It's a $cake cake")
    }

    var cakesEaten = 0
    while (cakesEaten < 3) {
        println("Eat a cake")
        cakesEaten++
    }

    var cakesBaked = 0
    do {
        println("Bake a cake")
        cakesBaked++
    } while(cakesBaked < cakesEaten)

    // You have a program that counts pizza slices until there's a whole pizza with 8 slices. Refactor this program in two ways: Use a while loop. Use a do-while loop.
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

    pizzaSlices = 0
    while(pizzaSlices < 7) {
        pizzaSlices++
        println("Pizza slices: $pizzaSlices :(")
    }
    pizzaSlices++
    println("There are $pizzaSlices slices of pizza. We have a whole pizza :)")

    pizzaSlices = 0
    pizzaSlices++
    do {
        println("Pizza slices: $pizzaSlices :(")
        pizzaSlices++
    } while(pizzaSlices < 8)
    println("There are $pizzaSlices slices of pizza. We have a whole pizza :)")

    // Write a program that simulates the Fizz buzz game. Your task is to print numbers from 1 to 100 incrementally, replacing any number divisible by three with the word "fizz", and any number divisible by five with the word "buzz". Any number divisible by both 3 and 5 must be replaced with the word "fizzbuzz".
    for (number in 1..100) {
        println(
            when {
                number % 15 == 0 -> "fizzbuzz"
                number % 3 == 0 -> "fizz"
                number % 5 == 0 -> "buzz"
                else -> "$number"
            }
        )
    }

    // You have a list of words. Use for and if to print only the words that start with the letter l.
    val words = listOf("dinosaur", "limousine", "magazine", "language")
    for (word in words) {
        if (word.startsWith("l")) {
            println(word)
        }
    }
}