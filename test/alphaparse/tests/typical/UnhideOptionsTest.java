package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.parser_options.Unhide;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UnhideOptionsTest {

    @Test
    void testUnhide1() {
        final @NotNull Parser p = Alpha.parser("""
                S   = A <B>
                <A> = 'a'
                B   = 'b'
                """);
        var text = "ab";

        Assertions.assertEquals(
                ParseTree.create("S", "a"),
                p.parse(text));
        Assertions.assertEquals(
                ParseTree.create("S", "a", ParseTree.create("B", "b")),
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.CONTENT)));
        Assertions.assertEquals(
                ParseTree.create("S", ParseTree.create("A", "a")),
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.TAGS)));
        Assertions.assertEquals(
                ParseTree.create("S", ParseTree.create("A", "a"), ParseTree.create("B", "b")),
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.ALL)));
    }
}
