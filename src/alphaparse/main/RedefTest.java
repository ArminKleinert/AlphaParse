package alphaparse.main;

import alphaparse.Alpha;
import alphaparse.grammar.RedefinitionOption;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;

class RedefTest {
    public static void main(String[] args) {
        ParserCreationOptions opts = ParserCreationOptions
                .getDefault()
                .withRedefinitionOption(RedefinitionOption.OVERRIDE);
        String gr = "S : 'A'\nS : 'B'\nS : 'C'";
        Parser p;

        p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.OVERRIDE));
        System.out.println(p.parse("A").isSuccess()); // false
        System.out.println(p.parse("B").isSuccess()); // false
        System.out.println(p.parse("C").isSuccess()); // true

        p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.ERROR)); // Fails
        System.out.println(p.parse("A").isSuccess()); // n.a.
        System.out.println(p.parse("B").isSuccess()); // n.a.
        System.out.println(p.parse("C").isSuccess()); // n.a.

        p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.CHOICE));
        System.out.println(p.parse("A").isSuccess()); // true
        System.out.println(p.parse("B").isSuccess()); // true
        System.out.println(p.parse("C").isSuccess()); // true

        p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.KEEP));
        System.out.println(p.parse("A").isSuccess()); // true
        System.out.println(p.parse("B").isSuccess()); // false
        System.out.println(p.parse("C").isSuccess()); // false
    }
}
