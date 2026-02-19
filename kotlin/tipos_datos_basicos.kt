/**
 * Este código nos enseña dos cosas fundamentales:
 * 1. Cómo modificar el valor de variables numéricas usando operadores de asignación compuesta (atajos matemáticos).
 * 2. La diferencia entre la "inferencia de tipos" (cuando Kotlin adivina el tipo) 
 * y la "declaración explícita" de los tipos de datos básicos (Int, Double, Boolean, etc.).
 */
fun main() {
    // Declaramos una variable mutable (var) e iniciamos su valor en 10.
    var customers = 10

    // Reasignamos su valor a 8.
    customers = 8

    // Suma tradicional: cogemos el valor actual (8), le sumamos 3, y guardamos el resultado (11) en la misma variable.
    customers = customers + 3
    
    // Operador de asignación compuesta (+=): Es exactamente lo mismo que la línea anterior, pero más corto.
    // Suma 3 al valor actual (11 + 3 = 14).
    customers += 3
    
    // Resta 3 al valor actual (14 - 3 = 11).
    customers -= 3
    
    // Multiplica el valor actual por 2 (11 * 2 = 22).
    customers *= 2
    
    // Divide el valor actual entre 2 (22 / 2 = 11).
    customers /= 2

    // Imprime el resultado final de todas esas operaciones.
    println(customers)

    // INFERENCIA DE TIPOS:
    // Kotlin es inteligente. Si pones un 3, sabe automáticamente que 'x' es de tipo Int (entero).
    val x = 3
    // Si pones texto entre comillas dobles, sabe automáticamente que 'y' es de tipo String (cadena de texto).
    val y = "hello world"

    println(x)
    println(y)

    // EJERCICIO PROPUESTO (Tutorial oficial de Kotlin):
    // Explicitly declare the correct type for each variable
    
    // DECLARACIÓN EXPLÍCITA:
    // A veces queremos o necesitamos decirle a Kotlin exactamente qué tipo de dato estamos creando.
    // Para ello, ponemos dos puntos ':' después del nombre de la variable seguido del tipo.
    
    // 'Int' es para números enteros estándar.
    val a:Int = 1000
    // 'String' es para cadenas de texto (siempre entre comillas dobles "").
    val b:String = "log message"
    // 'Double' es para números con decimales de alta precisión.
    val c:Double = 3.14
    // 'Long' es para números enteros gigantes que no cabrían en un 'Int'. 
    // Nota pro: Kotlin permite usar guiones bajos (_) para separar los miles y que sea más fácil de leer.
    val d:Long = 100_000_000_000_000
    // 'Boolean' es para valores lógicos: solo puede ser 'true' (verdadero) o 'false' (falso).
    val e:Boolean = false
    // 'Char' es para un único carácter aislado (siempre entre comillas simples '').
    val f:Char = 'a'

    // Imprimimos todas las variables juntas en una sola línea usando una plantilla de cadena.
    print("$a , $b , $c , $d , $e , $f")
}