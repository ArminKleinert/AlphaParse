package alphaparse.main;

import alphaparse.Alpha;
import alphaparse.grammar.ProductionRedefinitionOption;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;

class RedefTest {
    public static void main(String[] args) {
        ParserCreationOptions opts = ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(ProductionRedefinitionOption.OVERRIDE);
        String gr = "S : 'A'\nS : 'B'\nS : 'C'";
        Parser p;

        p = Alpha.parser(gr, opts.withRedefinitionOption(ProductionRedefinitionOption.OVERRIDE));
        System.out.println(p.parse("A").isSuccess()); // false
        System.out.println(p.parse("B").isSuccess()); // false
        System.out.println(p.parse("C").isSuccess()); // true

        p = Alpha.parser(gr, opts.withRedefinitionOption(ProductionRedefinitionOption.ERROR)); // Fails
        System.out.println(p.parse("A").isSuccess()); // n.a.
        System.out.println(p.parse("B").isSuccess()); // n.a.
        System.out.println(p.parse("C").isSuccess()); // n.a.

        p = Alpha.parser(gr, opts.withRedefinitionOption(ProductionRedefinitionOption.CHOICE));
        System.out.println(p.parse("A").isSuccess()); // true
        System.out.println(p.parse("B").isSuccess()); // true
        System.out.println(p.parse("C").isSuccess()); // true

        p = Alpha.parser(gr, opts.withRedefinitionOption(ProductionRedefinitionOption.KEEP));
        System.out.println(p.parse("A").isSuccess()); // true
        System.out.println(p.parse("B").isSuccess()); // false
        System.out.println(p.parse("C").isSuccess()); // false
    }
}
