package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import org.junit.jupiter.api.Test;

class NegativeLookaheadTest {
    @Test void test(){}

    @Test void identifierButNotType(){
        var p = Alpha.parser(
                "S := !type #'[a-zA-Z][a-zA-Z0-9_]+'\n"+"type := 'int' ε",
                ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXPLICIT_EOF));
//        System.out.println(p.parse("int"));
//        System.out.println(p.parse("int1"));
//        System.out.println(p.parse("iint"));
    }
    @Test void eofTest(){
        var p = Alpha.parser(
                "S := !('int' (#'\\s')) #'[a-zA-Z]+'",
                ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXPLICIT_EOF).withCorrectnessCheck(false));
//        System.out.println(p.parse("integer"));
//        System.out.println(p.parse("int"));
//        System.out.println(p.parse(""));
    }
}