import java.util.Scanner;

public class EntradaSalidaBasica {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter a number: ");
        int a = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter a string: ");
        String text = sc.nextLine();

        System.out.println(a);
        System.out.println(text);

        // Create a program that will ask the user to enter the radius of a circle,
        // and it will output the area of the circle (PI * radius * radius).
        // This area will be printed with two decimal digits.
        float radius, area;

        System.out.print("Enter circle radius: ");
        radius = sc.nextFloat();
        area = 3.14f * radius * radius;
        System.out.printf("Area = %.2f", area);

        sc.close();
    }
}