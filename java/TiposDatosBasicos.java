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
    }
}