package alphaparse;

import alphaparse.parser_options.ParsingOptions;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class IterativeRegexTest {
    @Test
    void testCheckByPartials() {
        var p = Alpha.parser(                "S : #'A+'");
        var parses = Alpha.parses(p, "AAAA", ParsingOptions.getDefault().withIterativeDeepening(true).withPartial(true));
        Assertions.assertEquals(
                List.of(ParseTree.create("S", "A"),
                        ParseTree.create("S", "AA"),
                        ParseTree.create("S", "AAA"),
                        ParseTree.create("S", "AAAA")),
                parses);
    }
    @Test
    void testRegexString() {
        var p = Alpha.parser(                "S : #'A+' 'A' | #'A+' 'AA' | #'A+' 'AAA' | #'A+' 'AAAA'");

        Assertions.assertTrue(p.parses("").isEmpty());
        Assertions.assertTrue(p.parses("ABA").isEmpty());

        var parses = Alpha.parses(p, "AAAA", ParsingOptions.getDefault().withIterativeDeepening(true));
        Assertions.assertEquals(
                Set.of(ParseTree.create("S", "A", "AAA"),
                        ParseTree.create("S", "AA", "AA"),
                        ParseTree.create("S", "AAA", "A")),
                new HashSet<>(parses));
    }
    @Test
    void testStringRegex() {
        var p = Alpha.parser(                "S : 'A' #'A+' | 'AA' #'A+' | 'AAA' #'A+' | 'AAAA' #'A+'");

        Assertions.assertTrue(p.parses("").isEmpty());
        Assertions.assertTrue(p.parses("ABA").isEmpty());

        var parses = Alpha.parses(p, "AAAA", ParsingOptions.getDefault().withIterativeDeepening(true));
        Assertions.assertEquals(
                Set.of(ParseTree.create("S", "A", "AAA"),
                        ParseTree.create("S", "AA", "AA"),
                        ParseTree.create("S", "AAA", "A")),
                new HashSet<>(parses));
    }
    @Test
    void testRegexRegex() {
        var p = Alpha.parser(                "S : #'A+' #'A+'");

        Assertions.assertTrue(p.parses("").isEmpty());
        Assertions.assertTrue(p.parses("ABA").isEmpty());

        var parses = Alpha.parses(p, "AAAA", ParsingOptions.getDefault().withIterativeDeepening(true));
        Assertions.assertEquals(
                Set.of(ParseTree.create("S", "A", "AAA"),
                        ParseTree.create("S", "AA", "AA"),
                        ParseTree.create("S", "AAA", "A")),
                new HashSet<>(parses));
    }
}