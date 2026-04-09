package alphaparse;

import alphaparse.result.ParseTree;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AlphaTest {

    @Test
    void parse() {
        final @NotNull var p = Alpha.parser("S : 'A' | 'B' | S S");
        {
            final @NotNull var res = Alpha.parse(p, "A");
            Assertions.assertEquals(new ParseTree("S", "A"), res);
        }
        {
            final @NotNull var res = Alpha.parse(p, "B");
            Assertions.assertEquals(new ParseTree("S", "B"), res);
        }
        {
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(
                    new ParseTree("S", new ParseTree("S", "A"), new ParseTree("S", "B")),
                    res);
        }
    }

    @Test
    void parseCat() {
        {
            final @NotNull var p = Alpha.parser("S : 'A' 'B'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(new ParseTree("S", "A", "B"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S : 'A' 'B' S | eps");
            Assertions.assertEquals(new ParseTree("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(new ParseTree("S", "A", "B", new ParseTree("S")), Alpha.parse(p, "AB"));
        }
    }

    @Test
    void parseSimpleString() {
        {
            final @NotNull var p = Alpha.parser("S : 'AB'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(new ParseTree("S", "AB"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S : ''");
            final @NotNull var res = Alpha.parse(p, "");
            Assertions.assertEquals(new ParseTree("S"), res);
        }
    }

    @Test
    void parsePartial() {
        {
            final @NotNull var p = Alpha.parser("S : ''");
            final @NotNull var res = Alpha.parse(p, "");
            Assertions.assertEquals(new ParseTree("S"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S : 'AB'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(new ParseTree("S", "AB"), res);
        }
    }

    @Test
    void parseFailure() {
    }

    @Test
    void parseWithOptions() {
    }

    @Test
    void parseTotal() {
    }

    @Test
    void parsesWithChoice() {
        {
            final @NotNull var p = Alpha.parser("S : 'A' | 'B' | S S");
            final @NotNull var res = Alpha.parses(p, "ABA");
            final var possibleResults = new HashSet<>(sabssPossibleResults());

            // Using Sets because the order of results is implementation-dependent when using choice combinators.
            Assertions.assertEquals(possibleResults, new HashSet<>(res));
        }
    }

    @Test
    void parsesWithChoiceEps() {
        {
            final @NotNull var p = Alpha.parser("S : eps | A | B | C\nA : C \nB : C \nC : eps");
            final @NotNull var possibleTrees = Set.of(
                    new ParseTree("S"),
                    new ParseTree("S", new ParseTree("C")),
                    new ParseTree("S", new ParseTree("A", new ParseTree("C"))),
                    new ParseTree("S", new ParseTree("B", new ParseTree("C")))
            );
            Assertions.assertEquals(possibleTrees, new HashSet<>(Alpha.parses(p, "")));
        }
        {
            final @NotNull var grammar = """
                    S : (r1 | r2 | r3)* | eps
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            final @NotNull var text = "aa";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = new HashSet<>(Alpha.parses(p, text));
            final @NotNull var possibleParses = new HashSet<>(r1r2r3Results());
            Assertions.assertEquals(possibleParses, ps);
        }
    }

    @Test
    void parsesWithOrderedChoice() {
        {
            final @NotNull var p = Alpha.parser("S : 'A' / 'B' / S S");
            final @NotNull var res = Alpha.parses(p, "ABA");
            final @NotNull var possibleResults = sabssPossibleResults();

            Assertions.assertEquals(possibleResults.getFirst(), res.getFirst());
            Assertions.assertEquals(possibleResults, res);
        }
        {
            final @NotNull var p = Alpha.parser("S : A / B / eps / C\nA : C \nB : C \nC : eps");
            final @NotNull var possibleTrees = List.of(
                    new ParseTree("S", new ParseTree("A", new ParseTree("C"))),
                    new ParseTree("S", new ParseTree("B", new ParseTree("C"))),
                    new ParseTree("S"),
                    new ParseTree("S", new ParseTree("C"))
            );
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, ""));
        }
        {
            final @NotNull var p = Alpha.parser("S : 'a' / eps / 'a'");
            final @NotNull var possibleTrees = List.of(new ParseTree("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            final @NotNull var p = Alpha.parser("S : eps / 'a' / 'a' / eps");
            final @NotNull var possibleTrees = List.of(new ParseTree("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            final @NotNull var grammar = """
                    S : (r1 / r2 / r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            final @NotNull var text = "aa";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = Alpha.parses(p, text);
            final @NotNull var possibleParses =  r1r2r3Results();
            Assertions.assertEquals(possibleParses, ps);
        }
    }

    private List<ParseTree> r1r2r3Results() {
        return List.of(
                new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r1", "a")),
                new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r1", "a")),
                new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r2", "a")),
                new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r2", "a")),
                new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r3", "a")),
                new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r3", "a")),
                new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r3", "a")),
                new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r2", "a")),
                new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r1", "a"))
        );
    }

    private List<ParseTree> sabssPossibleResults() {
        final var possibleResult1 =
                new ParseTree(
                        "S",
                        new ParseTree("S", "A"),
                        new ParseTree("S", new ParseTree("S", "B"), new ParseTree("S", "A")
                        ));
        final var possibleResult2 =
                new ParseTree(
                        "S",
                        new ParseTree("S", new ParseTree("S", "A"), new ParseTree("S", "B")),
                        new ParseTree("S", "A")
                );
        return List.of(possibleResult1, possibleResult2);
    }

    @Test void parsesPartial() {
        {
            final @NotNull var grammar = """
                    S : (r1 | r2 | r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            final @NotNull var text = "aa";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = new HashSet<>(Alpha.parses(p, text, new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false)));
            final @NotNull var possibleParses = new HashSet<>(partialParsesOrderedR123());
            Assertions.assertEquals(possibleParses, ps);
        }{
            final @NotNull var grammar = """
                    S : (r1 / r2 / r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            final @NotNull var text = "aa";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = Alpha.parses(p, text, new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false));
            final @NotNull var possibleParses = partialParsesOrderedR123();
            Assertions.assertEquals(possibleParses, ps);
        }
    }

    private List<ParseTree> partialParsesOrderedR123() {
        return List.of(
                new ParseTree("S"),
                new ParseTree("S", new ParseTree("r1", "a")),
                new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r1", "a")),
                new ParseTree("S", new ParseTree("r2", "a")),
                new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r1", "a")),
                new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r2", "a")),
                new ParseTree("S", new ParseTree("r3", "a")),
                new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r2", "a")),
                new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r3", "a")),
                new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r3", "a")),
                new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r3", "a")),
                new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r2", "a")),
                new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r1", "a"))
        );
    }
}