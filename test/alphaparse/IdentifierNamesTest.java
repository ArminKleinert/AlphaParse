package alphaparse;

import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.util.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdentifierNamesTest {
    @Test
    void extendedNamesDisallowed() {
        var opts = ParserCreationOptions.getDefault().withRulesAvailable(
                Utils.remove(ParserCreationOptions.getDefault().usableRules(), RulesAvailable.EXTENDED_IDENTIFIERS)
        );
        Assertions.assertDoesNotThrow(()->Alpha.parser("S : 'a'", opts));
        Assertions.assertDoesNotThrow(()->Alpha.parser("S : 'a123b'", opts));
        Assertions.assertDoesNotThrow(()->Alpha.parser("S : 'a_b'", opts));
        Assertions.assertThrows(Exception.class, ()->Alpha.parser("1 : 'a'", opts));
        Assertions.assertThrows(Exception.class, ()->Alpha.parser("\uD83C\uDF81 : 'a'", opts));
    }
    @Test
    void extendedNamesAllowed() {
        var opts = ParserCreationOptions.getDefault().withRulesAvailable(
                Utils.cons(ParserCreationOptions.getDefault().usableRules(), RulesAvailable.EXTENDED_IDENTIFIERS)
        );
        Assertions.assertThrows(Exception.class, ()->Alpha.parser("1 : 'a'", opts));
        Assertions.assertDoesNotThrow(()->Alpha.parser("S : 'a'", opts));
        Assertions.assertDoesNotThrow(()->Alpha.parser("\uD83C\uDF81 : 'a'", opts));
        Assertions.assertDoesNotThrow(()->Alpha.parser("a123 : 'a'", opts));
        Assertions.assertDoesNotThrow(()->Alpha.parser("a_123 : 'a'", opts));
    }
}
