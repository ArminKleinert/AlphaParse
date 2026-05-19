package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringTest {

    @Test
    void explicitStringCaseInsensitivity() {
        Assertions.assertEquals(
                ParseTree.create("S", "A"),
                Alpha.parser("S = %i\"A\"", ParserCreationOptions.ABNF()).parse("A"));
        Assertions.assertEquals(
                ParseTree.create("S", "A"),
                Alpha.parser("S = %i\"A\"", ParserCreationOptions.ABNF()).parse("a"));
    }

    @Test
    void explicitStringCaseSensitivity() {
        Assertions.assertEquals(
                ParseTree.create("S", "A"),
                Alpha.parser("S = %s\"A\"", ParserCreationOptions.ABNF()).parse("A"));
        Assertions.assertTrue(
                Alpha.parser("S = %s\"A\"", ParserCreationOptions.ABNF()).parse("a").isFailure());
    }
}
