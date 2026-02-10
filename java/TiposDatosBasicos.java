public class TiposDatosBasicos {
    public static void main(String[] args) {

        // ==========================================
        // CASTING (Conversión de tipos)
        // ==========================================
        
        // De Float a Int 
        // No redondea, TRUNCA (corta los decimales)
        float pi = 3.1416f;
        int piInteger = (int)pi; 
        System.out.println(piInteger); // Imprime: 3

        // De Int a Double
        // Se puede poner (double) explícito o dejar que Java lo haga solo.
        int number = 5;
        double realNumber = (double)number;
        System.out.println(realNumber); // Imprime: 5.0

        // ==========================================
        // CARACTERES (char)
        // ==========================================
        // Los char usan comillas SIMPLES (' ').
        char symbol = 'a';
        System.out.println(symbol);
        
        // Los String usan comillas DOBLES (" ").
        String text = "Hello World";
        System.out.println(text);

        // Los char son números disfrazados (Código ASCII/Unicode).
        // 'a' vale 97. Si le sumas 25, obtienes 122.
        // El carácter 122 en la tabla ASCII es la letra 'z'.
        char symbol2 = 'a';
        symbol2 += 25; 
        System.out.println(symbol2); // Imprime: z


        // ==========================================
        // CONCATENACIÓN DE STRINGS
        // ==========================================
        
        // Evaluando de izquierda a derecha.
        // 1º: "Hello" + 3 -> "Hello3" (Se convierte en texto)
        // 2º: "Hello3" + 2 -> "Hello32"
        // NO SUMA, concatena los caracteres.
        String text2 = "Hello" + 3 + 2;
        System.out.println(text2); // Imprime: Hello32

        // Los paréntesis fuerzan a hacer la suma matemática PRIMERO
        // 1º: (3 + 2) -> 5
        // 2º: "Hello" + 5 -> "Hello5"
        String text3 = "Hello" + (3 + 2);
        System.out.println(text3); // Imprime: Hello5

        // ==========================================
        // OPERACIONES MATEMÁTICAS Y TIPOS
        // ==========================================

        // División entera vs real
        // Si dividimos 3 / 2 (ambos enteros), Java da 1.
        // Para tener decimales, al menos UNO debe ser float/double
        // (float) 3 convierte el 3 en 3.0, y entonces 3.0 / 2 = 1.5
        float result = (float) 3 / 2;
        System.out.println(result);

        // Promoción de tipos
        // float * int = float
        // En Java siempre gana el tipo más "grande"
        float a = 3.5f;
        int b = 4;
        float result2 = a * b;
        System.out.println(result2);

        // ==========================================
        // Texto a Número
        // ==========================================
        // Convertir un String "023" a un número matemático 23.
        int value = Integer.parseInt("023");
        System.out.println(value);

        // ==========================================
        // Número a Texto
        // ==========================================
        
        // Concatenar con cadena vacía.
        int number2 = 23;
        String text4 = "" + number2;
        System.out.println(text4);

        // Usando String.valueOf()
        int number3 = 23;
        String text5 = String.valueOf(number3);
        System.out.println(text5);

        // ==========================================
        // EJERCICIO: EDADES
        // ==========================================
        // Programa que define dos edades, las suma y calcula la media
        
        System.out.println("--- Ejercicio Edades ---");
        byte age1 = 43, age2 = 39;
        
        System.out.println("Edad 1: " + age1);
        System.out.println("Edad 2: " + age2);
        
        // En Java, al sumar dos bytes (byte + byte), el resultado se convierte automáticamente en int
        // Por eso es OBLIGATORIO hacer el casting (byte) delante para volver a guardarlo en una variable de tipo 'byte'.
        byte sumAges = (byte)(age1 + age2);
        System.out.println("La suma es: " + sumAges);

        // Para la media, casteamos a (float) UNO de los datos
        // Si no lo hiciéramos, dividiría enteros y perderíamos los decimales
        float ageAverages = (float)(age1 + age2) / 2;
        System.out.println("La media es: " + ageAverages);
    }
}