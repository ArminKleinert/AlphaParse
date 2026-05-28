package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParseTreeSpanTest {
    @Test
    void testBasics() {
        var p = Alpha.parser("S = 'b' A 'n'\nA = 'Aa'");
        var tree = p.parse("bAan").castToParseSuccess();

        Assertions.assertEquals(
                ParseTree.create("S", "b", ParseTree.create("A", "Aa"), "n"),
                tree);
        Assertions.assertEquals(0, tree.getSpanStart());
        Assertions.assertEquals(4, tree.getSpanEndExclusive());

        var spannedStringInTree = tree.containedString("bAan");
        Assertions.assertTrue(spannedStringInTree.isPresent());
        Assertions.assertEquals("bAan", spannedStringInTree.get());
    }

    @Test
    void testInSubTree() {
        var p = Alpha.parser("S = 'b' A 'n'\nA = 'Aa'");
        var tree = p.parse("bAan").castToParseSuccess();
        var subTree = (ParseTree) tree.getContent().get(1).content();

        Assertions.assertEquals(ParseTree.create("A", "Aa"), subTree);
        Assertions.assertEquals(1, subTree.getSpanStart());
        Assertions.assertEquals(3, subTree.getSpanEndExclusive());

        var spannedStringInSubTree = subTree.containedString("bAan");
        Assertions.assertTrue(spannedStringInSubTree.isPresent());
        Assertions.assertEquals("Aa", spannedStringInSubTree.get());
    }

    @Test
    void testEmptySpan() {
        var p = Alpha.parser("S = ε");
        var tree = p.parse("").castToParseSuccess();

        Assertions.assertEquals(0, tree.getSpanStart());
        Assertions.assertEquals(0, tree.getSpanEndExclusive());
    }

    @Test
    void testNoSpan() {
        var tree = ParseTree.create("S", "a", "b", "cdef");

        Assertions.assertTrue(tree.getSpanStart() < 0);
        Assertions.assertTrue(tree.getSpanEndExclusive() < 0);
        Assertions.assertTrue(tree.containedString("abcdef").isEmpty());
    }

    @Test
    void testInvalidStringCheck() {
        var p = Alpha.parser("S = 'b' A 'n'\nA = 'Aa'");
        var tree = p.parse("bAan").castToParseSuccess();
        Assertions.assertThrows(IllegalArgumentException.class, () -> tree.containedString("ban"));
    }

    @Test
    void testOtherString() {
        var p = Alpha.parser("S = 'b' A 'n'\nA = 'Aa'");
        var tree = p.parse("bAan").castToParseSuccess();
        Assertions.assertEquals("aaaa", tree.containedString("aaaaaaan").orElseThrow());
    }
}
