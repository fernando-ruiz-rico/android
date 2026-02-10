import java.util.Scanner;

public class Bucles {
    public static void main(String[] args) {

        int n = 1;
        while(n <= 10) {
            System.out.println(n);
            n++;
        }

        int n2 = 1;
        while(n2 >= 1 && n2 <= 10) {
            System.out.println(n2);
            n2++;
        }

        int n3 = 1;
        do {
            System.out.println(n3);
            n3++;
        } while (n3 <= 10);

        for(int n4 = 1; n4 <= 10; n4++) {
            System.out.println(n4);
        }

        // Program that asks the user to enter numbers, until he types 0.
        // Then, the program must sum all the numbers entered by the user
        int number, sum = 0;

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter numbers (0 to finish): ");

        do {
            number = sc.nextInt();
            sum += number;
        } while(number != 0);

        System.out.println(sum);

        // Program that asks the user to enter valid numbers between 1 and 100
        // and then counts from these numbers to 1 in descending order
        do {
            System.out.println("Enter number to count from (1 to 100): ");
            number = sc.nextInt();

            if (number >= 1 && number <= 100) {
                for (int i = number; i >= 1; i--) {
                    System.out.println(i);
                }
            }

        } while(number >= 1 && number <= 100);

        sc.close();
    }
}