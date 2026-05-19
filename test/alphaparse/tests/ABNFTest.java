package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.error.ParserCreationFailure;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ABNFTest {
    @Test
    void singleQuotesForStringTerminalsNotAllowed() {
        Assertions.assertThrows(
                ParserCreationFailure.class,
                ()-> Alpha.parser("S : 'abc'", ParserCreationOptions.ABNF()));
    }

    @Test
    void caseSensitivityTest() {
        {
            var p = Alpha.parser("S : \"abc\"", ParserCreationOptions.ABNF());
            Assertions.assertEquals(ParseTree.create("S", "abc"), p.parse("abc"));
            Assertions.assertEquals(ParseTree.create("S", "abc"), p.parse("AbC"));
            Assertions.assertEquals(ParseTree.create("S", "abc"), p.parse("ABC"));
        }
        {
            var p = Alpha.parser("S : \"A\" \"B\"", ParserCreationOptions.ABNF());
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), p.parse("ab"));
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), p.parse("Ab"));
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), p.parse("AB"));
        }
    }

    @Test
    void countedRepetitionTestExact() {
        var p = Alpha.parser("S : 2 \"A\"", ParserCreationOptions.ABNF());
        Assertions.assertTrue(p.parse("").isFailure());
        Assertions.assertTrue(p.parse("A").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
        Assertions.assertTrue(p.parse("AAA").isFailure());}
    @Test
    void countedRepetitionTestSameSides() {
        var p = Alpha.parser("S : 2*2 \"A\"", ParserCreationOptions.ABNF());
        Assertions.assertTrue(p.parse("").isFailure());
        Assertions.assertTrue(p.parse("a").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
        Assertions.assertTrue(p.parse("aaa").isFailure());}
    @Test
    void countedRepetitionTestRightOnly() {
        var p = Alpha.parser("S : *2 \"A\"", ParserCreationOptions.ABNF());
        Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
        Assertions.assertEquals(ParseTree.create("S", "A"), p.parse("a"));
        Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
        Assertions.assertTrue(p.parse("aaa").isFailure());}
    @Test
    void countedRepetitionTestLeftOnly() {
        var p = Alpha.parser("S : 2* \"A\"", ParserCreationOptions.ABNF());
        Assertions.assertTrue(p.parse("").isFailure());
        Assertions.assertTrue(p.parse("a").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
        Assertions.assertEquals(ParseTree.create("S", "A", "A", "A"), p.parse("aaa"));}
    @Test
    void countedRepetitionTestBoth() {
        var p = Alpha.parser("S : 1*2 \"A\"", ParserCreationOptions.ABNF());
        Assertions.assertTrue(p.parse("").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "A"), p.parse("a"));
        Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
        Assertions.assertTrue(p.parse("aaa").isFailure());}
    @Test
    void countedRepetitionTestStarOnly() {
        var p = Alpha.parser("S : * \"A\"", ParserCreationOptions.ABNF());
        Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
        Assertions.assertEquals(ParseTree.create("S", "A"), p.parse("a"));
        Assertions.assertEquals(ParseTree.create("S", "A", "A"), p.parse("aa"));
        Assertions.assertEquals(ParseTree.create("S", "A", "A", "A"), p.parse("aaa"));
    }
    @Test
    void incrementalExtensionTest() {
        var p = Alpha.parser("""
                    S := "a" S
                    S =/ "b" S
                    S =/ epsilon
                    """, ParserCreationOptions.ABNF());
        var epsTree = ParseTree.create("S");
        Assertions.assertEquals(
                ParseTree.create("S", "a", epsTree),
                p.parse("a")
        );
        Assertions.assertEquals(
                ParseTree.create("S", "a", ParseTree.create("S", "b", epsTree)),
                p.parse("ab")
        );
        Assertions.assertEquals(
                ParseTree.create("S", "a", ParseTree.create("S", "a", epsTree)),
                p.parse("aa")
        );
    }

    @Test
    void codepointsTest() {
        {
            var p = Alpha.parser("""
                    S := %x41-43
                    """, ParserCreationOptions.ABNF());
            Assertions.assertEquals(
                    ParseTree.create("S", "A"),
                    p.parse("A")
            );
        }
        {
            var p = Alpha.parser("""
                    S := %d65-67
                    """, ParserCreationOptions.ABNF());
            Assertions.assertEquals(
                    ParseTree.create("S", "A"),
                    p.parse("A")
            );
        }
        {
            var p = Alpha.parser("S : 1* A\n<A> : %d66", ParserCreationOptions.ABNF());
            Assertions.assertEquals(
                    ParseTree.create("S", "B"),
                    p.parse("B")
            );
        }
        {
            var p = Alpha.parser("S : 1* (%d65-67)", ParserCreationOptions.ABNF());
            Assertions.assertEquals(
                    ParseTree.create("S", "B", "B", "B"),
                    p.parse("BBB")
            );
        }
    }

    @Test void codepointFailureTest() {
        {
            var p = Alpha.parser("S : 1* (%d65-67)", ParserCreationOptions.ABNF());
            Assertions.assertEquals(
                    ParseTree.create("S", "B"),
                    p.parse("B")
            );
        }
    }
}