package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parsing.ConcatCombinator;
import alphaparse.parsing.LookaheadCombinator;
import alphaparse.parsing.TerminalRegexpCombinator;
import alphaparse.parsing.TerminalStringCombinator;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class MultipleLookaheadsTest {
    @Test
    void contradictoryLookaheads() {
        var p = Alpha.parser("S := &'a' &'b' ('a' | 'b' | 'c')+");
        Assertions.assertTrue(p.parse("a").isFailure());
        Assertions.assertTrue(p.parse("b").isFailure());
        Assertions.assertTrue(p.parse("c").isFailure());
    }

    @Test
    void lookaheads1() {
        var p = Alpha.parser(new Grammar(Map.of(
                        Sym.sym("S"), new ConcatCombinator(List.of(new LookaheadCombinator(new LookaheadCombinator(new TerminalStringCombinator("a", false))),
                                new TerminalRegexpCombinator(Pattern.compile("[abc]"))))
                )),
                ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S")));
        Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
        Assertions.assertTrue(p.parse("b").isFailure());
    }

    @Test
    void doubledLookahead() {
        var p = Alpha.parser("S = &'a' &'a' ('a' | 'b' | 'c')+");
        Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
        Assertions.assertTrue(p.parse("b").isFailure());
    }

    @Test
    void doubledNegativeLookahead() {
        var p = Alpha.parser("S = !'a' !'a' ('a' | 'b' | 'c')+");
        Assertions.assertEquals(ParseTree.create("S", "b"), p.parse("b"));
        Assertions.assertEquals(ParseTree.create("S", "c"), p.parse("c"));
        Assertions.assertTrue(p.parse("a").isFailure());
    }

    @Test
    void doubledNegativeLookahead1() {
        var p = Alpha.parser("S = !'a' !'b' ('a' | 'b' | 'c')+");
        Assertions.assertEquals(ParseTree.create("S", "c"), p.parse("c"));
        Assertions.assertTrue(p.parse("a").isFailure());
        Assertions.assertTrue(p.parse("b").isFailure());
    }
}
