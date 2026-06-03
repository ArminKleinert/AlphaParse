package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.grammar.Grammar;
import alphaparse.grammar.GrammarBuilder;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parsing.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

class GrammarBuilderTest {
    @Test
    void withWS() {
        var pFromGB = Alpha.parser("""
                S = A B
                <A> = 'foo'
                <B> = #'\\d+'
                """, ParserCreationOptions.newWithStandardWhitespace());
        System.out.println(pFromGB.parse("foo12"));
    }

    @Test
    void equivalentToStringGrammar() {
        var pFromString = Alpha.parser(
                        """
                                S = NUMBER NUMBER*
                                NUMBER = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
                                """)
                .grammar();
        var pFromGB = new GrammarBuilder(ParserCreationOptions.getDefault()) {
            @Override
            public void make() {
                addProduction("S", concat(Sym.sym("NUMBER"), repeatMin(nt(Sym.sym("NUMBER")), 0)));
                addProduction("NUMBER", alternation("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
            }
        }.build();
        Assertions.assertEquals(pFromString, pFromGB);
    }

    @Test
    void equivalentToMoreExplicitGrammar() {
        var pGrammarList = new LinkedHashMap<Sym, Rule>();
        pGrammarList.put(Sym.sym("S"),
                new ConcatRule(List.of(new NonTerminal(Sym.sym("NUMBER")), new ZeroOrMoreRule(new NonTerminal(Sym.sym("NUMBER"))))));
        pGrammarList.put(Sym.sym("NUMBER"), new AlternationRule(Stream.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").map(it -> (Rule) new StringTerm(it, false)).toList()));
        var finalGrammar = Alpha.parser(new Grammar(pGrammarList), ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S"))).grammar();

        var pFromGB = new GrammarBuilder(ParserCreationOptions.getDefault()) {
            @Override
            public void make() {
                addProduction("S", concat(Sym.sym("NUMBER"), zeroOrMore(nt(Sym.sym("NUMBER")))));
                addProduction("NUMBER", alternation("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
            }
        }.build();
        Assertions.assertEquals(finalGrammar, pFromGB);
    }
}
