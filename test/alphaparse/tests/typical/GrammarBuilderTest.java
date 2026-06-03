package alphaparse.tests.typical;

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
    void equivalentToStringGrammar() {
        var gFromString = Alpha.parser(
                        """
                                S = NUMBER NUMBER*
                                NUMBER = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
                                """)
                .grammar();
        var gFromGB = new GrammarBuilder(ParserCreationOptions.getDefault()) {
            @Override
            public void make() {
                addProduction("S", concat(Sym.sym("NUMBER"), repeatMin(nt(Sym.sym("NUMBER")), 0)));
                addProduction("NUMBER", alternation("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
            }
        }.build();

        Assertions.assertEquals(gFromString, gFromGB);

        var pFromString = Alpha.parser(gFromString, ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S")));
        var pFromGB = Alpha.parser(gFromGB, ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S")));
        var text = "0123456789";
        Assertions.assertEquals(pFromString.parse(text), pFromGB.parse(text));
    }

    @Test
    void equivalentToMoreExplicitGrammar() {
        var pGrammarList = new LinkedHashMap<Sym, Rule>();
        pGrammarList.put(Sym.sym("S"),
                new ConcatRule(List.of(NonTerminal.create(Sym.sym("NUMBER")), new ZeroOrMoreRule(NonTerminal.create(Sym.sym("NUMBER"))))));
        pGrammarList.put(Sym.sym("NUMBER"), new AlternationRule(Stream.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").map(it -> (Rule) new StringTerm(it, false)).toList()));
        var pFromGrammar = Alpha.parser(new Grammar(pGrammarList), ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S"))).grammar();

        var pFromGB = new GrammarBuilder(ParserCreationOptions.getDefault()) {
            @Override
            public void make() {
                addProduction("S", concat(Sym.sym("NUMBER"), zeroOrMore(nt(Sym.sym("NUMBER")))));
                addProduction("NUMBER", alternation("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
            }
        }.build();

        Assertions.assertEquals(pFromGrammar, pFromGB);
    }
}
