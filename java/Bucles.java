import java.util.Scanner; // Herramienta necesaria para leer datos del teclado.

public class Bucles {
    public static void main(String[] args) {

        // ==========================================
        // Bucle WHILE 
        // ==========================================
        // CONCEPTO: "Pregunta primero, actúa después".
        // Si la condición es falsa desde el inicio, el código NUNCA se ejecuta.
        
        System.out.println("--- Bucle While (1 a 10) ---");
        int n = 1;
        while(n <= 10) {
            System.out.println(n);
            n++; 
        }

        // ==========================================
        // Bucle WHILE con condición compuesta
        // ==========================================
        // CONCEPTO: Lógica booleana.
        // Usamos '&&' (AND). Para entrar, AMBAS condiciones deben ser verdaderas a la vez.
        
        System.out.println("--- Bucle While Compuesto ---");
        int n2 = 1;
        while(n2 >= 1 && n2 <= 10) { 
            System.out.println(n2);
            n2++;
        }

        // ==========================================
        // Bucle DO-WHILE 
        // ==========================================
        // CONCEPTO: "Actúa primero, pregunta después".
        // Garantiza que el código se ejecute AL MENOS UNA VEZ, sin importar la condición.
        
        System.out.println("--- Bucle Do-While ---");
        int n3 = 1;
        do {
            System.out.println(n3); // Imprime primero...
            n3++;
        } while (n3 <= 10); // ... y verifica la condición al final.

        // ==========================================
        // Bucle FOR
        // ==========================================
        // CONCEPTO: El bucle compacto.
        // Estructura: (Inicio ; Condición para seguir ; Paso a ejecutar al final de cada iteración)
        
        System.out.println("--- Bucle For ---");
        for(int n4 = 1; n4 <= 10; n4++) {
            System.out.println(n4);
        }

        // ==========================================
        // EJERCICIO de acumulador
        // ==========================================
        // Enunciado: Pedir números al usuario hasta que escriba 0.
        // Al final, mostrar la suma de todos los números introducidos.
        
        System.out.println("--- Ejercicio: Suma de números ---");
        int number, sum = 0;

        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce números (escribe 0 para terminar): ");

        do {
            number = sc.nextInt(); // El programa se detiene aquí esperando al usuario
            sum += number; // 'sum += number' es lo mismo que 'sum = sum + number'
        } while(number != 0); 

        System.out.println("La suma total es: " + sum);

        // ==========================================
        // EJERCICIO Avanzado (Validación + Cuenta atrás)
        // ==========================================
        // Enunciado: Pedir un número entre 1 y 100. Si es válido, hacer una cuenta atrás
        // desde ese número hasta 1. El programa se repite hasta que el usuario falla el rango.
        
        System.out.println("--- Ejercicio Avanzado: Validación y Cuenta Regresiva ---");
        
        // Bucle externo (DO-WHILE): Se encarga de repetir la pregunta al usuario
        do {
            System.out.println("Introduce un número para la cuenta atrás (entre 1 y 100): ");
            number = sc.nextInt();

            // Filtro de seguridad: Solo contamos si el número es válido
            if (number >= 1 && number <= 100) {
                
                System.out.println(">> Iniciando cuenta atrás desde " + number + ":");
                
                // Bucle interno (FOR): Se encarga de la cuenta atrás
                // 'i--' significa que restamos 1 en cada iteración (Cuenta descendente)
                for (int i = number; i >= 1; i--) {
                    System.out.println(i);
                }
                System.out.println(">> ¡Cuenta terminada!");
                
            } else {
                System.out.println("Número fuera de rango. Fin del programa.");
            }

        } while(number >= 1 && number <= 100); // Se repite MIENTRAS el usuario siga en el rango correcto

        sc.close(); // Buena práctica: cerrar el Scanner al final del programa
    }
}