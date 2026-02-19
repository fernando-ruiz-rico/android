/**
 * Este código se centra en la creación y uso de Funciones en Kotlin:
 * - Declaración de funciones básicas, con parámetros y con valores de retorno.
 * - Funciones de una sola expresión (Single-expression functions) para ahorrar código.
 * - Uso de parámetros con valores por defecto.
 * - Llamadas a funciones utilizando argumentos nombrados (named arguments) para mayor claridad.
 */

// Importa la constante PI desde la librería matemática de Kotlin.
import kotlin.math.PI

// Función básica que no recibe parámetros y no devuelve ningún valor (por defecto devuelve 'Unit', similar a 'void' en otros lenguajes).
fun hello() {
    println("Hello world!")
    println("Hello kotlin!")
}

// Función que recibe dos parámetros enteros (a y b) y especifica explícitamente que devolverá un entero (: Int).
fun sum(a: Int, b: Int): Int {
    // La palabra clave 'return' devuelve el resultado de la suma y sale de la función.
    return a + b
}

// Función de una sola expresión. Hace lo mismo que 'sum', pero como el cuerpo es una única operación, 
// podemos omitir las llaves {} y el 'return', usando directamente el símbolo '='. 
// Kotlin infiere automáticamente que el tipo de retorno es Int.
fun sum2(a: Int, b: Int) = a + b

// Función con un parámetro obligatorio (message) y un parámetro opcional con valor por defecto (prefix = "Info").
fun printMessageWithPrefix(message: String, prefix: String = "Info") {
    println("[$prefix] $message")
}

// Variables globales (fuera de cualquier función) simulando una base de datos sencilla.
val registeredUsernames = mutableListOf("john_doe", "jane_smith")
var registeredEmails = mutableListOf("john@example.com", "jane@example.com")

// Función que recibe un usuario y un email, y devuelve un mensaje de tipo String.
fun registerUser(username: String, email: String): String {
    // Comprueba si el usuario ya existe en la lista.
    if (username in registeredUsernames) {
        // "Early return" (Retorno anticipado): Si entra aquí, devuelve este error y la función termina inmediatamente.
        return "Username already taken. Please choose a different username."
    }

    // Hace la misma comprobación para el correo.
    if (email in registeredEmails) {
        return "Email already exists. Please choose a different email."
    }

    // Si ha pasado las comprobaciones anteriores, añade los datos a las listas mutables.
    registeredUsernames.add(username)
    registeredEmails.add(email)
    return "User registered successfully: $username"
}

// EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
// Write a function called circleArea that takes the radius of a circle in integer format as a parameter and outputs the area of that circle.

// Calcula el área de un círculo usando la fórmula matemática $A = \pi \cdot r^2$.
// Recibe un Int pero devuelve un Double porque PI tiene decimales.
fun circleArea(radius: Int): Double {
    return PI * radius * radius
}

// EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
// Rewrite the circleArea function from the previous exercise as a single-expression function.

// La misma función de arriba, pero simplificada a una sola expresión con '='.
fun circleArea2(radius: Int): Double = PI * radius * radius

// EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
// You have a function that translates a time interval given in hours, minutes, and seconds into seconds. In most cases, you need to pass only one or two function parameters while the rest are equal to 0. Improve the function and the code that calls it by using default parameter values and named arguments so that the code is easier to read.

// Se asigna el valor por defecto de 0 a todos los parámetros.
// Así, si el usuario no proporciona las horas o los minutos al llamar a la función, no dará error.
fun intervalInSeconds(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Int {
    return (((hours * 60) + minutes) * 60) + seconds
}

// EJERCICIO PROPUESTO:
// Create a function named personalizedGreeting that takes three parameters: name (String), age (Int), and city (String).
// Assign a default value of "Madrid" to the city parameter.
// The function should print a message like: "Hello, my name is [name], I am [age] years old and I live in [city]."

// El parámetro 'city' tiene "Madrid" como valor por defecto.
fun personalizedGreeting(name:String, age:Int, city:String = "Madrid") {
    println("Hello, my name is $name and I am $age years old and I live in $city")
}

// Función principal donde se ejecutan y prueban todas las funciones declaradas arriba.
fun main() {
    // Llama a la función hello() tres veces.
    hello()
    hello()
    hello()

    // Imprime el resultado de llamar a las funciones de suma.
    println(sum(1, 2))
    println(sum(3, 4))
    println(sum2(3, 4))

    // Llama a la función usando el orden posicional normal (el primer string es message, el segundo es prefix).
    printMessageWithPrefix("Hello", "Log")
    // Uso de "Argumentos Nombrados" (Named arguments). Al especificar el nombre del parámetro (prefix = ...), 
    // podemos cambiar el orden en el que se los enviamos a la función.
    printMessageWithPrefix(prefix = "Log", message = "Hello")
    // Al enviar solo un argumento, 'prefix' toma su valor por defecto ("Info").
    printMessageWithPrefix("Hello")

    // Pruebas del sistema de registro de usuarios.
    println(registeredUsernames)
    println(registeredEmails)
    // Falla porque "john_doe" ya existe en la lista inicial.
    println(registerUser("john_doe", "newjohn@example.com"))
    println(registeredUsernames)
    println(registeredEmails)
    // Funciona correctamente porque ambos datos son nuevos.
    println(registerUser("new_user", "newuser@example.com"))
    println(registeredUsernames)
    println(registeredEmails)

    // Pruebas de las funciones matemáticas para el área del círculo.
    println(circleArea(2))
    println(circleArea(3))

    println(circleArea2(2))
    println(circleArea2(3))

    // Pruebas del cálculo de intervalos de tiempo.
    // Usando argumentos posicionales normales (horas, minutos, segundos).
    println(intervalInSeconds(1, 1, 1))
    println(intervalInSeconds(1, 2, 1))
    // Al enviar solo un parámetro, asume que es el primero (hours) y el resto valen 0.
    println(intervalInSeconds(1))

    // Envía horas y minutos por posición, los segundos valen 0.
    println(intervalInSeconds(0, 1))
    // Usando argumentos nombrados, enviamos solo los minutos. Horas y segundos valdrán 0.
    println(intervalInSeconds(minutes = 1))
    // Usando argumentos nombrados, podemos omitir las horas y enviarlo en el orden que queramos.
    println(intervalInSeconds(seconds = 2, minutes = 1))
    println(intervalInSeconds(hours = 1, seconds = 2))

    // Pruebas del saludo personalizado.
    // Solo enviamos nombre y edad. 'city' toma el valor "Madrid" automáticamente.
    personalizedGreeting("John", 50)
    // Enviamos los tres parámetros en orden posicional.
    personalizedGreeting("Fernando", 50, "Alicante")
    // Enviamos los tres parámetros desordenados usando argumentos nombrados.
    personalizedGreeting(city = "Valencia", age = 40, name = "Peter")
}