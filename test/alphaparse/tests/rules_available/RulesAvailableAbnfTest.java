package alphaparse.tests.rules_available;

import alphaparse.Alpha;
import alphaparse.error.ParserCreationFailure;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RulesAvailableAbnfTest {
    private @NotNull ParserCreationOptions opts() {
        return ParserCreationOptions.getDefault().withRulesAvailable(RulesAvailable.ABNF_RULES());
    }

    @Test
    void alternation() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = \"a\" | \"b\"", opts()));
    }

    @Test
    void epsilon() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = epsilon", opts()));
    }

    @Test
    void explicitStringCaseSensitivity() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = %i\"a\"", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = %s\"a\"", opts()));
    }

    @Test
    void extendedIdentifiers() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\"", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("\uD83C\uDF81 = \"a\"", opts()));
    }

    @Test
    void lookahead() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = &\"a\" \"a\"", opts()));
    }

    @Test
    void negativeLookahead() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = !\"b\" \"a\"", opts()));
    }

    @Test
    void optional() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = [\"a\"]", opts()));
    }

    @Test
    void optionalQuery() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = \"a\"?", opts()));
    }

    @Test
    void optionalRepetition() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = {\"a\"}", opts()));
    }

    @Test
    void optionalRepetitionStar() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = \"a\"?", opts()));
    }

    @Test
    void orderedChoice() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = \"a\" / \"b\"", opts()));
    }

    @Test
    void plus() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S = \"a\"+", opts()));
    }

    @Test
    void regex() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = #\"a\"", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = #'a'", opts()));
    }

    @Test
    void singleQuotesForStringTerminals() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : 'abc'", opts()));
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
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S = * \"a\"", opts()));
    }
}
