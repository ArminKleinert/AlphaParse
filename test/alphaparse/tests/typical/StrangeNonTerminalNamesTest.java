package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.parsing.EOFTerm;
import alphaparse.testutil.PT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class StrangeNonTerminalNamesTest {
    @Test
    void nonTerminalStartsWithEpsilonName() {
        var opts = ParserCreationOptions.getDefault().withEpsilonNames(List.of("Eps"));

        Assertions.assertDoesNotThrow(()-> Alpha.parser("S = EpsNT\nEpsNT = \"1\"", opts));

        var p = Alpha.parser("S = EpsNT\nEpsNT = \"1\"", opts);
        Assertions.assertEquals(
                PT.create("S", PT.create("EpsNT", "1")),
                p.parse("1")
        );
    }
    @Test
    void nonTerminalStartsWithEofName() {
        var opts = ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXPLICIT_EOF);

        Assertions.assertDoesNotThrow(()-> Alpha.parser(
                "S = " + EOFTerm.text() + "NT\n" + EOFTerm.text()+"NT = \"1\"",
                opts));

        var p = Alpha.parser("S = EpsNT\nEpsNT = \"1\"", opts);
        Assertions.assertEquals(
                PT.create("S", PT.create("EpsNT", "1")),
                p.parse("1")
        );
    }
}
