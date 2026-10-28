package calculator;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public double calculate(String operation, double a, double b) {

        return switch (operation) {
            case "add" -> a + b;
            case "subtract" -> a - b;
            case "multiply" -> a * b;

            case "divide" -> {
                if (b == 0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                yield a / b;
            }

            default -> throw new IllegalArgumentException("Unknown operation");
        };
    }
}