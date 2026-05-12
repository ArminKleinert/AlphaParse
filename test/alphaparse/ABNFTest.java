package alphaparse;

import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ABNFTest {
    @Test
    void singleQuotesForStringTerminalsNotAllowed() {
        Assertions.assertThrows(
                IllegalStateException.class,
                ()->Alpha.parser("S : 'abc'", ParserCreationOptions.ABNF()));
    }

    @Test
    void caseSensitivityTest() {
        {
            var p = Alpha.parser("S : \"abc\"", ParserCreationOptions.ABNF());
            Assertions.assertEquals(ParseTree.create("S", "abc"), p.parse("abc"));
            Assertions.assertEquals(ParseTree.create("S", "ABC"), p.parse("AbC"));
            Assertions.assertEquals(ParseTree.create("S", "ABC"), p.parse("ABC"));
        }
        {
            var p = Alpha.parser("S : \"a\" \"b\"", ParserCreationOptions.ABNF());
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), p.parse("ab"));
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), p.parse("Ab"));
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), p.parse("AB"));
        }
    }

    @Test
    void countedRepetitionTest() {
        {
            var p = Alpha.parser("S : 2 \"A\"", ParserCreationOptions.ABNF());
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("A").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
            Assertions.assertTrue(p.parse("AAA").isFailure());
        }
        {
            var p = Alpha.parser("S : 2*2 \"A\"", ParserCreationOptions.ABNF());
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
            Assertions.assertTrue(p.parse("aaa").isFailure());
        }
        {
            var p = Alpha.parser("S : *2 \"A\"", ParserCreationOptions.ABNF());
            Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
            Assertions.assertEquals(ParseTree.create("S", "A"), p.parse("a"));
            Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
            Assertions.assertTrue(p.parse("aaa").isFailure());
        }
        {
            var p = Alpha.parser("S : 2* \"A\"", ParserCreationOptions.ABNF());
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
            Assertions.assertEquals(ParseTree.create("S", "A", "A", "A"), p.parse("aaa"));
        }
        {
            var p = Alpha.parser("S : 1*2 \"A\"", ParserCreationOptions.ABNF());
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "A"), p.parse("a"));
            Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
            Assertions.assertTrue(p.parse("aaa").isFailure());
        }
    }
}