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
        var p = Alpha.parser("S : 'A' 'B'");
        {
            var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(new ParseTree("S", "A", "B"), res);
        }
    }

    @Test
    void parseSimpleString() {
        var p = Alpha.parser("S : 'AB'");
        {
            var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(new ParseTree("S", "AB"), res);
        }
    }

    @Test
    void parsePartial() {
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
        final var possibleResult1 =
                new ParseTree(
                        "S",
                        new ParseTree("S", "A"),
                        new ParseTree("S", new ParseTree("S", "B"), new ParseTree("S", "A"))
                );
        final var possibleResult2 =
                new ParseTree(
                        "S",
                        new ParseTree("S", new ParseTree("S", "A"), new ParseTree("S", "B")),
                        new ParseTree("S", "A")
                );
        final var possibleResults = Set.of(possibleResult1, possibleResult2);

        var p = Alpha.parser("S : 'A' | 'B' | S S");
        var res = Alpha.parses(p, "ABA");

        // Using Sets because the order of results is implementation-dependent when using choice combinators.
        Assertions.assertEquals(possibleResults, new HashSet<>(res));
    }

    @Test
    void parsesWithOrderedChoice() {
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
        final var possibleResults = List.of(possibleResult1, possibleResult2);

        var p = Alpha.parser("S : 'A' / 'B' / S S");
        var res = Alpha.parses(p, "ABA");

        Assertions.assertEquals(possibleResults, res);
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