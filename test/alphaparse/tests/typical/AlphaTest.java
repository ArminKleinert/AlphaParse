package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.error.ParserCreationFailure;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.parser_options.UnhideOptions;
import alphaparse.result.Node;
import alphaparse.result.ParseTree;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AlphaTest {
    @Test
    void simplifiedParseTreeCreation() {
        var pt1 = ParseTree.create("S", "a", "a");
        var pt2 = ParseTree.create(new Node.NodeTreeTag(Sym.sym("S")), List.of(Node.of("a"), Node.of("a")));
        Assertions.assertEquals(pt2, pt1);
    }

    @Test
    void singleOrDoubleQuotationEquivalenceForStrings() {
        var pSingleQuoted = """
                S = 'a' 'b"c\\''
                """;
        var pDoubleQuoted = """
                S = "a" "b\\"c'"
                """;

        // Valid parse
        Assertions.assertEquals(
                Alpha.parser(pSingleQuoted).parse("ab\"c'"),
                Alpha.parser(pDoubleQuoted).parse("ab\"c'"));

        // Invalid parse
        Assertions.assertEquals(
                Alpha.parser(pSingleQuoted).parse(""),
                Alpha.parser(pDoubleQuoted).parse(""));

        // The parsers are the same on the inside.
        Assertions.assertEquals(
                Alpha.parser(pSingleQuoted),
                Alpha.parser(pDoubleQuoted));
    }

    @Test
    void singleOrDoubleQuotationEquivalenceForRegexes() {
        var pSingleQuoted = """
                S = #'a' #'b"c\\''
                """;
        var pDoubleQuoted = """
                S = #"a" #"b\\"c'"
                """;

        // Valid parse
        Assertions.assertEquals(
                Alpha.parser(pSingleQuoted).parse("ab\"c'"),
                Alpha.parser(pDoubleQuoted).parse("ab\"c'"));

        // Invalid parse
        Assertions.assertEquals(
                Alpha.parser(pSingleQuoted).parse(""),
                Alpha.parser(pDoubleQuoted).parse(""));

        // The parsers are the same on the inside.
        Assertions.assertEquals(
                Alpha.parser(pSingleQuoted),
                Alpha.parser(pDoubleQuoted));
    }

    @Test
    void testParserCreationNewWithStandardWhitespace() {
        var p = Alpha.parser(
                "S = ('a' | 'b')*",
                ParserCreationOptions.newWithStandardWhitespace()
        );
        var tree = ParseTree.create("S", "a", "b", "a", "b", "a");
        Assertions.assertEquals(tree, p.parse("a b      a\tb\na"));
    }

    @Test
    void testUnhideOptionsNone() {
        var p = Alpha.parser("S = 'a' <B> C <D> 'a'\nB = 'b'+\n<C> = 'c'\n<D> = 'd'");
        var opts = ParsingOptions.getDefault().withUnhide(UnhideOptions.NONE);
        var tree = ParseTree.create("S", "a", "c", "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testUnhideOptionsTags() {
        var p = Alpha.parser("S = 'a' <B> C <D> 'a'\nB = 'b'+\n<C> = 'c'\n<D> = 'd'");
        var opts = ParsingOptions.getDefault().withUnhide(UnhideOptions.TAGS);
        var tree = ParseTree.create("S", "a", ParseTree.create("C", "c"), "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testUnhideOptionsContent() {
        var p = Alpha.parser("S = 'a' <B> C <D> 'a'\nB = 'b'+\n<C> = 'c'\n<D> = 'd'");
        var opts = ParsingOptions.getDefault().withUnhide(UnhideOptions.CONTENT);
        var tree = ParseTree.create("S", "a", ParseTree.create("B", "b"), "c", "d", "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testUnhideOptionsAll() {
        var p = Alpha.parser("S = 'a' <B> C <D> 'a'\nB = 'b'+\n<C> = 'c'\n<D> = 'd'");
        var opts = ParsingOptions.getDefault().withUnhide(UnhideOptions.ALL);
        var tree = ParseTree.create("S", "a", ParseTree.create("B", "b"), ParseTree.create("C", "c"), ParseTree.create("D", "d"), "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testUnhideOptionsInOneCase() {
        var p = Alpha.parser("S = 'a' <B> C <D> 'a'\nB = 'b'+\n<C> = 'c'\n<D> = 'd'");

        Assertions.assertEquals(
                ParseTree.create("S", "a", "c", "a"),
                Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.NONE)));

        Assertions.assertEquals(
                ParseTree.create("S", "a", ParseTree.create("C", "c"), "a"),
                Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.TAGS)));

        Assertions.assertEquals(
                ParseTree.create("S", "a", ParseTree.create("B", "b"), "c", "d", "a"),
                Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.CONTENT)));

        Assertions.assertEquals(
                ParseTree.create("S",
                        "a",
                        ParseTree.create("B", "b"),
                        ParseTree.create("C", "c"),
                        ParseTree.create("D", "d"),
                        "a"),
                Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.ALL)));
    }

    @Test
    void testPartialParseOptionIgnoredOnSingleParse() {
        {
            var p = Alpha.parser("S = 'a'+");
            var opts = ParsingOptions.getDefault().withPartial(true);
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa", opts));
        }
        {
            var p = Alpha.parser("S = 'a'");
            var opts = ParsingOptions.getDefault().withPartial(true);
            Assertions.assertTrue(p.parse("aa", opts).isFailure());
        }
    }

    @Test
    void testPartialParseOptionIfNotInGrammar() {
        {
            var p = Alpha.parser("S = 'a'");
            var opts = ParsingOptions.getDefault().withPartial(true);
            Assertions.assertEquals(List.of(ParseTree.create("S", "a")), p.parses("aa", opts));
        }
    }

    @Test
    void parserCreationWithExplicitStartProduction() {
        {
            final var opts = ParserCreationOptions.getDefault().withStartProduction(Sym.sym("B"));
            final @NotNull var p = Alpha.parser("A = 'a'\nB = 'b'", opts);

            Assertions.assertEquals(p.startProduction(), opts.startProduction());

            Assertions.assertTrue(Alpha.parse(p, "a").isFailure());
            Assertions.assertEquals(ParseTree.create("B", "b"), Alpha.parse(p, "b"));
        }
        {
            // The production is not in the grammar => Fail
            final var opts = ParserCreationOptions.getDefault().withStartProduction(Sym.sym("B"));
            Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser("A = 'a'", opts));
        }
    }

    @Test
    void parseWithExplicitStartProduction() {
        {
            final @NotNull var p = Alpha.parser("A = 'a'\nB = 'b'");

            final var opts = ParsingOptions.getDefault().withStart(Sym.sym("B"));

            Assertions.assertTrue(Alpha.parse(p, "b").isFailure());
            Assertions.assertEquals(ParseTree.create("B", "b"), Alpha.parse(p, "b", opts));
        }
        {
            // The production is not in the grammar => Fail
            final var opts = ParsingOptions.getDefault().withStart(Sym.sym("B"));
            final @NotNull var p = Alpha.parser("A = 'a'");
            Assertions.assertThrows(ParserCreationFailure.class, () -> Alpha.parse(p, "a", opts));
        }
    }

    @Test
    void parse() {
        final @NotNull var p = Alpha.parser("S = 'A' | 'B' | S S");
        {
            final @NotNull var res = Alpha.parse(p, "A");
            Assertions.assertEquals(ParseTree.create("S", "A"), res);
        }
        {
            final @NotNull var res = Alpha.parse(p, "B");
            Assertions.assertEquals(ParseTree.create("S", "B"), res);
        }
        {
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(
                    ParseTree.create("S", ParseTree.create("S", "A"), ParseTree.create("S", "B")),
                    res);
        }
    }

    @Test
    void parseCat() {
        {
            final @NotNull var p = Alpha.parser("S = 'A' 'B'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S = 'A' 'B' S | ε");
            Assertions.assertEquals(ParseTree.create("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(ParseTree.create("S", "A", "B", ParseTree.create("S")), Alpha.parse(p, "AB"));
        }
        {
            final @NotNull var p = Alpha.parser("S = 'a' 'a' 'a'");
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertTrue(p.parse("aa").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), p.parse("aaa"));
            Assertions.assertTrue(p.parse("aaaa").isFailure());
        }
    }

    @Test
    void parsePlus() {
        {
            final @NotNull var p = Alpha.parser("S = 'a'+");
            Assertions.assertTrue(Alpha.parse(p, "").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a"), Alpha.parse(p, "a"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), Alpha.parse(p, "aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), Alpha.parse(p, "aaa"));
        }
        {
            final @NotNull var p = Alpha.parser("S = ('a' | 'b')+");
            Assertions.assertTrue(Alpha.parse(p, "").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "b"), Alpha.parse(p, "b"));
            Assertions.assertEquals(ParseTree.create("S", "a", "b", "a"), Alpha.parse(p, "aba"));
        }
    }

    @Test
    void parseStar() {
        {
            final @NotNull var p = Alpha.parser("S = 'a'*");
            Assertions.assertEquals(ParseTree.create("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(ParseTree.create("S", "a"), Alpha.parse(p, "a"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), Alpha.parse(p, "aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), Alpha.parse(p, "aaa"));
        }
        {
            final @NotNull var p = Alpha.parser("S = ('a' | 'b')*");
            Assertions.assertEquals(ParseTree.create("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(ParseTree.create("S", "b"), Alpha.parse(p, "b"));
            Assertions.assertEquals(ParseTree.create("S", "a", "b", "a"), Alpha.parse(p, "aba"));
        }
    }

    @Test
    void parseSimpleComplex() {
        {
            final @NotNull var p = Alpha.parser("S = ε | S");
            var forest = p.parses("").castToParsesSuccess();
            Assertions.assertEquals(
                    List.of(
                            ParseTree.create("S"), ParseTree.create("S", ParseTree.create("S")),
                            ParseTree.create("S", ParseTree.create("S", ParseTree.create("S"))),
                            ParseTree.create("S", ParseTree.create("S", ParseTree.create("S", ParseTree.create("S"))))
                    ),
                    forest.stream().limit(4).toList());
        }
    }

    @Test
    void parseSimpleString() {
        {
            final @NotNull var p = Alpha.parser("S = 'AB'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(ParseTree.create("S", "AB"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S = ''");
            final @NotNull var res = Alpha.parse(p, "");
            Assertions.assertEquals(ParseTree.create("S"), res);
        }
    }

    @Test
    void parsePartial() {
        {
            final @NotNull var p = Alpha.parser("S = ''");
            final @NotNull var res = Alpha.parse(p, "");
            Assertions.assertEquals(ParseTree.create("S"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S = 'AB'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(ParseTree.create("S", "AB"), res);
        }
    }

    @Test
    void parsesWithChoice() {
        {
            final @NotNull var p = Alpha.parser("S = 'A' | 'B' | S S");
            final @NotNull var res = Alpha.parses(p, "ABA");
            final var possibleResults = new HashSet<>(sabssPossibleResults());

            // Using Sets because the order of results is implementation-dependent when using alternation rules.
            Assertions.assertEquals(possibleResults, new HashSet<>(res));
        }
    }

    @Test
    void parsesWithChoiceEps() {
        {
            final @NotNull var p = Alpha.parser("S = ε | A | B | C\nA = C \nB = C \nC = ε");
            final @NotNull var possibleTrees = Set.of(
                    ParseTree.create("S"),
                    ParseTree.create("S", ParseTree.create("C")),
                    ParseTree.create("S", ParseTree.create("A", ParseTree.create("C"))),
                    ParseTree.create("S", ParseTree.create("B", ParseTree.create("C")))
            );
            Assertions.assertEquals(possibleTrees, new HashSet<>(Alpha.parses(p, "")));
        }
        {
            final @NotNull var grammar = """
                    S  = (r1 | r2 | r3)* | ε
                    r1 = 'a'
                    r2 = 'a'
                    r3 = 'a'
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
            final @NotNull var p = Alpha.parser("S = 'A' / 'B' / S S");
            final @NotNull var res = Alpha.parses(p, "ABA");
            final @NotNull var possibleResults = sabssPossibleResults();

            Assertions.assertEquals(possibleResults, res);
        }
        {
            final @NotNull var p = Alpha.parser("""
                    S = A / B / ε / C
                    A = C
                    B = C
                    C = ε
                    """);
            final @NotNull var possibleTrees = List.of(
                    ParseTree.create("S", ParseTree.create("A", ParseTree.create("C"))),
                    ParseTree.create("S", ParseTree.create("B", ParseTree.create("C"))),
                    ParseTree.create("S"),
                    ParseTree.create("S", ParseTree.create("C"))
            );
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, ""));
        }
        {
            final @NotNull var p = Alpha.parser("S = 'a' / ε / 'a'");
            final @NotNull var possibleTrees = List.of(ParseTree.create("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            final @NotNull var p = Alpha.parser("S = ε / 'a' / 'a' / ε");
            final @NotNull var possibleTrees = List.of(ParseTree.create("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            final @NotNull var grammar = """
                    S = (r1 / r2 / r3)*
                    r1 = 'a'
                    r2 = 'a'
                    r3 = 'a'
                    """;
            final @NotNull var text = "a";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = Alpha.parses(p, text);
            final @NotNull var possibleParses = List.of(
                    ParseTree.create("S", ParseTree.create("r1", "a")),
                    ParseTree.create("S", ParseTree.create("r2", "a")),
                    ParseTree.create("S", ParseTree.create("r3", "a"))
            );
            Assertions.assertEquals(possibleParses, ps);
        }
        {
            final @NotNull var grammar = """
                    S = (r1 / r2)*
                    r1 = 'a'
                    r2 = 'a'
                    """;
            final @NotNull var text = "aa";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = Alpha.parses(p, text);
            final @NotNull var possibleParses = List.of(
                    ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r1", "a")),
                    ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r1", "a")),
                    ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r2", "a")),
                    ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r2", "a"))
            );
            Assertions.assertEquals(possibleParses, ps);
        }
    }

    private @NotNull @Unmodifiable List<ParseTree> r1r2r3Results() {
        return List.of(
                ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r1", "a")),
                ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r1", "a")),
                ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r2", "a")),
                ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r2", "a")),
                ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r3", "a")),
                ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r3", "a")),
                ParseTree.create("S", ParseTree.create("r3", "a"), ParseTree.create("r3", "a")),
                ParseTree.create("S", ParseTree.create("r3", "a"), ParseTree.create("r2", "a")),
                ParseTree.create("S", ParseTree.create("r3", "a"), ParseTree.create("r1", "a"))
        );
    }

    private @NotNull @Unmodifiable List<ParseTree> sabssPossibleResults() {
        return List.of(
                ParseTree.create(
                        "S",
                        ParseTree.create("S", "A"),
                        ParseTree.create("S", ParseTree.create("S", "B"), ParseTree.create("S", "A"))
                ),
                ParseTree.create(
                        "S",
                        ParseTree.create("S", ParseTree.create("S", "A"), ParseTree.create("S", "B")),
                        ParseTree.create("S", "A")
                )
        );
    }

    @Test
    void parsesPartial() {
        {
            final @NotNull var grammar = """
                    S  = (r1 / r2 / r3)*
                    r1 = 'a'
                    r2 = 'a'
                    r3 = 'a'
                    """;
            final @NotNull var text = "aa";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = Alpha.parses(p, text, new ParsingOptions(null, true, UnhideOptions.NONE, false, false));
            final @NotNull var possibleParses = partialParsesOrderedR123();
            Assertions.assertEquals(possibleParses, ps);
        }
        {
            final @NotNull var grammar = """
                    S  = (r1 | r2 | r3)*
                    r1 = 'a'
                    r2 = 'a'
                    r3 = 'a'
                    """;
            final @NotNull var text = "aa";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var ps = new HashSet<>(Alpha.parses(p, text, new ParsingOptions(null, true, UnhideOptions.NONE, false, false)));
            final @NotNull var possibleParses = new HashSet<>(partialParsesOrderedR123());
            Assertions.assertEquals(possibleParses, ps);
        }
    }

    private @NotNull @Unmodifiable List<ParseTree> partialParsesOrderedR123() {
        return List.of(
                ParseTree.create("S"),
                ParseTree.create("S", ParseTree.create("r1", "a")),
                ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r1", "a")),
                ParseTree.create("S", ParseTree.create("r2", "a")),
                ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r1", "a")),
                ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r2", "a")),
                ParseTree.create("S", ParseTree.create("r3", "a")),
                ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r2", "a")),
                ParseTree.create("S", ParseTree.create("r2", "a"), ParseTree.create("r3", "a")),
                ParseTree.create("S", ParseTree.create("r1", "a"), ParseTree.create("r3", "a")),
                ParseTree.create("S", ParseTree.create("r3", "a"), ParseTree.create("r3", "a")),
                ParseTree.create("S", ParseTree.create("r3", "a"), ParseTree.create("r2", "a")),
                ParseTree.create("S", ParseTree.create("r3", "a"), ParseTree.create("r1", "a"))
        );
    }

    @Test
    void parserWithStart() {
        final @NotNull var p = Alpha.parser("S1 = 'A'\nS2 = 'B'");
        Assertions.assertEquals(ParseTree.create("S1", "A"), p.parse("A"));
        Assertions.assertTrue(p.parse("B").isFailure());

        var parserWithOtherStart = p.withStartProduction(Sym.sym("S2"));
        Assertions.assertTrue(parserWithOtherStart.parse("A").isFailure());
        Assertions.assertEquals(ParseTree.create("S2", "B"), parserWithOtherStart.parse("B"));
    }

    @Test
    void parseWithStart() {
        final @NotNull var p = Alpha.parser("S1 = 'A'\nS2 = 'B'");
        Assertions.assertEquals(ParseTree.create("S1", "A"), p.parse("A"));
        Assertions.assertTrue(p.parse("B").isFailure());

        var opts = ParsingOptions.getDefault().withStart(Sym.sym("S2"));
        Assertions.assertTrue(p.parse("A", opts).isFailure());
        Assertions.assertEquals(ParseTree.create("S2", "B"), p.parse("B", opts));
    }
}