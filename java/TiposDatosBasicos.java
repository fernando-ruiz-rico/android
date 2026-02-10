public class TiposDatosBasicos {
    public static void main(String[] args) {

        float pi = 3.1416f;
        int piInteger = (int)pi;
        System.out.println(piInteger);

        int number = 5;
        double realNumber = (double)number;
        System.out.println(realNumber);

        char symbol = 'a';
        System.out.println(symbol);

        String text = "Hello World";
        System.out.println(text);

        char symbol2 = 'a';
        symbol2 += 25;
        System.out.println(symbol2);

        String text2 = "Hello" + 3 + 2;
        System.out.println(text2);

        String text3 = "Hello" + (3 + 2);
        System.out.println(text3);

        float result = (float) 3 / 2;
        System.out.println(result);

        float a = 3.5f;
        int b = 4;
        float result2 = a * b;
        System.out.println(result2);

        int value = Integer.parseInt("023");
        System.out.println(value);

        int number2 = 23;
        String text4 = "" + number2;
        System.out.println(text4);

        int number3 = 23;
        String text5 = String.valueOf(number3);
        System.out.println(text5);

        // Create a program that:
        // Defines two byte variables to store your age and the age of a friend
        // Defines another byte variable to store the addition of both ages (you may need to typecast the result)
        // Defines a float variable to store the average of these ages, including fraction digits
        // Prints the message "The age average is " followed by the average calculated in previous step
        byte age1 = 43, age2 = 39;
        System.out.println(age1);
        System.out.println(age2);
        byte sumAges = (byte)(age1 + age2);
        System.out.println("The sum is " + sumAges);

        float ageAverages = (float)(age1 + age2) / 2;
        System.out.println("The average is " + ageAverages);
    }
}