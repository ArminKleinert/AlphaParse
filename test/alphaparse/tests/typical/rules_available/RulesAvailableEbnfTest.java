package alphaparse.tests.typical.rules_available;

import alphaparse.Alpha;
import alphaparse.error.ParserCreationFailure;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RulesAvailableEbnfTest {
    private @NotNull ParserCreationOptions opts() {
        return ParserCreationOptions.getDefault().withRulesAvailable(RulesAvailable.ebnfRules());
    }

    @Test
    void alternation() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := \"a\" | \"b\"", opts()));
    }

    @Test
    void epsilon() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := ε", opts()));
    }

    @Test
    void explicitStringCaseSensitivity() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := %i\"a\"", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := %s\"a\"", opts()));
    }

    @Test
    void extendedIdentifiers() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := \"a\"", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("\uD83C\uDF81 = \"a\"", opts()));
    }

    @Test
    void lookahead() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := &\"a\" \"a\"", opts()));
    }

    @Test
    void negativeLookahead() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := !\"b\" \"a\"", opts()));
    }

    @Test
    void optional() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := [\"a\"]", opts()));
    }

    @Test
    void optionalQuery() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := \"a\"?", opts()));
    }

    @Test
    void optionalRepetition() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := {\"a\"}", opts()));
    }

    @Test
    void optionalRepetitionStar() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := \"a\"?", opts()));
    }

    @Test
    void orderedChoice() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := \"a\" / \"b\"", opts()));
    }

    @Test
    void plus() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := \"a\"+", opts()));
    }

    @Test
    void regex() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := #\"a\"", opts()));
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := #'a'", opts()));
    }

    @Test
    void singleQuotesForStringTerminals() {
        Assertions.assertDoesNotThrow(() -> Alpha.parser("S := 'abc'", opts()));
    }

    @Test
    void valueRange() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := %x41-5a", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := %d65-90", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := %b1000001-1011010", opts()));
    }

    @Test
    void variableRepetition() {
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := 1*5 \"a\"", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := 1* \"a\"", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := *5 \"a\"", opts()));
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S := * \"a\"", opts()));
    }
}
