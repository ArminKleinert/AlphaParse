package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.grammar.GrammarBuilder;
import alphaparse.grammar.RedefinitionOption;
import alphaparse.parser_options.ParserCreationOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class GrammarBuilderTest {
    @Test
    void simpleTest() {
        var p = Alpha.parser("S = S S | 'abc' | ('def' | 'ghi') S | '1'?", ParserCreationOptions
                .newWithStandardWhitespace()
                .withRedefinitionOption(RedefinitionOption.CHOICE)
                .withStartProduction(Sym.sym("S")));

        var opts = ParserCreationOptions
                .newWithStandardWhitespace()
                .withRedefinitionOption(RedefinitionOption.CHOICE)
                .withStartProduction(Sym.sym("S"));
//        var gb = new GrammarBuilder(opts) {
//            @Override
//            public void make() {
//                addProduction("S",
//                        alternation(
//                                concat(nt(Sym.sym("S")), nt(Sym.sym("S"))),
//                                of("abc"),
//                                concat(alternation("def", "ghi"), of(Sym.sym("S"))),
//                                optional(string("1"))));
//            }
//        }.build();

        System.out.println(p.grammar());
        //System.out.println(Alpha.parser(gb, opts).grammar());
        System.out.println();
        System.out.println(p.parse("defghi1"));
        //System.out.println(Alpha.parser(gb, opts).parse("ghidef1"));
    }
}
