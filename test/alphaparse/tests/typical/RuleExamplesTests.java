package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.parser_options.RedefinitionOption;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.testutil.PT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

class RuleExamplesTests {
    @Test
    void testChoiceExample1() {
        {
            var p = Alpha.parser("S = 'a' | 'b' | 'ab'");
            Assertions.assertEquals(PT.create("S", "a"), p.parse("a"));
            Assertions.assertEquals(PT.create("S", "b"), p.parse("b"));
            Assertions.assertEquals(PT.create("S", "ab"), p.parse("ab"));
        }
        {
            var opts = ParserCreationOptions
                    .getDefault()
                    .withRedefinitionOption(RedefinitionOption.CHOICE)
                    .withRuleDefinitionOps(Stream.concat(ParserCreationOptions.defaultRuleDefinitionOps().stream(), Stream.of("=/")).toList());
            var p = Alpha.parser("""
                    S =  'a'
                    S =/ 'b'
                    S =/ 'ab'
                    """, opts);
            Assertions.assertEquals(PT.create("S", "a"), p.parse("a"));
            Assertions.assertEquals(PT.create("S", "b"), p.parse("b"));
            Assertions.assertEquals(PT.create("S", "ab"), p.parse("ab"));
        }
    }
}
