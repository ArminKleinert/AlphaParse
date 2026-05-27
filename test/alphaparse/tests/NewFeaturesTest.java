package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import org.junit.jupiter.api.Test;

class NewFeaturesTest {
    @Test
    void oneOrMoreRepetitionReplacements() {
        var p5 = Alpha.parser("S := &'ab' ('a' | 'b')+", ParserCreationOptions.getDefault().withCorrectnessCheck(false));

        //System.out.println(((CombinatorWithManyParsers)p5.grammar().getProduction(Sym.sym("S"))).getParsers().stream().map(Object::getClass).toList());

        System.out.println(p5);
        System.out.println(p5.parse("aba"));

//        var p4 = Alpha.parser("S := 1*3 %x1F381", ParserCreationOptions.getDefault().withCorrectnessCheck(false));
//        System.out.println(p4);
//
//        var p1 = Alpha.parser("S = \"a\"\nA = 'r'");
//        System.out.println(p1.show());
//        System.out.println(p1.parse("a"));
//
//        var p2 = Alpha.parser("S := %x42-5a");
//        System.out.println(p2);
//
//        var p3 = Alpha.parser("epsilons := 'a'");
//        System.out.println(p3);
    }
}
