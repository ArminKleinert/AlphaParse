package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.error.ParserCreationFailure;
import alphaparse.parser_options.*;
import alphaparse.result.PT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class RegexTest {
    @Test
    void testNum() {
        var ruleTypes = Set.of(RulesAvailable.REGEX);
        var opts = ParserCreationOptions.pureEbnf().withRulesAvailable(ruleTypes);
        var p = Alpha.parser("S = #\"[a-fA-F0-9]+\"", opts);
        Assertions.assertEquals(
                PT.create("S", "7F"),
                p.parse("7F")
        );
    }

    @Test
    void testInvalid() {
        var ruleTypes = Set.<RulesAvailable>of();
        var opts = ParserCreationOptions.pureEbnf().withRulesAvailable(ruleTypes);
        Assertions.assertThrows(
                ParserCreationFailure.class,
                () -> Alpha.parser("S = #\"[a-fA-F0-9]+\"", opts));
    }
}
