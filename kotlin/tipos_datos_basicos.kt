fun main() {
    var customers = 10

    customers = 8

    customers = customers + 3
    customers += 3
    customers -= 3
    customers *= 2
    customers /= 2

    println(customers)

    val x = 3
    val y = "hello world"

    println(x)
    println(y)

    // Explicitly declare the correct type for each variable
    val a:Int = 1000
    val b:String = "log message"
    val c:Double = 3.14
    val d:Long = 100_000_000_000_000
    val e:Boolean = false
    val f:Char = 'a'

    print("$a , $b , $c , $d , $e , $f")
}