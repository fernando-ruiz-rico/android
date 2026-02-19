/**
 * Este código cubre los conceptos más básicos y fundamentales de Kotlin:
 * - Imprimir mensajes en la consola (con y sin saltos de línea).
 * - Declaración de variables de solo lectura (val) y variables mutables (var).
 * - Uso de plantillas de cadenas (String templates) para incrustar valores y operaciones matemáticas dentro de textos.
 */
fun main() {
    // Sin saltos de línea
    // La función 'print' muestra el texto tal cual. Como no añade un salto al final, 
    // el siguiente 'print' escribirá su mensaje justo a continuación en la misma línea.
    print("Hello, world!")
    print("Hello, world!")

    // Con saltos de línea
    // El carácter especial '\n' representa un salto de línea manual dentro del texto.
    print("Hello, world!\n")
    print("Hello, world!\n")

    // 'println' (print line) es la forma recomendada en Kotlin para imprimir. 
    // Escribe el mensaje y automáticamente añade un salto de línea al final.
    println("Hello, world!")
    println("Hello, world!")

    // Constantes
    // 'val' (de "value") crea una variable de solo lectura o inmutable (como una constante). 
    // Una vez que le asignas un valor (5), no puedes cambiarlo más adelante.
    val popcorn = 5
    println(popcorn)
    val hotdog = 7
    println(hotdog)

    // Variables
    // 'var' (de "variable") crea una variable mutable. Su valor puede cambiar durante la ejecución del programa.
    var customers = 10
    println(customers)
    // Reasignamos el valor de la variable de 10 a 8. Esto está permitido porque usamos 'var'.
    customers = 8
    println(customers)

    // Plantillas de cadenas
    // Usando el símbolo '$' puedes insertar el valor de una variable directamente dentro de un texto.
    println("There are $customers customers")
    
    // Si necesitas hacer una operación (como una suma) o llamar a una función dentro del texto, 
    // usas el símbolo '$' seguido de llaves '{}'.
    println("There are ${customers + 1} customers")

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Complete the code to make the program print "Mary is 20 years old" to standard output
    
    // Crea dos variables de solo lectura con los datos.
    val name = "Mary"
    val age = 20
    
    // Utiliza la plantilla de cadenas con '$' para incrustar ambas variables en la frase final.
    println("$name is $age years old")
}