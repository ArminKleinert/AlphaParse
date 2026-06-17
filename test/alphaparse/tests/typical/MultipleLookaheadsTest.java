package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parsing.ConcatRule;
import alphaparse.parsing.LookaheadRule;
import alphaparse.parsing.RegexTerm;
import alphaparse.parsing.StringTerm;
import alphaparse.result.PT;
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
                        Sym.sym("S"), ConcatRule.create(List.of(LookaheadRule.create(LookaheadRule.create(StringTerm.create("a", false))),
                                RegexTerm.create(Pattern.compile("[abc]"))))
                )),
                ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S")));
        Assertions.assertEquals(PT.create("S", "a"), p.parse("a"));
        Assertions.assertTrue(p.parse("b").isFailure());
    }

    @Test
    void doubledLookahead() {
        var p = Alpha.parser("S = &'a' &'a' ('a' | 'b' | 'c')+");
        Assertions.assertEquals(PT.create("S", "a"), p.parse("a"));
        Assertions.assertTrue(p.parse("b").isFailure());
    }

    @Test
    void doubledNegativeLookahead() {
        var p = Alpha.parser("S = !'a' !'a' ('a' | 'b' | 'c')+");
        Assertions.assertEquals(PT.create("S", "b"), p.parse("b"));
        Assertions.assertEquals(PT.create("S", "c"), p.parse("c"));
        Assertions.assertTrue(p.parse("a").isFailure());
    }

    @Test
    void doubledNegativeLookahead1() {
        var p = Alpha.parser("S = !'a' !'b' ('a' | 'b' | 'c')+");
        Assertions.assertEquals(PT.create("S", "c"), p.parse("c"));
        Assertions.assertTrue(p.parse("a").isFailure());
        Assertions.assertTrue(p.parse("b").isFailure());
    }
}
