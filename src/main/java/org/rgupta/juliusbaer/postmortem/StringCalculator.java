package org.rgupta.juliusbaer.postmortem;

import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Null input");
        }

        if (input.isEmpty()) {
            return 0;
        }

        String numbers = input;
        String separatorRegex = "[,\\n]";

        // 1. Extract optional custom delimiter
        if (input.startsWith("//")) {

            int headerEnd = input.indexOf('\n');

            if (headerEnd == -1) {
                throw new IllegalArgumentException("Invalid header");
            }

            String delimiter = input.substring(2, headerEnd);

            if (delimiter.isEmpty()) {
                throw new IllegalArgumentException("Empty delimiter");
            }

            numbers = input.substring(headerEnd + 1);

            // Match one or more occurrences of the literal delimiter,
            // or a single comma or newline.
            separatorRegex = "(?:" + Pattern.quote(delimiter) + ")+|[,\\n]";
        }

        // 2. Split, preserving trailing empty tokens
        String[] tokens = numbers.split(separatorRegex, -1);

        // 3. Validate and accumulate
        int sum = 0;

        for (String token : tokens) {

            if (token.isEmpty()) {
                throw new IllegalArgumentException(
                        "Empty value or trailing delimiter"
                );
            }

            int value = Integer.parseInt(token);

            if (value < 0) {
                throw new IllegalArgumentException(
                        "Negative number: " + value
                );
            }

            if (value <= 1000) {
                sum = Math.addExact(sum, value);
            }
        }

        return sum;
    }
}
