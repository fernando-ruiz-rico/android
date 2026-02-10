import java.util.Scanner;

public class InstruccionesControl {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int age;

        // Entrada de datos básica
        System.out.print("Enter your age: ");
        age = sc.nextInt();

        // ==========================================
        // Estructura IF-ELSE
        // ==========================================
        // El programa toma UN camino u OTRO, nunca los dos.
        
        System.out.println("--- Condicional If-Else ---");
        if (age >= 18) // Condición: Mayor o igual a 18
        {
            System.out.println("You are old enough"); // Se ejecuta si es VERDADERO
        }
        else {
            System.out.println("You are not adult yet"); // Se ejecuta si es FALSO
        }

        // ==========================================
        // Comparación de Igualdad (==)
        // ==========================================
        // Un solo igual (=) sirve para ASIGNAR valor (guardar dato).
        // Dos iguales (==) sirven para COMPARAR (preguntar).
        
        if (age == 18) {
            System.out.println("You are 18");
        }

        // ==========================================
        // Operadores Lógicos (&& - AND)
        // ==========================================
        // Aquí preguntamos por un RANGO.
        // Para que entre aquí, se deben cumplir LAS DOS condiciones a la vez.
        // (Mayor o igual que 18 Y TAMBIÉN menor o igual que 30).
        
        if (age >= 18 && age <= 30) {
            System.out.println("You are between 18 and 30");
        }

        // ==========================================
        // Estructura SWITCH
        // ==========================================
        // Se usa para evaluar casos específicos (10, 20, 30...)
        
        System.out.println("--- Estructura Switch ---");
        switch(age) {
            case 10: // En el caso de que age valga exactamente 10...
                System.out.println("You are 10");
                break; // El 'break' sale del switch. Si lo quitas, ejecutará también el caso 20.
            case 20:
                System.out.println("You are 20");
                break;
            case 30:
                System.out.println("You are 30");
                break;
            default: // Es como el "else" del switch. Se ejecuta si no coincide con ningún caso anterior.
                System.out.println("You are not 10, 20, 30");
        }

        // ==========================================
        // EJERCICIO: Lógica de Notas
        // ==========================================
        // Programa que pide 3 notas y determina:
        // - Si TODAS son aprobadas (>= 5)
        // - Si NINGUNA es aprobada (< 5)
        // - Si ALGUNAS son aprobadas (el resto de casos)
        
        int mark1, mark2, mark3;

        System.out.println("--- Ejercicio Notas ---");
        System.out.println("Enter 3 marks: ");
        mark1 = sc.nextInt();
        mark2 = sc.nextInt();
        mark3 = sc.nextInt();

        // Estructura IF - ELSE IF - ELSE
        // El programa evalúa en orden. En cuanto encuentra una verdad, entra y SE SALTA el resto.

        // CASO 1: TODAS APROBADAS
        // Usamos && (AND) porque necesitamos que la 1, la 2 Y la 3 cumplan la condición.
        if (mark1 >= 5 && mark2 >=5 && mark3 >= 5) {
            System.out.println("All the marks are greater than or equals to 5");
        }
        // CASO 2: NINGUNA APROBADA (Todas suspensas)
        // Si no se cumplió lo de arriba, preguntamos esto.
        // Todas deben ser menores que 5.
        else if (mark1 < 5 && mark2 < 5 && mark3 < 5) {
            System.out.println("None of the marks are greater than or equals to 5");
        }
        // CASO 3: CASO MIXTO
        // Si no son todas aprobadas, ni todas suspensas... por descarte, hay mezcla.
        // No hace falta poner condición, el 'else' atrapa "todo lo demás".
        else {
            System.out.println("Some of the marks are greater than or equals to 5");
        }

        sc.close();
    }
}