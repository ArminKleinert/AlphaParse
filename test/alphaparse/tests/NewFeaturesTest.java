package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.parsing.*;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewFeaturesTest {
    @Test void simple() {
        var g = """
                Expression = Term , { ( '+' | '-' ) , Term } ;
                Term       = Factor , { ( '*' | '/' ) , Factor } ;
                Factor     = Number | '(', Expression, ')' ;
                Number     = ['+' | '-' ] Digit , { Digit } ;
                Digit      = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' ;
                """;
        var p = Alpha.parser(g);
        System.out.println(p.parse("(8-9)*-20/18+1"));
    }
    @Test void repRepTest() {
        var p = Alpha.parser("S = 0*4A\n<A> = 'a'");
        Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
        Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
        Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
        Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), p.parse("aaa"));
        Assertions.assertEquals(ParseTree.create("S", "a", "a", "a", "a"), p.parse("aaaa"));
    }
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
}
