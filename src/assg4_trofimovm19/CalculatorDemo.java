package assg4_trofimovm19;

import java.util.Scanner;

public class CalculatorDemo {

    public static void main(String[] args) {
        //String in = " (2.3+10)*2.0 -5";
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter infix expression: ");
        String in = scanner.nextLine();

        Calculator calculator = new Calculator(in);
        System.out.println("Infix: " + calculator.toString());
        System.out.println("Postfix: " + calculator.getPostfix());
        System.out.println("Calculation result: " + calculator.evaluate());
    }
}
