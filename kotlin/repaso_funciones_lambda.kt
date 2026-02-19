fun saluda(nombre: String) {
  println("Hola $nombre")
}

fun primeroInverso(cadenas: List<String>): String {
  return reverseStr(cadenas[0])
}

fun reverseStr(cadena: String): String {
  val charList = cadena.toList() // Ej: "hola" -> ["h", "o", "l", "a"]
  return charList.reversed().joinToString("")
}

fun operar(n1: Int, n2: Int, operacion: (Int, Int) -> Int): Int {
  return operacion(n1, n2)
}

data class Persona(var nombre: String, var edad: Int)

fun main() {
  saluda("Pepe")
  val nombres = listOf("Pepe", "Juan", "Marta")
  println(primeroInverso(nombres))

  val reverseMayus = { cadena: String -> cadena.reversed().uppercase() }
  println(reverseMayus("Pepe")) // EPEP

  val sumar = { x: Int, y: Int -> x + y }
  val resultado = operar(6, 8, sumar)
  println(resultado)

  println(operar(6, 8, { x, y -> x * y }))

  println(nombres.map(::reverseStr)) // ::función -> Pasamos la referencia a una función existente (fun)

  println(nombres.map { c -> c.reversed() })
  /**
   * Cuando la función lambda solo recibe 1 parámetro, lo podemos omitir y usar it como nombre de parámetro
   */
  println(nombres.map { it.reversed() })

  val numeros = listOf(4, 7, 12, 24, 19, 3, 6)
  // Suma los números pares
  val sumaPares = numeros.filter { it % 2 == 0 }.sum()
  println(sumaPares) // 46

  val palabras = listOf("abeto", "mesa", "gato", "abanico", "aro", "teclado", "abedul", "mano", "aguacate")
  // Imprime las 3 primeras palabras que empiezan por 'a' en mayúsculas
  println(palabras.filter { it.startsWith("a") }.take(3).map { it.uppercase() })

  // Imprime las longitudes de las cadenas ordenadas de mayor a menor
  println(palabras.map { it.length }.sortedDescending())

  // A partir de una lista de palabras, imprime la concatenación de la primera letra de cada palabra
  // Ejemplo: ["pera", "manzana", "platano"] -> "pmp"
  println(palabras.map { it[0] }.joinToString(""))

  val persona = Persona("Juan", 23)
  println(persona)
  println(persona.nombre) // Juan
  println(persona.edad) // 23

  val personas = listOf<Persona>(
    Persona("Juan", 23),
    Persona("Marta", 41),
    Persona("Pedro", 16),
    Persona("Marco", 62),
    Persona("Ana", 15),
    Persona("Martín", 43),
  )

  // Imprime personas menores de edad
  println(personas.filter { it.edad < 18 })
  // Nombres personas menores de edad
  println(personas.filter { it.edad < 18 }.map { it.nombre })
  // Suma las edades de los 3 primeros
  println(personas.take(3).sumOf { it.edad })
  // Nombres en mayúsculas de las personas que empiezan por M
  println(personas.map { it.nombre }.filter { it.startsWith("M") }.joinToString() )
  // 3 personas más mayores
  println(personas.sortedByDescending { it.edad }.take(3))
}
