package alphaparse;

import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ObnoxiousTestCase {
    @Test
    void SS() {
        {
            var p = Alpha.parser("S : S");
            Assertions.assertThrows(Exception.class, () -> p.parse("a"));
        }
        {
            var p = Alpha.parser("S : S | 'a'");
            Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
        }
    }

    @Test
    void SEps() {
        var p = Alpha.parser("S : S");
        IO2.println(p.parses(""));
    }
}
