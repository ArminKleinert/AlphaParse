package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RedefinitionOptionTest {
    @Test
    void optionOverrideTest() {
        var redefinitionOpts = Alpha.ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(Grammar.RedefinitionOption.OVERRIDE);
        var p = Alpha.parser("S : 'A'\nS : 'B'\nS : 'C'", redefinitionOpts);
        Assertions.assertTrue(p.parse("A").isFailure());
        Assertions.assertTrue(p.parse("B").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "C"), p.parse("C"));
    }
    @Test
    void optionErrorTest() {
        var redefinitionOpts = Alpha.ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(Grammar.RedefinitionOption.ERROR);

        // Only one production -> No duplicates, no problem
        Assertions.assertEquals(
                ParseTree.create("S", "A"),
                Alpha.parser("S : 'A'", redefinitionOpts).parse("A"));

        // Duplicate name, different lhs -> Problem
        Assertions.assertThrows(
                IllegalArgumentException.class,
                ()->Alpha.parser("S : 'A'\nS : 'B'\nS : 'C'", redefinitionOpts));

        // Fails even if the production does not change
        Assertions.assertThrows(
                IllegalArgumentException.class,
                ()->Alpha.parser("S : 'A'\nS : 'A'\nS : 'A'", redefinitionOpts));
    }
    @Test
    void optionChoiceTest() {
        var redefinitionOpts = Alpha.ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(Grammar.RedefinitionOption.CHOICE);
        var p = Alpha.parser("S : 'A'\nS : 'B'\nS : 'C'", redefinitionOpts);
        Assertions.assertEquals(ParseTree.create("S", "A"), p.parse("A"));
        Assertions.assertEquals(ParseTree.create("S", "B"), p.parse("B"));
        Assertions.assertEquals(ParseTree.create("S", "C"), p.parse("C"));
    }
    @Test
    void optionKeepTest() {
        var redefinitionOpts = Alpha.ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(Grammar.RedefinitionOption.KEEP_AND_WARN);
        var p = Alpha.parser("S : 'A'\nS : 'B'\nS : 'C'", redefinitionOpts);
        Assertions.assertEquals(ParseTree.create("S", "A"), p.parse("A"));
        Assertions.assertTrue(p.parse("B").isFailure());
        Assertions.assertTrue(p.parse("C").isFailure());
    }
}