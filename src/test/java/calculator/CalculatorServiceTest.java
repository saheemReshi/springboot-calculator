package calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    CalculatorService service = new CalculatorService();

    @Test
    void testAdd() {
        assertEquals(5, service.calculate("add", 2, 3));
    }

    @Test
    void testSubtract() {
        assertEquals(2, service.calculate("subtract", 5, 3));
    }

    @Test
    void testMultiply() {
        assertEquals(12, service.calculate("multiply", 4, 3));
    }

    @Test
    void testDivide() {
        assertEquals(5, service.calculate("divide", 10, 2));
    }

    @Test
    void testDivideByZero() {
        assertThrows(IllegalArgumentException.class,
            () -> service.calculate("divide", 10, 0));
    }
}