package alphaparse;

import alphaparse.result.ParseTree;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AlphaTest {

    @Test
    void parse() {
        var p = Alpha.parser("S : 'A' | 'B' | S S");
        {
            var res = Alpha.parse(p, "A");
            Assertions.assertEquals(new ParseTree("S", "A"), res);
        }
        {
            var res = Alpha.parse(p, "B");
            Assertions.assertEquals(new ParseTree("S", "B"), res);
        }
        {
            var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(
                    new ParseTree("S", new ParseTree("S", "A"), new ParseTree("S", "B")),
                    res);
        }
    }

    @Test
    void parseCat() {
        {
            var p = Alpha.parser("S : 'A' 'B'");
            var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(new ParseTree("S", "A", "B"), res);
        }
        {
            var p = Alpha.parser("S : 'A' 'B' S | eps");
            Assertions.assertEquals(new ParseTree("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(new ParseTree("S", "A", "B", new ParseTree("S")), Alpha.parse(p, "AB"));
        }
    }

    @Test
    void parseSimpleString() {
        {
            var p = Alpha.parser("S : 'AB'");
            var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(new ParseTree("S", "AB"), res);
        }
        {
            var p = Alpha.parser("S : ''");
            var res = Alpha.parse(p, "");
            Assertions.assertEquals(new ParseTree("S"), res);
        }
    }

    @Test
    void parsePartial() {
        {
            var p = Alpha.parser("S : ''");
            var res = Alpha.parse(p, "");
            Assertions.assertEquals(new ParseTree("S"), res);
        }
        {
            var p = Alpha.parser("S : 'AB'");
            var res = Alpha.parse(p, "AB");
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
        var p = Alpha.parser("S : 'A' | 'B' | S S");
        var res = Alpha.parses(p, "ABA");
        final var possibleResults = new HashSet<>(sabssPossibleResults());

        // Using Sets because the order of results is implementation-dependent when using choice combinators.
        Assertions.assertEquals(possibleResults, new HashSet<>(res));
    }

    @Test
    void parsesWithChoiceEps() {
        {
            var p = Alpha.parser("S : eps | A | B | C\nA : C \nB : C \nC : eps");
            var possibleTrees = Set.of(
                    new ParseTree("S"),
                    new ParseTree("S", new ParseTree("C")),
                    new ParseTree("S", new ParseTree("A", new ParseTree("C"))),
                    new ParseTree("S", new ParseTree("B", new ParseTree("C")))
            );
            Assertions.assertEquals(possibleTrees, new HashSet<>(Alpha.parses(p, "")));
        }
        {
            var grammar = """
                    S : (r1 | r2 | r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            var text = "aa";
            var p = Alpha.parser(grammar);
            var ps = new HashSet<>(Alpha.parses(p, text));
            var possibleParses = Set.of(
                    new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r3", "a")),
                    new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r2", "a")),
                    new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r2", "a")),
                    new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r1", "a")),
                    new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r2", "a")),
                    new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r1", "a")),
                    new ParseTree("S", new ParseTree("r3", "a"), new ParseTree("r1", "a")),
                    new ParseTree("S", new ParseTree("r2", "a"), new ParseTree("r3", "a")),
                    new ParseTree("S", new ParseTree("r1", "a"), new ParseTree("r3", "a"))
            );
            Assertions.assertEquals(possibleParses, ps);
        }
    }

    @Test
    void parsesWithOrderedChoice() {
        {
            var p = Alpha.parser("S : 'A' / 'B' / S S");
            var res = Alpha.parses(p, "ABA");
            var possibleResults = sabssPossibleResults();

            Assertions.assertEquals(possibleResults.getFirst(), res.getFirst());
            Assertions.assertEquals(possibleResults, res);
        }
        {
            var p = Alpha.parser("S : A / B / eps / C\nA : C \nB : C \nC : eps");
            var possibleTrees = List.of(
                    new ParseTree("S", new ParseTree("A", new ParseTree("C"))),
                    new ParseTree("S", new ParseTree("B", new ParseTree("C"))),
                    new ParseTree("S"),
                    new ParseTree("S", new ParseTree("C"))
            );
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, ""));
        }
        {
            var p = Alpha.parser("S : 'a' / eps / 'a'");
            var possibleTrees = List.of(new ParseTree("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            var p = Alpha.parser("S : eps / 'a' / 'a' / eps");
            var possibleTrees = List.of(new ParseTree("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            var grammar = """
                    S : (r1 / r2 / r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            var text = "aa";
            var p = Alpha.parser(grammar);
            var ps = Alpha.parses(p, text);
            var possibleParses = List.of(
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
            Assertions.assertEquals(possibleParses, ps);
        }
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
            var grammar = """
                    S : (r1 | r2 | r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            var text = "aa";
            var p = Alpha.parser(grammar);
            var ps = new HashSet<>(Alpha.parses(p, text, new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false)));
            var possibleParses = new HashSet<>(partialParsesOrderedR123());
            Assertions.assertEquals(possibleParses, ps);
        }{
            var grammar = """
                    S : (r1 / r2 / r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
                    """;
            var text = "aa";
            var p = Alpha.parser(grammar);
            var ps = Alpha.parses(p, text, new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false));
            var possibleParses = partialParsesOrderedR123();
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

    @Test
    void parsesWithOptions() {
    }

    @Test
    void parsesTotalSuccess() {
    }

    @Test
    void parsesTotalFailure() {
    }


    @Test
    void parsesOrFailureSuccess() {
    }

    @Test
    void parsesOrFailureFailure() {
    }

    @Test
    void parserFrom() {
    }

    @Test
    void parserFromString() {
    }

    @Test
    void parserFromFile() {
    }

    @Test
    void parserFromStringWithOptions() {
    }


    @Test
    void parserFromFileWithOptions() {
    }

    @Test
    void parserFromGrammarWithOptions() {
    }
}