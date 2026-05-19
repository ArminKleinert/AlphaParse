package alphaparse.tests;

import alphaparse.Sym;
import alphaparse.result.ParseTree;
import alphaparse.util.Conversions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

 class ConvTest {
    @Test
    void listToTreeTest() {
        var l = List.of(Sym.sym("S"), "A", List.of(Sym.sym("S"), "A"));
        var pt = Conversions.toParseTree(l);
        Assertions.assertEquals(
                ParseTree.create("S", "A", ParseTree.create("S", "A")),
                pt
        );
    }

    @Test
    void mapToTreeTest() {
        var l = Map.of(
                Sym.sym("tag"), Sym.sym("S"),
                Sym.sym("content"), List.of("A", Map.of(Sym.sym("tag"), Sym.sym("S"), Sym.sym("content"), List.of("A"))));
        var pt = Conversions.toParseTree(l);
        Assertions.assertEquals(
                ParseTree.create("S", "A", ParseTree.create("S", "A")),
                pt
        );
    }

    @Test
    void mixedToTreeTest() {
        {
            var l = Map.of(
                    Sym.sym("tag"), Sym.sym("S"),
                    Sym.sym("content"), List.of("A", List.of(Sym.sym("S"), "A")));
            var pt = Conversions.toParseTree(l);
            Assertions.assertEquals(
                    ParseTree.create("S", "A", ParseTree.create("S", "A")),
                    pt
            );
        }
        {
            var l = List.of(Sym.sym("S"), "A", Map.of(Sym.sym("tag"), Sym.sym("S"), Sym.sym("content"), List.of("A")));
            var pt = Conversions.toParseTree(l);
            Assertions.assertEquals(
                    ParseTree.create("S", "A", ParseTree.create("S", "A")),
                    pt
            );
        }
    }
}
