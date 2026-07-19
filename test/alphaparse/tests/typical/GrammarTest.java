package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.parsing.EpsilonTerm;
import alphaparse.parsing.NonTerminal;
import alphaparse.parsing.StringTerm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class GrammarTest {
    @Test
    void grammarCollect() {
        var g = Alpha.parser("S = '1' | '2' S").grammar();
        var ga = g.analyze();

        Assertions.assertTrue(ga.isValid());

        Assertions.assertEquals(Set.of(Sym.sym("S")), new HashSet<>(ga.usedNTs()));
        Assertions.assertEquals(Set.of(Sym.sym("S")), new HashSet<>(ga.definedNTs()));

        Assertions.assertEquals(
                Set.of(StringTerm.create("1", false), StringTerm.create("2", false)),
                new HashSet<>(ga.collect(it -> it instanceof StringTerm)));
    }

    @Test
    void grammarAnalysisDoesNotRateStartSymAsUnused() {
        var ga = Alpha.parser("""
                S = A | "a"
                A = A epsilon | epsilon
                B = "b"
                C = "c"
                """).grammar().analyze();
        Assertions.assertFalse(ga.getUnusedNTs().contains(Sym.sym("S")));

        var ga2 = Alpha.parser("S = 'a'").grammar().analyze();
        Assertions.assertFalse(ga2.getUnusedNTs().contains(Sym.sym("S")));
    }

    @Test
    void grammarAnalysis() {
        var ga = Alpha.parser("""
                S = A | "a"
                A = A epsilon | epsilon
                B = "b"
                C = "c"
                """).grammar().analyze();
        Assertions.assertEquals(Set.of(Sym.sym("S"), Sym.sym("A"), Sym.sym("B"), Sym.sym("C")), ga.definedNTs());
        Assertions.assertEquals(Set.of(Sym.sym("A")), ga.usedNTs());
        Assertions.assertEquals(Set.of(Sym.sym("B"), Sym.sym("C")), ga.getUnusedNTs());
        Assertions.assertFalse(ga.getUnusedNTs().contains(Sym.sym("S")));
        Assertions.assertTrue(ga.isValid());
    }

    @Test
    void grammarSubsetFullTest() {
        var ga = Alpha.parser("""
                S = A | "a"
                A = A B | epsilon
                B = C
                C = "c"
                """).grammar().analyze();
        Assertions.assertEquals(
                ga.grammar().keySet(),
                ga.reachableSubset(Sym.sym("S")));
    }

    @Test
    void grammarSubsetPartialTest() {
        var ga = Alpha.parser("""
                S = A | "a"
                A = A | C
                B = C
                C = "c"
                """).grammar().analyze();
        Assertions.assertEquals(
                Set.of(Sym.sym("S"), Sym.sym("A"), Sym.sym("C")),
                ga.reachableSubset(Sym.sym("S"))
        );
    }
}
