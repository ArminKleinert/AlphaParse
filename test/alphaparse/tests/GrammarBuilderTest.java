package alphaparse.tests;

import alphaparse.Sym;
import alphaparse.grammar.GrammarBuilder;
import alphaparse.grammar.RedefinitionOption;
import alphaparse.parser_options.ParserCreationOptions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class GrammarBuilderTest {
    @Test
    void simpleTest() {
        var gb = new GrammarBuilder(ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(RedefinitionOption.CHOICE)) {
            @Override
            public void make() {
                addProduction("S", of("abc"));
                addProduction("S", of(Set.of("def", "ghi", Sym.sym("S"))));
                addProduction("S", repeat(string("1"), 0, 1));
            }
        };
        System.out.println(gb.build());
    }
}
