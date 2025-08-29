import java.util.Scanner;

public class ConsoleCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Simple Calculator (type 'q' to quit)");

        while (true) {
            System.out.print("\nEnter first number (or q): ");
            if (sc.hasNext("[qQ]")) break;  // quit if user types q
            double a = readDouble(sc);

            System.out.print("Operator (+ - * /): ");
            String op = sc.next().trim();

            System.out.print("Enter second number: ");
            double b = readDouble(sc);

            Double ans = compute(a, b, op);
            if (ans == null) {
                System.out.println("Error: invalid operator or divide by zero.");
            } else {
                System.out.println("Result: " + ans);
            }
        }

        System.out.println("Goodbye!");
        sc.close();
    }

    private static double readDouble(Scanner sc) {
        while (!sc.hasNextDouble()) {
            String tok = sc.next();
            if (tok.matches("[qQ]")) System.exit(0);
            System.out.print("Not a number. Try again: ");
        }
        return sc.nextDouble();
    }

    private static Double compute(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> (b == 0) ? null : a / b;
            default -> null;
        };
    }
}
