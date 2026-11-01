package org.rgupta.juliusbaer.postmortem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StringCalculatorTest {

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("validInputs")
    void shouldCalculateSum(
            String description,
            String input,
            int expected) {

        StringCalculator calculator = new StringCalculator();

        assertEquals(expected, calculator.add(input), description);
    }

    static Stream<Arguments> validInputs() {
        return Stream.of(
                // Stage 1: Basic numbers
                Arguments.of("Empty input", "", 0),
                Arguments.of("Single number", "5", 5),
                Arguments.of("Two numbers", "1,2", 3),
                Arguments.of("Three numbers", "10,20,30", 60),
                Arguments.of("Zeros", "0,0,0", 0),

                // Stage 2: Newline delimiters
                Arguments.of("Newline", "1\n2", 3),
                Arguments.of("Mixed delimiters", "1\n2,3", 6),
                Arguments.of("Multiple newlines", "1\n2\n3", 6),
                Arguments.of("Comma and newline", "10,20\n30", 60),

                // Stage 4: Ignore numbers > 1000
                Arguments.of("Exactly 1000", "1000", 1000),
                Arguments.of("1001 ignored", "1001", 0),
                Arguments.of("Large number ignored", "2,1001,3", 5),
                Arguments.of("Boundary", "1000,1001", 1000),

                // Stage 6: Custom delimiters
                Arguments.of("Semicolon", "//;\n1;2;3", 6),
                Arguments.of("Pipe", "//|\n1|2|3", 6),
                Arguments.of("Dot", "//.\n1.2.3", 6),
                Arguments.of("Asterisk", "//*\n1*2*3", 6),
                Arguments.of("Hash", "//#\n10#20#30", 60),
                Arguments.of("Hash With Repeated Delimiters", "//#\n10#20###30", 60),
                Arguments.of("Repeated Delimiters", "//ab+ba\n1ab+baab+ba2", 3),

                // Stage 7: Combined requirements
                Arguments.of("Custom + ignore 1001",
                        "//;\n2;1001;3", 5),

                Arguments.of("Custom + include 1000",
                        "//;\n2;1000;3", 1005)
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("invalidInputs")
    void shouldRejectInvalidInput(
            String description,
            String input) {

        StringCalculator calculator = new StringCalculator();

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.add(input),
                description
        );
    }

    static Stream<Arguments> invalidInputs() {
        return Stream.of(
                // Stage 3: Invalid newline endings
                Arguments.of("Trailing newline", "1,2\n"),
                Arguments.of("Comma followed by newline", "1,\n"),
                Arguments.of("Single trailing newline", "5\n"),

                // Stage 5: Negative numbers
                Arguments.of("Single negative", "-1"),
                Arguments.of("Negative in sequence", "1,-2,3"),
                Arguments.of("Negative after newline", "1\n-5"),
                Arguments.of("Large negative", "-1001,2"),

                // Stage 7: Custom delimiter + negative
                Arguments.of("Custom with negative",
                        "//;\n1;-2;3")
        );
    }
}
