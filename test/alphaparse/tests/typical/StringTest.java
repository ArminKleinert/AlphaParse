package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.testutil.PT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringTest {
    @Test
    void explicitStringCaseInsensitivity() {
        var opts = ParserCreationOptions.getDefault()
                .addAvailableRule(RulesAvailable.STRING_CASE_SENSITIVITY_PREFIX);
        Assertions.assertEquals(
                PT.create("S", "A"),
                Alpha.parser("S = %i\"A\"", opts).parse("A"));
        Assertions.assertEquals(
                PT.create("S", "A"),
                Alpha.parser("S = %i\"A\"", opts).parse("a"));
    }

    @Test
    void explicitStringCaseSensitivity() {
        var opts = ParserCreationOptions.getDefault()
                .addAvailableRule(RulesAvailable.STRING_CASE_SENSITIVITY_PREFIX);
        Assertions.assertEquals(
                PT.create("S", "A"),
                Alpha.parser("S = %s\"A\"", opts).parse("A"));
        Assertions.assertTrue(
                Alpha.parser("S = %s\"A\"", opts).parse("a").isFailure());
    }
}
