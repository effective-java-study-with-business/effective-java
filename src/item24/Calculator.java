package item24;

import java.util.function.BiFunction;

public class Calculator {

    // Enum already includes static keyword
    public enum Operation {
        PLUS((v1, v2) -> (v1 + v2)),
        MINUS((v1, v2) -> (v1 - v2));

        private BiFunction<Integer, Integer, Integer> expression;

        Operation(BiFunction<Integer, Integer, Integer> expression) {
            this.expression = expression;
        }

        public Integer calculate(Integer v1, Integer v2) {
            return expression.apply(v1, v2);
        }
    }
}
