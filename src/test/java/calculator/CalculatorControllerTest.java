package calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void testAddEndpoint() throws Exception {
        mvc.perform(get("/api/v1/add?a=2&b=3"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    void testDivideByZero() throws Exception {
        mvc.perform(get("/api/v1/divide?a=10&b=0"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void testMissingParameter() throws Exception {
        mvc.perform(get("/api/v1/add?a=2"))
           .andExpect(status().isBadRequest());
    }
}