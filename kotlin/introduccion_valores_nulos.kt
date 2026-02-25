fun describeString(maybeString: String?): String {
    if (maybeString != null && maybeString.length > 0) {
        return "String of length ${maybeString.length}"
    }
    else {
        return "Empth or null string"
    }
}

fun lengthOfString(maybeString: String?): Int? = maybeString?.length

fun main() {
    var neverNull: String = "This can't be null"
    println(neverNull)

    // neverNull = null // Error

    var nullable: String? = "You can keep a null here"
    println(nullable)

    nullable = null

    var inferredNonNull = "This compiler assumes non-nullable"
    println(inferredNonNull)

    // inferredNonNull = null // Error

    val nullString: String? = null
    println(describeString(nullString))

    val nullString2: String? = "0123456789"
    println(describeString(nullString2))

    println(lengthOfString(nullString))
    println(lengthOfString(nullString2))

    var nullable2: String? = "You can keep a null here"
    println(nullable2?.uppercase())

    nullable2 = null
    println(nullable2?.uppercase())

    println(nullable2?.length) // null
    println(nullable2?.length ?: 0) // 0

    nullable2 = "0123456789"
    println(nullable2?.length ?: 0) // 10

    // You have the employeeById function that gives you access to a database of employees of a company. Unfortunately, this function returns a value of the Employee? type, so the result can be null. Your goal is to write a function that returns the salary of an employee when their id is provided, or 0 if the employee is missing from the database.
    data class Employee(val name: String, val salary: Int)

    fun employeeById(id: Int) = when(id) {
        1 -> Employee("Mary", 1000)
        2 -> null
        3 -> Employee("John", 1100)
        4 -> Employee("Ann", 2000)
        else -> null
    }

    fun salaryById(id: Int) = employeeById(id)?.salary ?: 0

    println(employeeById(1))
    println(employeeById(2))

    println(salaryById(1))
    println(salaryById(2))
}