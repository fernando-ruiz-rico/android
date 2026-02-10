import java.util.Scanner; 

public class EntradaSalidaBasica {
    public static void main(String[] args) {

        // Creamos el objeto Scanner para la entrada estándar (teclado)
        Scanner sc = new Scanner(System.in);

        // Lectura de números enteros
        // Usamos 'print' en lugar de 'println' para que el cursor se quede en la misma línea
        System.out.print("Introduce un número: "); 
        int a = sc.nextInt(); // Lee el número, PERO deja el "Enter" pulsado en la memoria

        // ==========================================
        // ¡ATENCIÓN: LIMPIEZA DE BUFFER!
        // ==========================================
        // Cuando escribes un número y pulsas ENTER, el número se guarda en 'a',
        // pero el salto de línea (Enter) se queda en el "buffer".
        // La siguiente instrucción (nextLine) sirve para quitar ese Enter
        sc.nextLine(); 

        // Lectura de texto (String)
        System.out.print("Introduce una cadena de texto: ");
        String text = sc.nextLine(); // Ahora sí, lee el texto completo hasta el próximo Enter.

        // Salida básica de datos
        System.out.println("Número leído: " + a);
        System.out.println("Texto leído: " + text);

        // ==========================================
        // EJERCICIO: CÁLCULO DE ÁREA
        // ==========================================
        // Programa que pide el radio de un círculo y calcula su área.
        // Fórmula: Área = PI * radio * radio
        // Requisito: Mostrar el resultado con 2 decimales.
        
        float radius, area; // Usamos 'float' para números con decimales

        System.out.print("Introduce el radio del círculo: ");
        
        // Nota: Si tu sistema está en español, quizás debas introducir la coma (,) en vez del punto (.)
        // Ejemplo: 2,5 en lugar de 2.5, depende de la configuración regional
        radius = sc.nextFloat(); 

        // Calculamos el área. 
        // La 'f' fuerza a que el número sea tratado como 'float'.
        area = 3.14f * radius * radius;
        
        // Salida con formato (printf)
        // printf = Print Formatted
        // Estructura: "Texto con huecos %", variable1, variable2...
        // %.2f significa: "Aquí va un número flotante (f) y quiero que muestres solo 2 decimales (.2)"
        // \n significa: Salto de línea
        System.out.printf("El área es = %.2f\n", area);

        sc.close(); // Cerramos el scanner para liberar recursos del sistema
    }
}