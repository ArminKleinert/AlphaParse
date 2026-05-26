package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.parsing.TerminalStringCombinator;
import alphaparse.result.ParseTree;
import alphaparse.util.Conversions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class GrammarTest {
   @Test
   void grammarCollectRules() {
       var g = Alpha.parser("S := '1' | '2' S").grammar();
       var ga = g.analyze();

       Assertions.assertTrue(ga.isValid());

       Assertions.assertEquals(Set.of(Sym.sym("S")), new HashSet<>(ga.usedNTs()));
       Assertions.assertEquals(Set.of(Sym.sym("S")), new HashSet<>(ga.definedNTs()));

       Assertions.assertEquals(
               Set.of(new TerminalStringCombinator("1", false),
                       new TerminalStringCombinator("2", false)),
               new HashSet<>(ga.collectRules(it -> it instanceof TerminalStringCombinator)));
   }
}
