package item24;

public class CalculatorMain {

    public static void main(String[] args) {
        int v1 = 10;
        int v2 = 5;

        System.out.printf("Plus Result : %d\n", Calculator.Operation.PLUS.calculate(v1, v2));
        System.out.printf("Minus Result : %d\n", Calculator.Operation.MINUS.calculate(v1, v2));
    }

}
