package alphaparse.tests.rules_available;

import alphaparse.Alpha;
import alphaparse.error.ParserCreationFailure;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RulesAvailableDefaultTest {
    private @NotNull ParserCreationOptions opts() {
        return ParserCreationOptions.getDefault().withRulesAvailable(RulesAvailable.DEFAULT_RULES());
    }

    @Test
    void alternation() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\" | \"b\"", opts()));
    }

    @Test
    void epsilon() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = epsilon", opts()));
    }

    @Test
    void extendedIdentifiers() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\"", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("\uD83C\uDF81 = \"a\"", opts()));
    }

    @Test
    void lookahead() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = &\"a\" \"a\"", opts()));
    }

    @Test
    void negativeLookahead() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = !\"b\" \"a\"", opts()));
    }

    @Test
    void optional() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = [\"a\"]", opts()));
    }

    @Test
    void optionalQuery() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\"?", opts()));
    }

    @Test
    void optionalRepetition() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = {\"a\"}", opts()));
    }

    @Test
    void optionalRepetitionStar() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\"?", opts()));
    }

    @Test
    void orderedChoice() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\" / \"b\"", opts()));
    }

    @Test
    void plus() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\"+", opts()));
    }

    @Test
    void regex() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = #\"a\"", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = #'a'", opts()));
    }

    @Test
    void singleQuotesForStringTerminals() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S : 'abc'", opts()));
    }

    @Test
    void valueRange() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = %x41-5a", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = %d65-90", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = %b1000001-1011010", opts()));
    }

    @Test
    void variableRepetition() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = 1*5 \"a\"", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = 1* \"a\"", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = *5 \"a\"", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = * \"a\"", opts()));
    }
}
