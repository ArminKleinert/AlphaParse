package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parsing.ConcatCombinator;
import alphaparse.parsing.LookaheadCombinator;
import alphaparse.parsing.TerminalRegexpCombinator;
import alphaparse.parsing.TerminalStringCombinator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

class MultipleLookaheadsTest {
//    @Test
//    void contradictoryLookaheads() {
//        var p = Alpha.parser("S := &'a' &'b' ('a' | 'b' | 'c')+");
//        System.out.println(p.show());
//        System.out.println(p.parse("a"));
//    }
    @Test
    void lookaheads1() {
        var p = Alpha.parser(new Grammar(Map.of(
                Sym.sym("S"), new ConcatCombinator(List.of(new LookaheadCombinator(new LookaheadCombinator(new TerminalStringCombinator("a", false))),
                                new TerminalRegexpCombinator(Pattern.compile("[abc]"))))
        )),
                ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S")));
        System.out.println(Objects.requireNonNull(p.grammar().getProduction(Sym.sym("S"))).getClass());
        System.out.println(p.parse("a"));
    }
    @Test
    void doubledLookahead() {
        var p = Alpha.parser("S := &'a' &'a' ('a' | 'b' | 'c')+");
        System.out.println(p.show());
        System.out.println(p.parse("a"));
    }
}
