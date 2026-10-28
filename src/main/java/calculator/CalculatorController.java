package calculator;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @GetMapping("/{operation}")
    public Map<String, Object> calculate(
            @PathVariable String operation,
            @RequestParam(required = false) String a,
            @RequestParam(required = false) String b) {

        double x = parseNumber(a);
        double y = parseNumber(b);

        double result = service.calculate(operation, x, y);

        return Map.of(
                "operation", operation,
                "a", x,
                "b", y,
                "result", result
        );
    }

    private double parseNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing number");
        }

        try {
            double number = Double.parseDouble(value);

            if (!Double.isFinite(number)) {
                throw new IllegalArgumentException("Invalid number");
            }

            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleError(IllegalArgumentException e) {
        return Map.of(
                "error", "BAD_REQUEST",
                "message", e.getMessage()
        );
    }
}