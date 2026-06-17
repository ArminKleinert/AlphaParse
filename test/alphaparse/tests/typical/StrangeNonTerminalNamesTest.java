package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.result.PT;
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
}
