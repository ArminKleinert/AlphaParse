package alphaparse;

import alphaparse.result.Node;
import alphaparse.result.ParseTree;

import java.util.HashSet;
import java.util.Set;

import alphaparse.util.ClassUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AlphaTest {
    @Test
    void outputForTemps() {
        {
        }
    }

//    @Test
//    void testMostDerived() {
//        IO2.println(ClassUtil.mostDerived(List.of("abc", "abc")));
//        IO2.println(ClassUtil.mostDerived(List.of(new StringBuilder("abc"), new StringBuffer("abc"))));
//        IO2.println(ClassUtil.mostDerived(List.of(new StringBuilder("abc"), "abc")));
//    }

    @Test
    void simplifiedParseTreeCreation() {
        var pt1 = ParseTree.create("S", "a", "a");
        var pt2 = ParseTree.create(new Node.NodeTreeTag(Keyword.intern("S")), List.of(Node.of("a"), Node.of("a")));
        Assertions.assertEquals(pt2, pt1);
    }

    @Test
    void singleOrDoubleQuotationEquivalenceForStrings() {
        var pSingleQuoted = """
                S : 'a' 'b"c\\''
                """;
        var pDoubleQuoted = """
                S : "a" "b\\"c'"
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
                S : #'a' #'b"c\\''
                """;
        var pDoubleQuoted = """
                S : #"a" #"b\\"c'"
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
    void testOptimizeMemoryDoesNotChangeOutput() {
        var p = Alpha.parser("S : 'u' (('a'+ | #'b*') / C)\n<C> : 1*3 'c'");
        var opt = Alpha.ParsingOptions.getDefault().withOptimizeMemory(true);

        Assertions.assertTrue(p.parse("", opt).isFailure());
        Assertions.assertEquals(p.parse(""), p.parse("", opt));

        Assertions.assertEquals(p.parse("u"), p.parse("u", opt));
        Assertions.assertEquals(p.parse("ua"), p.parse("ua", opt));
        Assertions.assertEquals(p.parse("ucc"), p.parse("ucc", opt));
        Assertions.assertEquals(p.parse("ucccc"), p.parse("ucccc", opt));
    }

    @Test
    void testParserCreationNewWithStandardWhitespace() {
        var p = Alpha.parser(
                "S : ('a' | 'b')*",
                Alpha.ParserCreationOptions.newWithStandardWhitespace()
        );
        var tree = ParseTree.create("S", "a", "b", "a", "b", "a");
        IO2.println(p.parse("a b      a\tb\na"));
        Assertions.assertEquals(tree, p.parse("a b      a\tb\na"));
    }

    @Test
    void testUnhideOptionsNone() {
        var p = Alpha.parser("S : 'a' <B> C <D> 'a'\nB : 'b'+\n<C> : 'c'\n<D> : 'd'");
        var opts = Alpha.ParsingOptions.getDefault().withUnhide(Alpha.UnhideOptions.none);
        var tree = ParseTree.create("S", "a", "c", "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testUnhideOptionsTags() {
        var p = Alpha.parser("S : 'a' <B> C <D> 'a'\nB : 'b'+\n<C> : 'c'\n<D> : 'd'");
        var opts = Alpha.ParsingOptions.getDefault().withUnhide(Alpha.UnhideOptions.tags);
        var tree = ParseTree.create("S", "a", ParseTree.create("C", "c"), "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testUnhideOptionsContent() {
        var p = Alpha.parser("S : 'a' <B> C <D> 'a'\nB : 'b'+\n<C> : 'c'\n<D> : 'd'");
        var opts = Alpha.ParsingOptions.getDefault().withUnhide(Alpha.UnhideOptions.content);
        var tree = ParseTree.create("S", "a", ParseTree.create("B", "b"), "c", "d", "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testUnhideOptionsAll() {
        var p = Alpha.parser("S : 'a' <B> C <D> 'a'\nB : 'b'+\n<C> : 'c'\n<D> : 'd'");
        var opts = Alpha.ParsingOptions.getDefault().withUnhide(Alpha.UnhideOptions.all);
        var tree = ParseTree.create("S", "a", ParseTree.create("B", "b"), ParseTree.create("C", "c"), ParseTree.create("D", "d"), "a");
        Assertions.assertEquals(tree, Alpha.parse(p, "abcda", opts));
    }

    @Test
    void testPartialParseOptionIgnoredOnSingleParse() {
        {
            var p = Alpha.parser("S = 'a'+");
            var opts = Alpha.ParsingOptions.getDefault().withPartial(true);
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa", opts));
        }
        {
            var p = Alpha.parser("S = 'a'");
            var opts = Alpha.ParsingOptions.getDefault().withPartial(true);
            Assertions.assertTrue(p.parse("aa", opts).isFailure());
        }
    }

    @Test
    void testPartialParseOptionIfNotInGrammar() {
        {
            var p = Alpha.parser("S = 'a'");
            var opts = Alpha.ParsingOptions.getDefault().withPartial(true);
            Assertions.assertEquals(List.of(ParseTree.create("S", "a")), p.parses("aa", opts));
        }
    }

    @Test
    void parserCreationWithExplicitStartProduction() {
        {
            final var opts = Alpha.ParserCreationOptions.getDefault().withStartProduction(Keyword.intern("B"));
            final @NotNull var p = Alpha.parser("A : 'a'\nB : 'b'", opts);

            Assertions.assertEquals(p.startProduction(), opts.startProduction());

            Assertions.assertTrue(Alpha.parse(p, "a").isFailure());
            Assertions.assertEquals(ParseTree.create("B", "b"), Alpha.parse(p, "b"));
        }
        {
            // The production is not in the grammar => Fail
            final var opts = Alpha.ParserCreationOptions.getDefault().withStartProduction(Keyword.intern("B"));
            Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser("A : 'a'", opts));
        }
    }

    @Test
    void parseWithExplicitStartProduction() {
        {
            final @NotNull var p = Alpha.parser("A : 'a'\nB : 'b'");

            final var opts = Alpha.ParsingOptions.getDefault().withStart(Keyword.intern("B"));

            Assertions.assertTrue(Alpha.parse(p, "b").isFailure());
            Assertions.assertEquals(ParseTree.create("B", "b"), Alpha.parse(p, "b", opts));
        }
        {
            // The production is not in the grammar => Fail
            final var opts = Alpha.ParsingOptions.getDefault().withStart(Keyword.intern("B"));
            final @NotNull var p = Alpha.parser("A : 'a'");
            Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parse(p, "a", opts));
        }
    }

    @Test
    void parseRepetitionMinimumOnly() {
        {
            final @NotNull var p = Alpha.parser("S : 2* 'a'");
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), p.parse("aaa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a", "a"), p.parse("aaaa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a", "a", "a"), p.parse("aaaaa"));
        }
    }

    @Test
    void parseRepetitionMaximumOnly() {
        {
            final @NotNull var p = Alpha.parser("S : *2 'a'");
            Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
            Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
        {
            final @NotNull var p = Alpha.parser("S : *0 'a'");
            Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertTrue(p.parse("aa").isFailure());
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
    }

    @Test
    void parseRepetitionMinMax() {
        {
            final @NotNull var p = Alpha.parser("S : 2*4 'a'");
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), p.parse("aaa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a", "a"), p.parse("aaaa"));
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
        {
            final @NotNull var p = Alpha.parser("S : 0*0 'a'");
            Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertTrue(p.parse("aa").isFailure());
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
            Assertions.assertTrue(p.parse("aaaaa").isFailure());
        }
    }

    @Test
    void parseRepetitionExact() {
        {
            final @NotNull var p = Alpha.parser("S : 2 'a'");
            Assertions.assertTrue(p.parse("").isFailure());
            Assertions.assertTrue(p.parse("a").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), p.parse("aa"));
            Assertions.assertTrue(p.parse("aaa").isFailure());
            Assertions.assertTrue(p.parse("aaaa").isFailure());
        }
    }

    @Test
    void createRepetitionParserFormatFailure() {
        // Negative minimum
        Assertions.assertThrows(IllegalStateException.class, () -> Alpha.parser("S : -1*2"));
        // Negative maximum
        Assertions.assertThrows(IllegalStateException.class, () -> Alpha.parser("S : *-1"));
        // Negative minimum
        Assertions.assertThrows(IllegalStateException.class, () -> Alpha.parser("S : -1*"));
        // Negative exact
        Assertions.assertThrows(IllegalStateException.class, () -> Alpha.parser("S : -1"));
        // Minimum greater than maximum
        Assertions.assertThrows(IllegalStateException.class, () -> Alpha.parser("S : 4*2"));
    }

    @Test
    void createRepetitionParserFailure() {
        // Negative minimum
        Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser("S : -1*2 'a'"));
        // Negative maximum
        Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser("S : *-1 'a'"));
        // Negative minimum
        Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser("S : -1* 'a'"));
        // Negative exact
        Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser("S : -1 'a'"));
        // Minimum greater than maximum
        Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser("S : 4*2 'a'"));
    }

    @Test
    void parse() {
        final @NotNull var p = Alpha.parser("S : 'A' | 'B' | S S");
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
            final @NotNull var p = Alpha.parser("S : 'A' 'B'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(ParseTree.create("S", "A", "B"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S : 'A' 'B' S | eps");
            Assertions.assertEquals(ParseTree.create("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(ParseTree.create("S", "A", "B", ParseTree.create("S")), Alpha.parse(p, "AB"));
        }
        {
            final @NotNull var p = Alpha.parser("S : 'a' 'a' 'a'");
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
            final @NotNull var p = Alpha.parser("S : 'a'+");
            Assertions.assertTrue(Alpha.parse(p, "").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "a"), Alpha.parse(p, "a"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), Alpha.parse(p, "aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), Alpha.parse(p, "aaa"));
        }
        {
            final @NotNull var p = Alpha.parser("S : ('a' | 'b')+");
            Assertions.assertTrue(Alpha.parse(p, "").isFailure());
            Assertions.assertEquals(ParseTree.create("S", "b"), Alpha.parse(p, "b"));
            Assertions.assertEquals(ParseTree.create("S", "a", "b", "a"), Alpha.parse(p, "aba"));
        }
    }

    @Test
    void parseStar() {
        {
            final @NotNull var p = Alpha.parser("S : 'a'*");
            Assertions.assertEquals(ParseTree.create("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(ParseTree.create("S", "a"), Alpha.parse(p, "a"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a"), Alpha.parse(p, "aa"));
            Assertions.assertEquals(ParseTree.create("S", "a", "a", "a"), Alpha.parse(p, "aaa"));
        }
        {
            final @NotNull var p = Alpha.parser("S : ('a' | 'b')*");
            Assertions.assertEquals(ParseTree.create("S"), Alpha.parse(p, ""));
            Assertions.assertEquals(ParseTree.create("S", "b"), Alpha.parse(p, "b"));
            Assertions.assertEquals(ParseTree.create("S", "a", "b", "a"), Alpha.parse(p, "aba"));
        }
    }

    @Test
    void parseSimpleComplex() {
        {
            final @NotNull var p = Alpha.parser("S : epsilon | S");
            var forest = p.parses("").castToParsesSuccess();
            IO2.println(forest.stream().limit(5).toList());
        }
    }

    @Test
    void parseSimpleString() {
        {
            final @NotNull var p = Alpha.parser("S : 'AB'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(ParseTree.create("S", "AB"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S : ''");
            final @NotNull var res = Alpha.parse(p, "");
            Assertions.assertEquals(ParseTree.create("S"), res);
        }
    }

    @Test
    void parsePartial() {
        {
            final @NotNull var p = Alpha.parser("S : ''");
            final @NotNull var res = Alpha.parse(p, "");
            Assertions.assertEquals(ParseTree.create("S"), res);
        }
        {
            final @NotNull var p = Alpha.parser("S : 'AB'");
            final @NotNull var res = Alpha.parse(p, "AB");
            Assertions.assertEquals(ParseTree.create("S", "AB"), res);
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
                    ParseTree.create("S"),
                    ParseTree.create("S", ParseTree.create("C")),
                    ParseTree.create("S", ParseTree.create("A", ParseTree.create("C"))),
                    ParseTree.create("S", ParseTree.create("B", ParseTree.create("C")))
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

            Assertions.assertEquals(possibleResults, res);
        }
        {
            final @NotNull var p = Alpha.parser("""
                    S : A / B / eps / C
                    A : C
                    B : C
                    C : eps
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
            final @NotNull var p = Alpha.parser("S : 'a' / eps / 'a'");
            final @NotNull var possibleTrees = List.of(ParseTree.create("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            final @NotNull var p = Alpha.parser("S : eps / 'a' / 'a' / eps");
            final @NotNull var possibleTrees = List.of(ParseTree.create("S", "a"));
            Assertions.assertEquals(possibleTrees, Alpha.parses(p, "a"));
        }
        {
            final @NotNull var grammar = """
                    S : (r1 / r2 / r3)*
                    r1 : 'a'
                    r2 : 'a'
                    r3 : 'a'
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
                    S : (r1 / r2)*
                    r1 : 'a'
                    r2 : 'a'
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
        final @NotNull var p = Alpha.parser("S1 : 'A'\nS2 : 'B'");
        Assertions.assertEquals(ParseTree.create("S1", "A"), p.parse("A"));
        Assertions.assertTrue(p.parse("B").isFailure());

        var parserWithOtherStart = p.withStartProduction(Keyword.intern("S2"));
        Assertions.assertTrue(parserWithOtherStart.parse("A").isFailure());
        Assertions.assertEquals(ParseTree.create("S2", "B"), parserWithOtherStart.parse("B"));
    }

    @Test
    void parseWithStart() {
        final @NotNull var p = Alpha.parser("S1 : 'A'\nS2 : 'B'");
        Assertions.assertEquals(ParseTree.create("S1", "A"), p.parse("A"));
        Assertions.assertTrue(p.parse("B").isFailure());

        var opts = Alpha.ParsingOptions.getDefault().withStart(Keyword.intern("S2"));
        Assertions.assertTrue(p.parse("A", opts).isFailure());
        Assertions.assertEquals(ParseTree.create("S2", "B"), p.parse("B", opts));
    }
}