fun pedirTextoValido(mensaje: String) : String {
    while(true) {
        print(mensaje)
        val entrada = readln().trim()
        if (entrada.isNotEmpty()) return entrada
        println("Error: El campo no puede estar vacío.")
    }
}

fun pedirIdValido() : String {
    while(true) {
        print("ID del Material (ej. LIB-001): ")
        val entrada = readln().trim().uppercase()
        if (entrada.matches(Regex("[A-Z]{3}-[0-9]{3}"))) return entrada
        println("Error: Debes introducir 3 letras, un guión y 3 números.")
    }
}

fun main() {
    println(pedirIdValido())
}