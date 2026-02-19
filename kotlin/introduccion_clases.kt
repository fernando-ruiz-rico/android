/**
 * Este código demuestra los conceptos básicos de Programación Orientada a Objetos en Kotlin:
 * - Creación de clases estándar y propiedades (val para inmutables, var para mutables).
 * - Creación de métodos (funciones dentro de una clase).
 * - Uso de 'data classes' (clases de datos), que están optimizadas para almacenar información
 * y generan automáticamente funciones muy útiles como toString(), equals() y copy().
 */

// Define una clase estándar llamada 'Contact'. 
// Tiene un constructor primario con 'id' (solo lectura/inmutable) y 'email' (mutable).
class Contact(val id: Int, var email: String) {
    // Función miembro (o método) de la clase que imprime el ID del contacto.
    fun printId() {
        println("Id: $id")
    }
}

// Define una 'data class' llamada 'User'. 
// Al ser una data class, Kotlin genera automáticamente representaciones legibles de texto, comparaciones lógicas, etc.
data class User(val name: String, val id: Int)

// EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
// Define a data class Employee with two properties: one for a name, and another for a salary. Make sure that the property for salary is mutable, otherwise you won't get a salary boost at the end of the year! The main function demonstrates how you can use this data class.

// Define la 'data class' Employee. El nombre es inmutable (val) pero el salario debe poder cambiar (var).
data class Employee(val name: String, var salary: Int)

fun main() {
    // Crea una instancia de la clase normal 'Contact'.
    val contact = Contact(1, "mary@gmail.com")
    
    // Al imprimir una clase estándar directamente, se mostrará su referencia en memoria (ej. Contact@5a07e868).
    println(contact)

    // Accede e imprime las propiedades individuales del objeto.
    println(contact.id)
    println(contact.email)
    // Llama al método definido dentro de la clase.
    contact.printId()

    // Modifica la propiedad 'email' (esto es posible porque se definió como 'var').
    contact.email = "jane@gmail.com"
    // Comprobamos que el ID sigue igual, pero el email ha cambiado.
    println(contact.id)
    println(contact.email)
    contact.printId()

    // Crea varias instancias de la 'data class' User.
    val user = User("Alex", 1)
    // Al imprimir una data class, muestra una cadena legible automáticamente (ej. User(name=Alex, id=1)).
    println(user)
    
    val secondUser = User("Alex", 1)
    println(secondUser)
    
    val thirdUser = User("Max", 2)
    println(thirdUser)

    // Compara si el contenido de los objetos es igual usando '=='.
    // En las data classes, '==' compara los datos exactos que contienen, no su referencia en memoria.
    // Como 'user' y 'secondUser' tienen los mismos datos ("Alex" y 1), esto devolverá 'true'.
    println("user == secondUser ? ${user == secondUser}")
    
    // Como 'user' y 'thirdUser' tienen datos diferentes, devolverá 'false'.
    println("user == thirdUser ? ${user == thirdUser}")

    // La función copy() es exclusiva de las data classes. Permite clonar un objeto.
    // Aquí crea una copia exacta de 'user'.
    println(user.copy())
    // Crea una copia pero cambia la primera propiedad (name) por "Max".
    println(user.copy("Max"))
    // Hace lo mismo que arriba, pero usando "argumentos nombrados" (recomendado para mayor claridad).
    println(user.copy(name = "Max"))
    // Crea una copia de 'user' ("Alex") pero le cambia la propiedad 'id' a 3.
    println(user.copy(id = 3))

    // Prueba de la clase Employee del ejercicio.
    val emp = Employee("Mary", 1221)
    // Imprime los datos iniciales de Mary.
    println(emp)
    // Incrementa el salario de Mary en 10 (posible gracias a 'var').
    emp.salary += 10
    // Imprime los datos con el salario actualizado.
    println(emp)

    // Hace lo mismo para otro empleado ("Boss").
    val emp2 = Employee("Boss", 3000)
    println(emp2)
    emp2.salary += 200
    println(emp2)
}