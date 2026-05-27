package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.parsing.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class NewFeaturesTest {
    @Test void exclusionFullTest1() {
        var p6 = Alpha.parser(
                "S := #'[0-9]+' - ('11' | '13')",
                ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXCLUSION));
        System.out.println(p6);
        System.out.println("---");
        System.out.println(p6.parse("12"));
        System.out.println("---");
        System.out.println(p6.parse("11"));
    }
    @Test void exclusionFullTest() {
        var p6 = Alpha.parser(
                "S := #'[0-9]+' - '11'",
                ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXCLUSION));
        System.out.println(p6);
        System.out.println("---");
        System.out.println(p6.parse("12"));
        System.out.println("---");
        System.out.println(p6.parse("11"));
    }
    @Test
    void exclusionTest() {
        var p6 = Alpha.parser(
                "S := #'[0-9]+' - '11' 'a'",
                ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXCLUSION));
        System.out.println(p6);
        System.out.println("---");
        System.out.println(p6.parse("12a"));
        System.out.println("---");
        System.out.println(p6.parse("12"));
        System.out.println("---");
        System.out.println(p6.parse("11a"));
        System.out.println("---");
        System.out.println(p6.parse("ba"));
    }

    @Test
    void oneOrMoreRepetitionReplacements() {

//        var p5 = Alpha.parser("S := &'ab' ('a' | 'b')+", ParserCreationOptions.getDefault().withCorrectnessCheck(false));
//        System.out.println(p5.parse("aba"));

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
