package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.error.ParserCreationFailure;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CountedRepetitionTest {
    @Test
    void parseRepetitionMinimumOnly() {
        {
            final @NotNull var p = Alpha.parser("S : 2* 'a'");
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), p.parse("aaa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a", "a"), p.parse("aaaa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a", "a", "a"), p.parse("aaaaa"));
        }
    }

    @Test
    void parseRepetitionMaximumOnly() {
        {
            final @NotNull var p = Alpha.parser("S : *2 'a'");
            Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
            Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
        {
            final @NotNull var p = Alpha.parser("S : *0 'a'");
            Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertTrue(p.parse("aa").isFailure());
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
    }

    @Test
    void parseRepetitionMinMax() {
        {
            final @NotNull var p = Alpha.parser("S : 2*4 'a'");
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), p.parse("aaa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a", "a"), p.parse("aaaa"));
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
        {
            final @NotNull var p = Alpha.parser("S : 0*0 'a'");
            Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertTrue(p.parse("aa").isFailure());
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
    }

    @Test
    void parseRepetitionExact() {
        {
            final @NotNull var p = Alpha.parser("S : 2 'a'");
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
        }
    }

    @Test
    void createRepetitionParserFormatFailure() {
        // Negative minimum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : -1*2"));
        // Negative maximum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : *-1"));
        // Negative minimum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : -1*"));
        // Negative exact
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : -1"));
        // Minimum greater than maximum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : 4*2"));
    }

    @Test
    void createRepetitionParserFailure() {
        // Negative minimum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : -1*2 'a'"));
        // Negative maximum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : *-1 'a'"));
        // Negative minimum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : -1* 'a'"));
        // Negative exact
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : -1 'a'"));
        // Minimum greater than maximum
        Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parser("S : 4*2 'a'"));
    }
}
