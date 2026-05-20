package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.error.ParserCreationFailure;
import alphaparse.parser.Parser;
import alphaparse.parser_options.*;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
class RegexTest {
    @Test void testNum() {
        var ruleTypes = Set.of(RulesAvailable.REGEX);
        var opts = ParserCreationOptions.pureEbnf().withRulesAvailable(ruleTypes);
        var p = Alpha.parser("S : #\"[a-fA-F0-9]+\"", opts);
        Assertions.assertEquals(
                ParseTree.create("S", "7F"),
                p.parse("7F")
        );
    }
    @Test void testInvalid() {
        var ruleTypes = Set.<RulesAvailable>of();
        var opts = ParserCreationOptions.pureEbnf().withRulesAvailable(ruleTypes);
        Assertions.assertThrows(
                ParserCreationFailure.class,
                ()-> Alpha.parser("S : #\"[a-fA-F0-9]+\"", opts));
    }
}
