package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CombinatorExamplesTests {
    @Test
    void testChoiceExample1() {
        {
            var p = Alpha.parser("S = 'a' | 'b' | 'ab'");
            IO2.println(p.parse("a"));
            IO2.println(p.parse("b"));
            IO2.println(p.parse("ab"));
            Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
            Assertions.assertEquals(ParseTree.create("S", "b"), p.parse("b"));
            Assertions.assertEquals(ParseTree.create("S", "ab"), p.parse("ab"));
        }{
            var opts = Alpha.ParserCreationOptions
                    .getDefault()
                    .withRedefinitionOption(Grammar.RedefinitionOption.CHOICE);
            var p = Alpha.parser("""
                    S := 'a'
                    S =/ 'b'
                    S =/ 'ab'
                    """, opts);
            IO2.println(p.parse("a"));
            IO2.println(p.parse("b"));
            IO2.println(p.parse("ab"));
            Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
            Assertions.assertEquals(ParseTree.create("S", "b"), p.parse("b"));
            Assertions.assertEquals(ParseTree.create("S", "ab"), p.parse("ab"));
        }
    }
}
