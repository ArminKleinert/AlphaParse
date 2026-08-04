package alphaparse.tests.typical.redefinition_options;

import alphaparse.Alpha;
import alphaparse.parser_options.RedefinitionOption;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.testutil.PT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RedefinitionOptionTest {
    @Test
    void optionOverrideTest() {
        var redefinitionOpts = ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(RedefinitionOption.OVERRIDE);
        var p = Alpha.parser("S = 'A'\nS = 'B'\nS = 'C'", redefinitionOpts);
        Assertions.assertTrue(p.parse("A").isFailure());
        Assertions.assertTrue(p.parse("B").isFailure());
        Assertions.assertEquals(PT.create("S", "C"), p.parse("C"));
    }
    @Test
    void optionErrorTest() {
        var redefinitionOpts = ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(RedefinitionOption.ERROR);

        // Only one production -> No duplicates, no problem
        Assertions.assertEquals(
                PT.create("S", "A"),
                Alpha.parser("S = 'A'", redefinitionOpts).parse("A"));

        // Duplicate name, different lhs -> Problem
        Assertions.assertThrows(
                IllegalArgumentException.class,
                ()->Alpha.parser("S = 'A'\nS = 'B'\nS = 'C'", redefinitionOpts));

        // Fails even if the production does not change
        Assertions.assertThrows(
                IllegalArgumentException.class,
                ()->Alpha.parser("S = 'A'\nS = 'A'\nS = 'A'", redefinitionOpts));
    }
    @Test
    void optionChoiceTest() {
        var redefinitionOpts = ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(RedefinitionOption.CHOICE);
        var p = Alpha.parser("S = 'A'\nS = 'B'\nS = 'C'", redefinitionOpts);
        Assertions.assertEquals(PT.create("S", "A"), p.parse("A"));
        Assertions.assertEquals(PT.create("S", "B"), p.parse("B"));
        Assertions.assertEquals(PT.create("S", "C"), p.parse("C"));
    }
    @Test
    void optionKeepTest() {
        var redefinitionOpts = ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(RedefinitionOption.KEEP);
        var p = Alpha.parser("S = 'A'\nS = 'B'\nS = 'C'", redefinitionOpts);
        Assertions.assertEquals(PT.create("S", "A"), p.parse("A"));
        Assertions.assertTrue(p.parse("B").isFailure());
        Assertions.assertTrue(p.parse("C").isFailure());
    }
}