import java.util.Scanner;

public class InstruccionesControl {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int age;

        System.out.print("Enter your age: ");
        age = sc.nextInt();

        if (age >= 18)
        {
            System.out.println("You are old enough");
        }
        else {
            System.out.println("You are not adult yet");
        }

        if (age == 18) {
            System.out.println("You are 18");
        }

        if (age >= 18 && age <= 30) {
            System.out.println("You are between 18 and 30");
        }

        switch(age) {
            case 10:
                System.out.println("You are 10");
                break;
            case 20:
                System.out.println("You are 20");
                break;
            case 30:
                System.out.println("You are 30");
                break;
            default:
                System.out.println("You are not 10, 20, 30");
        }

        // Program that asks the user to enter 3 marks, and then it
        // determines if all, none of some of them are greater or equal than 5
        int mark1, mark2, mark3;

        System.out.println("Enter 3 marks: ");
        mark1 = sc.nextInt();
        mark2 = sc.nextInt();
        mark3 = sc.nextInt();

        if (mark1 >= 5 && mark2 >=5 && mark3 >= 5) {
            System.out.println("All the marks are greater than or equals to 5");
        }
        else if (mark1 < 5 && mark2 < 5 && mark3 < 5) {
            System.out.println("None of the marks are greater than or equals to 5");
        }
        else {
            System.out.println("Some of the marks are greater than or equals to 5");
        }

        sc.close();
    }
}