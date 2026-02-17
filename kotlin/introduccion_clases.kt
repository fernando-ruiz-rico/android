class Contact(val id: Int, var email: String) {
    fun printId() {
        println("Id: $id")
    }
}

data class User(val name: String, val id: Int)

// Define a data class Employee with two properties: one for a name, and another for a salary. Make sure that the property for salary is mutable, otherwise you won't get a salary boost at the end of the year! The main function demonstrates how you can use this data class.
data class Employee(val name: String, var salary: Int)

fun main() {
    val contact = Contact(1, "mary@gmail.com")
    println(contact)

    println(contact.id)
    println(contact.email)
    contact.printId()

    contact.email = "jane@gmail.com"
    println(contact.id)
    println(contact.email)
    contact.printId()

    val user = User("Alex", 1)
    println(user)
    val secondUser = User("Alex", 1)
    println(secondUser)
    val thirdUser = User("Max", 2)
    println(thirdUser)

    println("user == secondUser ? ${user == secondUser}")
    println("user == thirdUser ? ${user == thirdUser}")

    println(user.copy())
    println(user.copy("Max"))
    println(user.copy(name = "Max"))
    println(user.copy(id = 3))

    val emp = Employee("Mary", 1221)
    println(emp)
    emp.salary += 10
    println(emp)

    val emp2 = Employee("Boss", 3000)
    println(emp2)
    emp2.salary += 200
    println(emp2)
}