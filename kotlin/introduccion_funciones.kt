import kotlin.math.PI

fun hello() {
    println("Hello world!")
    println("Hello kotlin!")
}

fun sum(a: Int, b: Int): Int {
    return a + b
}

fun sum2(a: Int, b: Int) = a + b


fun printMessageWithPrefix(message: String, prefix: String = "Info") {
    println("[$prefix] $message")
}

val registeredUsernames = mutableListOf("john_doe", "jane_smith")
var registeredEmails = mutableListOf("john@example.com", "jane@example.com")

fun registerUser(username: String, email: String): String {
    if (username in registeredUsernames) {
        return "Username already taken. Please choose a different username."
    }

    if (email in registeredEmails) {
        return "Email already exists. Please choose a different email."
    }

    registeredUsernames.add(username)
    registeredEmails.add(email)
    return "User registered successfully: $username"
}

// Write a function called circleArea that takes the radius of a circle in integer format as a parameter and outputs the area of that circle.
fun circleArea(radius: Int): Double {
    return PI * radius * radius
}

// Rewrite the circleArea function from the previous exercise as a single-expression function.
fun circleArea2(radius: Int): Double = PI * radius * radius

// You have a function that translates a time interval given in hours, minutes, and seconds into seconds. In most cases, you need to pass only one or two function parameters while the rest are equal to 0. Improve the function and the code that calls it by using default parameter values and named arguments so that the code is easier to read.
fun intervalInSeconds(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Int {
    return (((hours * 60) + minutes) * 60) + seconds
}

fun main() {
    hello()
    hello()
    hello()

    println(sum(1, 2))
    println(sum(3, 4))
    println(sum2(3, 4))

    printMessageWithPrefix("Hello", "Log")
    printMessageWithPrefix(prefix = "Log", message = "Hello")
    printMessageWithPrefix("Hello")

    println(registeredUsernames)
    println(registeredEmails)
    println(registerUser("john_doe", "newjohn@example.com"))
    println(registeredUsernames)
    println(registeredEmails)
    println(registerUser("new_user", "newuser@example.com"))
    println(registeredUsernames)
    println(registeredEmails)

    println(circleArea(2))
    println(circleArea(3))

    println(circleArea2(2))
    println(circleArea2(3))

    println(intervalInSeconds(1, 1, 1))
    println(intervalInSeconds(1, 2, 1))
    println(intervalInSeconds(1))

    println(intervalInSeconds(0, 1))
    println(intervalInSeconds(minutes = 1))
    println(intervalInSeconds(seconds = 2, minutes = 1))
    println(intervalInSeconds(hours = 1, seconds = 2))
}