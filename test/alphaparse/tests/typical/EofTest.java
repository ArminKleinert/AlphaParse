package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.parsing.EOFTerm;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EofTest {
    @Test
    void basicTest1() {
        var opts = ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXPLICIT_EOF);

        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = " + EOFTerm.text, opts).parse(""));

        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = <' '>" + EOFTerm.text, opts).parse(" "));

        Assertions.assertEquals(
                ParseTree.create("S", "a"),
                Alpha.parser("S = 'a' " + EOFTerm.text, opts).parse("a"));
    }

    @Test
    void eofInParserWithWhitespace() {
        var opts = ParserCreationOptions.newWithStandardWhitespace().addAvailableRule(RulesAvailable.EXPLICIT_EOF);

        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = " + EOFTerm.text, opts).parse(""));

        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = " + EOFTerm.text, opts).parse(" "));
    }
}
