package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.parser.Parser;
import alphaparse.parser_options.*;
import alphaparse.result.PT;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class AlphaCoreTest {

    final @NotNull Parser as_and_bs = Alpha.parser(
            """
                    S = AB*
                    AB = A B
                    A = 'a'+
                    B = 'b'+
                    """);

    final @NotNull Parser as_and_bs_regex = Alpha.parser(
            """
                    S = AB*
                    AB = A B
                    A = #'a'+
                    B = #'b'+
                    """);

    final @NotNull Parser as_and_bs_variation1 = Alpha.parser(
            """
                    S = AB*
                    AB = 'a'+ 'b'+
                    """);

    final @NotNull Parser as_and_bs_variation2 = Alpha.parser(
            """
                    S = ('a'+ 'b'+)*
                    """);

    final @NotNull Parser paren_ab = Alpha.parser(
            """
                    paren-wrapped = '(' seq-of-A-or-B ')'
                    seq-of-A-or-B = ('a' | 'b')*
                    """);

    final @NotNull Parser paren_ab_hide_parens = Alpha.parser(
            """
                    paren-wrapped = <'('> seq-of-A-or-B <')'>
                    seq-of-A-or-B = ('a' | 'b')*
                    """);

    final @NotNull Parser paren_ab_manually_flattened = Alpha.parser(
            """
                    paren-wrapped = <'('> ('a' | 'b')* <')'>
                    """);

    final @NotNull Parser paren_ab_hide_tag = Alpha.parser(
            """
                    paren-wrapped = <'('> seq-of-A-or-B <')'>
                    <seq-of-A-or-B> = ('a' | 'b')*
                    """);

    final @NotNull Parser paren_ab_hide_both_tags = Alpha.parser(
            """
                    <paren-wrapped> = <'('> seq-of-A-or-B <')'>
                    <seq-of-A-or-B> = ('a' | 'b')*
                    """);

    final @NotNull Parser words_and_numbers = Alpha.parser(
            """
                    sentence = token (<whitespace> token)*
                    <token> = word | number
                    whitespace = #'\\s+'
                    word = #'[a-zA-Z]+'
                    number = #'[0-9]+'
                    """);

    final @NotNull Parser words_and_numbers_one_character_at_a_time = Alpha.parser(
            """
                    sentence = token (<whitespace> token)*
                    <token> = word | number
                    whitespace = #'\\s+'
                    word = letter+
                    number = digit+
                    <letter> = #'[a-zA-Z]'
                    <digit> = #'[0-9]'
                    """);

    final @NotNull Parser ambiguous = Alpha.parser(
            """
                    S = A A
                    A = 'a'*
                    """);

    final @NotNull Parser not_ambiguous = Alpha.parser(
            """
                    S = A A
                    A = #'a*'
                    """);


    final @NotNull Parser lookahead_example = Alpha.parser(
            """
                    S = &'ab' ('a' | 'b')+
                    """);

    final @NotNull Parser negative_lookahead_example = Alpha.parser(
            """
                    S = !'ab' ('a' | 'b')+
                    """);

    final @NotNull Parser ambiguous_tokenizer = Alpha.parser(
            """
                    sentence = token (<whitespace> token)*
                    <token> = keyword | identifier
                    whitespace = #'\\s+'
                    identifier = #'[a-zA-Z]+'
                    keyword = 'cond' | 'defn'
                    """);

    final @NotNull Parser unambiguous_tokenizer = Alpha.parser(
            """
                    sentence = token (<whitespace> token)*
                    <token> = keyword | !keyword identifier
                    whitespace = #'\\s+'
                    identifier = #'[a-zA-Z]+'
                    keyword = 'cond' | 'defn'
                    """);

    final @NotNull Parser preferential_tokenizer = Alpha.parser(
            """
                    sentence = token (<whitespace> token)*
                    <token> = keyword / identifier
                    whitespace = #'\\s+'
                    identifier = #'[a-zA-Z]+'
                    keyword = 'cond' | 'defn'
                    """);

    final @NotNull Parser arithmetic = Alpha.parser(
            """
                    expr = add-sub
                    <add-sub> = mul-div | add | sub
                    add = add-sub <'+'> mul-div
                    sub = add-sub <'-'> mul-div
                    <mul-div> = term | mul | div
                    mul = mul-div <'*'> term
                    div = mul-div <'/'> term
                    <term> = number | <'('> add-sub <')'>
                    number = #'[0-9]+'
                    """);

    final @NotNull Parser tricky_ebnf_build = Alpha.parser("""
                    S = A | B
                    <A> = '='*
                    B = 'b' '='
                    """,
            ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S"))
    );

    final @NotNull Parser whitespace = Alpha.parser(
            """
                    whitespace = #'\\s+'
                    """);

    final @NotNull Parser auto_whitespace_example = Alpha.parser(
            """
                    S = A B
                    <A> = 'foo'
                    <B> = #'\\d+'
                    """,
            ParserCreationOptions.getDefault().withWhitespaceParser(whitespace));

    final @NotNull Parser words_and_numbers_auto_whitespace = Alpha.parser(
            """
                    sentence = token+
                    <token> = word | number
                    word = #'[a-zA-Z]+'
                    number = #'[0-9]+'
                    """,
            ParserCreationOptions.getDefault().withWhitespaceParser(whitespace));

    final @NotNull Parser auto_whitespace_example2 = Alpha.parser(
            """
                    S = A B
                    <A> = 'foo'
                    <B> = #'\\d+'
                    """,
            ParserCreationOptions.getDefault().withWhitespaceParser(
                    Alpha.getPredefinedWhitespaceParser("standard")));

    final @NotNull Parser words_and_numbers_auto_whitespace2 = Alpha.parser(
            """
                    sentence = token+
                    <token> = word | number
                    word = #'[a-zA-Z]+'
                    number = #'[0-9]+'
                    """,
            ParserCreationOptions.getDefault().withWhitespaceParser(
                    Alpha.getPredefinedWhitespaceParser("standard")));

    final @NotNull Parser whitespace_or_comments = Alpha.parser(
            """
                    ws-or-comments = #'\\s+' | comments
                    comments = comment+
                    comment = '(*' inside-comment* '*)'
                    inside-comment =  !'*)' !'(*' #'.' | comment
                    """,
            ParserCreationOptions.getDefault().withWhitespaceParser(whitespace));

    final @NotNull Parser words_and_numbers_auto_whitespace_and_comments = Alpha.parser(
            """
                    sentence = token+
                    <token> = word | number
                    word = #'[a-zA-Z]+'
                    number = #'[0-9]+'
                    """,
            ParserCreationOptions.getDefault().withWhitespaceParser(whitespace_or_comments));

    final @NotNull Parser eat_a = Alpha.parser("Aeater = #'[a]'+",
            ParserCreationOptions.getDefault());

    final @NotNull Parser int_or_double = Alpha.parser(
            """
                    ws = #'\\s+';
                    Int = #'[0-9]+';
                    Double = #'[0-9]+\\.[0-9]*|\\.[0-9]+';
                    <ConstExpr> = Int | Double;
                    Input = ConstExpr <ws> ConstExpr;
                    """,
            ParserCreationOptions.getDefault().withStartProduction(Sym.sym("Input")));

    final @NotNull Parser case_insensitive_regexp = Alpha.parser(
            """
                    S = #'(?i)a+'
                    """);

    @Test
    void testAsAndBs() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree tree = PT.create(
                "S",
                PT.create("AB", PT.create("A", "a", "a", "a", "a", "a"), PT.create("B", "b", "b", "b")),
                PT.create("AB", PT.create("A", "a", "a", "a", "a"), PT.create("B", "b", "b"))
        );

        Assertions.assertEquals(
                tree,
                Alpha.parse(as_and_bs, text)
        );

        Assertions.assertEquals(
                tree,
                as_and_bs.parse(text)
        );
    }

    @Test
    void testAsAndBsRegex() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree tree = PT.create(
                "S",
                PT.create("AB", PT.create("A", "a", "a", "a", "a", "a"), PT.create("B", "b", "b", "b")),
                PT.create("AB", PT.create("A", "a", "a", "a", "a"), PT.create("B", "b", "b"))
        );

        Assertions.assertEquals(
                tree,
                Alpha.parse(as_and_bs_regex, text)
        );

        Assertions.assertEquals(
                tree,
                as_and_bs_regex.parse(text)
        );
    }

    @Test
    void testAsAndBsHiccup() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree tree = PT.create(
                "S",
                PT.create("AB", PT.create("A", "a", "a", "a", "a", "a"), PT.create("B", "b", "b", "b")),
                PT.create("AB", PT.create("A", "a", "a", "a", "a"), PT.create("B", "b", "b"))
        );
        var abTag = Sym.sym("AB");
        var aTag = Sym.sym("A");
        var bTag = Sym.sym("B");
        final @NotNull List<Object> treeHiccup = List.of(
                Sym.sym("S"),
                List.of(abTag, List.of(aTag, "a", "a", "a", "a", "a"), List.of(bTag, "b", "b", "b")),
                List.of(abTag, List.of(aTag, "a", "a", "a", "a"), List.of(bTag, "b", "b"))
        );

        Assertions.assertEquals(
                treeHiccup,
                tree.toRawList()
        );

        Assertions.assertEquals(
                treeHiccup,
                Alpha.parse(as_and_bs, text).castToParseSuccess().toRawList()
        );
    }

    @Test
    void testAsAndBsVariation1() {
        final @NotNull String text = "aaaaabbbaaaabb";

        var res = PT.create("S",
                PT.create("AB", "a", "a", "a", "a", "a", "b", "b", "b"),
                PT.create("AB", "a", "a", "a", "a", "b", "b"));

        Assertions.assertEquals(
                res,
                as_and_bs_variation1.parse(text));
    }

    @Test
    void testAsAndBsVariation2() {
        final @NotNull String text = "aaaaabbbaaaabb";

        var res = PT.create(
                "S",
                "a", "a", "a", "a", "a", "b", "b", "b", "a", "a", "a", "a", "b", "b");
        var resList = List.of(
                Sym.sym("S"),
                "a", "a", "a", "a", "a", "b", "b", "b", "a", "a", "a", "a", "b", "b");

        Assertions.assertEquals(
                res,
                as_and_bs_variation2.parse(text));
        Assertions.assertEquals(
                resList,
                as_and_bs_variation2.parse(text).castToParseSuccess().toRawList());
    }

    @Test
    void testParenAb() {
        var text = "(aba)";
        var tree = PT.create("paren-wrapped",
                "(",
                PT.create("seq-of-A-or-B", "a", "b", "a"),
                ")");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab, text));

        Assertions.assertEquals(Alpha.parse(paren_ab, text), paren_ab.parse(text));
    }

    @Test
    void testParenAbHideParens() {
        var text = "(aba)";
        var tree = PT.create("paren-wrapped",
                PT.create("seq-of-A-or-B", "a", "b", "a"));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_parens, text));
        Assertions.assertEquals(Alpha.parse(paren_ab_hide_parens, text), paren_ab_hide_parens.parse(text));
    }

    @Test
    void testParenAbManuallyFlattened() {
        var text = "(aba)";
        var tree = PT.create("paren-wrapped", "a", "b", "a");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_manually_flattened, text));
        Assertions.assertEquals(Alpha.parse(paren_ab_manually_flattened, text), paren_ab_manually_flattened.parse(text));
    }

    @Test
    void testParenAbHideTag() {
        var text = "(aba)";
        var tree = PT.create("paren-wrapped", "a", "b", "a");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_tag, text));
        Assertions.assertEquals(Alpha.parse(paren_ab_hide_tag, text), paren_ab_hide_tag.parse(text));
    }

    @Test
    void testParenAbHideBothTags() {
        var text = "(aba)";

        // Sadly, AlphaParse can not output untagged trees.
        // The expected result for Instaparse is as follows:
        //    (paren-ab-hide-both-tags "(aba)") ;=> ("a" "b" "a")
        var tree = PT.create(ParseTree.NULL_TAG.content().name(), "a", "b", "a");

        // That raw output can be achieved by manual conversion:
        Assertions.assertEquals(List.of("a", "b", "a"), tree.toRawList());

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_both_tags, text));
        Assertions.assertEquals(Alpha.parse(paren_ab_hide_both_tags, text), paren_ab_hide_both_tags.parse(text));
    }

    @Test
    void testParserAsFunction() {
        var text = "aaaa";
        var grammar = "S = 'a' S / '' ";
        var tree =
                PT.create("S", "a", PT.create("S", "a", PT.create("S", "a", PT.create("S", "a", PT.create("S")))));

        Assertions.assertEquals(tree, Alpha.parser(grammar).parse(text));
    }

    @Test
    void testParserAsFunction2() {
        var text = "aaaa";
        var grammar = "S = S 'a' / ε";
        var tree =
                PT.create("S", PT.create("S", PT.create("S", PT.create("S", PT.create("S"), "a"), "a"), "a"), "a");

        Assertions.assertEquals(tree, Alpha.parser(grammar).parse(text));
    }

    @Test
    void testAmbiguousParses() {
        var text = "aaaaaa";

        var treesAmbiguous = List.of(
                PT.create("S", PT.create("A", "a"), PT.create("A", "a", "a", "a", "a", "a")),
                PT.create("S", PT.create("A", "a", "a", "a", "a", "a", "a"), PT.create("A")),
                PT.create("S", PT.create("A", "a", "a"), PT.create("A", "a", "a", "a", "a")),
                PT.create("S", PT.create("A", "a", "a", "a"), PT.create("A", "a", "a", "a")),
                PT.create("S", PT.create("A", "a", "a", "a", "a"), PT.create("A", "a", "a")),
                PT.create("S", PT.create("A", "a", "a", "a", "a", "a"), PT.create("A", "a")),
                PT.create("S", PT.create("A"), PT.create("A", "a", "a", "a", "a", "a", "a"))
        );

        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text));
        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text, ParsingOptions.getDefault()));

        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text));
        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text, ParsingOptions.getDefault()));
    }

    @Test
    void testUnambiguousParses() {
        var text = "aaaaaa";


        var treesUnambiguous = List.of(
                PT.create("S", PT.create("A", text), PT.create("A", ""))
        );

        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text));
        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text, ParsingOptions.getDefault()));

        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text));
        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text, ParsingOptions.getDefault()));
    }

    @Test
    void testLookaheadExample() {
        var text = "abaaaab";
        var tree = PT.create("S", "a", "b", "a", "a", "a", "a", "b");
        Assertions.assertEquals(tree, lookahead_example.parse(text));
        Assertions.assertEquals(tree, lookahead_example.parse(text, ParsingOptions.getDefault()));
    }

    @Test
    void testLookaheadExampleFailure() {
        var text = "bbaaaab";

        Assertions.assertFalse(lookahead_example.parse(text).isSuccess());
        Assertions.assertTrue(lookahead_example.parse(text).isFailure());
    }

    @Test
    void testNegativeLookaheadExample() {
        var text = "bbaaaab";
        var tree = PT.create("S", "b", "b", "a", "a", "a", "a", "b");

        Assertions.assertEquals(tree, negative_lookahead_example.parse(text));
    }

    @Test
    void testNegativeLookaheadExampleFailure() {
        var text = "abaaaab";

        Assertions.assertFalse(negative_lookahead_example.parse(text).isSuccess());
        Assertions.assertTrue(negative_lookahead_example.parse(text).isFailure());
    }

    @Test
    void testCharRangeExampleFailure() {
        var p = Alpha.parser("""
                Regex = (CharNonRange | Range) +
                Range = Char <'-'> Char
                CharNonRange = Char ! ('-' Char)
                Char = #'[-x]' | 'c' (! 'd') 'x'
                """);

        var tree = PT.create("Regex",
                PT.create("Range",
                        PT.create("Char", "x"),
                        PT.create("Char", "c", "x")));

        Assertions.assertEquals(
                tree,
                p.parse("x-cx")
        );
        Assertions.assertEquals(
                List.of(tree),
                p.parses("x-cx")
        );
    }

    @Test
    void testAmbiguousTokenizer() {
        var text = "defn my cond";
        var trees = Set.of(
                PT.create("sentence", PT.create("identifier", "defn"), PT.create("identifier", "my"), PT.create("identifier", "cond")),
                PT.create("sentence", PT.create("keyword", "defn"), PT.create("identifier", "my"), PT.create("identifier", "cond")),
                PT.create("sentence", PT.create("identifier", "defn"), PT.create("identifier", "my"), PT.create("keyword", "cond")),
                PT.create("sentence", PT.create("keyword", "defn"), PT.create("identifier", "my"), PT.create("keyword", "cond"))
        );

        Assertions.assertEquals(trees, new HashSet<>(Alpha.parses(ambiguous_tokenizer, text)));
    }

    @Test
    void testUnambiguousTokenizer() {
        var text = "defn my cond";
        var trees = List.of(
                PT.create("sentence", PT.create("keyword", "defn"), PT.create("identifier", "my"), PT.create("keyword", "cond"))
        );

        Assertions.assertEquals(trees, Alpha.parses(unambiguous_tokenizer, text));
    }

    @Test
    void testPreferentialTokenizer() {
        var text = "defn my cond";
        var trees = Set.of(
                PT.create("sentence", PT.create("keyword", "defn"), PT.create("identifier", "my"), PT.create("keyword", "cond")),

                PT.create("sentence", PT.create("identifier", "defn"), PT.create("identifier", "my"), PT.create("keyword", "cond")),

                PT.create("sentence", PT.create("keyword", "defn"), PT.create("identifier", "my"), PT.create("identifier", "cond")),

                PT.create("sentence", PT.create("identifier", "defn"), PT.create("identifier", "my"), PT.create("identifier", "cond"))
        );

        Assertions.assertEquals(trees, new HashSet<>(Alpha.parses(preferential_tokenizer, text)));
    }

    @Test
    void testRepeatedA() {
        var repeated_a = Alpha.parser("""
                S = 'a'+
                """);

        var text = "aaaaaa";
        var trees = List.of(
                PT.create("S", "a", "a", "a", "a", "a", "a")
        );

        Assertions.assertEquals(trees, Alpha.parses(repeated_a, text));

        var partialOpts = ParsingOptions.getDefault().withPartial(true);
        var treesPartial = List.of(
                PT.create("S", "a"),
                PT.create("S", "a", "a"),
                PT.create("S", "a", "a", "a"),
                PT.create("S", "a", "a", "a", "a"),
                PT.create("S", "a", "a", "a", "a", "a"),
                PT.create("S", "a", "a", "a", "a", "a", "a")
        );

        Assertions.assertEquals(treesPartial, Alpha.parses(repeated_a, text, partialOpts));
    }

    @Test
    void testWordsAndNumbersOneCharacterAtATime() {
        var text = "abc 123 def";
        var tree = PT.create("sentence",
                PT.create("word", "a", "b", "c"),
                PT.create("number", "1", "2", "3"),
                PT.create("word", "d", "e", "f"));

        Assertions.assertEquals(tree, Alpha.parse(words_and_numbers_one_character_at_a_time, text));
    }

    @Test
    void testArithmeticGrammar() {
        var text = "1-2/(3-4)+5*6";
        var tree = PT.create("expr",
                PT.create("add",
                        PT.create("sub",
                                PT.create("number", "1"),
                                PT.create("div",
                                        PT.create("number", "2"),
                                        PT.create("sub",
                                                PT.create("number", "3"),
                                                PT.create("number", "4")))),
                        PT.create("mul",
                                PT.create("number", "5"),
                                PT.create("number", "6")))
        );

        Assertions.assertEquals(tree, Alpha.parse(arithmetic, text));
    }

    /*
             (tricky-ebnf-build "===")
             [:S "=" "=" "="]

             (tricky-ebnf-build "b=")
             [:S [:B "b" "="]]
     */

    @Test
    void testTrickyEbnfBuild() {
        var text1 = "===";
        var text2 = "b=";
        var tree1 = PT.create("S", "=", "=", "=");
        var tree2 = PT.create("S", PT.create("B", "b", "="));

        Assertions.assertEquals(tree1, Alpha.parse(tricky_ebnf_build, text1));
        Assertions.assertEquals(tree2, Alpha.parse(tricky_ebnf_build, text2));
    }

    @Test
    void testFail() {
        Assertions.assertEquals(
                PT.create("S", "a"),
                Alpha.parser("S = A\n<A> = 'a'").parse("a"));
        Assertions.assertEquals(
                PT.create("S", PT.create("A")),
                Alpha.parser("S = A\nA = <'a'>").parse("a"));
    }

    @Test
    void testOptionalRepeat() {
        var parser = Alpha.parser("S = ('a'?)+");
        var tree = PT.create("S");

        Assertions.assertEquals(tree, parser.parse(""));
    }

    @Test
    void testDot() {
        var parser = Alpha.parser("""
                a = b c .
                b = 'b' .
                c = 'c' .
                """);
        var tree = PT.create("a",
                PT.create("b", "b"),
                PT.create("c", "c"));

        Assertions.assertEquals(tree, parser.parse("bc"));
    }

    @Test
    void testUnhide1() {
        var text = "(ababa)";
        var treeNormal = PT.create("paren-wrapped",
                PT.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"));
        var treeWParen = PT.create("paren-wrapped",
                "(",
                PT.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"),
                ")");
        var p = paren_ab_hide_parens;

        Assertions.assertEquals(treeNormal, p.parse(text));
        Assertions.assertEquals(treeWParen,
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.CONTENT)));
        Assertions.assertEquals(treeWParen,
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.ALL)));
        Assertions.assertEquals(treeNormal,
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.TAGS)));
    }

    @Test
    void testUnhide2() {
        var text = "(ababa)";
        var treeWTag = PT.create("paren-wrapped",
                PT.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"));
        var treeWAll = PT.create("paren-wrapped",
                "(",
                PT.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"),
                ")");
        var p = paren_ab_hide_tag;

        Assertions.assertEquals(treeWTag,
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.TAGS)));

        Assertions.assertEquals(treeWAll,
                p.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.ALL)));
    }

    @Test
    void testEpsEquivalence() {
        var opts = ParserCreationOptions
                .getDefault()
                .withEpsilonNames(List.of("Epsilon", "epsilon", "EPSILON", "eps", "ε"));
        Assertions.assertEquals(
                PT.create("S"),
                Alpha.parser("S = eps", opts).parse(""));
        Assertions.assertEquals(
                PT.create("S"),
                Alpha.parser("S = epsilon", opts).parse(""));
        Assertions.assertEquals(
                PT.create("S"),
                Alpha.parser("S = Epsilon", opts).parse(""));
        Assertions.assertEquals(
                PT.create("S"),
                Alpha.parser("S = EPSILON", opts).parse(""));
        Assertions.assertEquals(
                PT.create("S"),
                Alpha.parser("S = ε", opts).parse(""));
    }

    @Test
    void testEpsFail() {
        var opts = ParserCreationOptions
                .getDefault()
                .withEpsilonNames(List.of("Epsilon", "epsilon", "EPSILON", "eps", "ε"));
        Assertions.assertTrue(
                Alpha.parser("S = eps", opts)
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = epsilon", opts)
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = Epsilon", opts)
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = EPSILON", opts)
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = ε", opts)
                        .parse("a")
                        .isFailure());
    }

    @Test
    void testWordsAndNumbers() {
        var text = "ab 123 cd";
        var treeWithoutTokenTag = PT.create(
                "sentence",
                PT.create("word", "ab"),
                PT.create("number", "123"),
                PT.create("word", "cd")
        );
        var treeFull = PT.create(
                "sentence",
                PT.create("token", PT.create("word", "ab")),
                PT.create("whitespace", " "),
                PT.create("token", PT.create("number", "123")),
                PT.create("whitespace", " "),
                PT.create("token", PT.create("word", "cd"))
        );
        Assertions.assertEquals(treeWithoutTokenTag, words_and_numbers.parse(text));

        Assertions.assertEquals(treeFull, words_and_numbers.parse(text, ParsingOptions.getDefault().withUnhide(Unhide.UnhideOptions.ALL)));
    }

    @Test
    void testWordsAndNumbersAutoWhitespace() {
        var tree = PT.create(
                "sentence",
                PT.create("word", "ab"),
                PT.create("number", "123"),
                PT.create("word", "cd")
        );

        var p = words_and_numbers_auto_whitespace;
        Assertions.assertEquals(tree, p.parse("ab 123 cd"));
        Assertions.assertEquals(tree, p.parse(" ab 123 cd "));

        var p2 = words_and_numbers_auto_whitespace2;
        Assertions.assertEquals(tree, p2.parse("ab 123 cd"));
        Assertions.assertEquals(tree, p2.parse(" ab 123 cd "));
    }

    @Test
    void testWordsAndNumbersAutoWhitespaceAndComments() {
        var tree = PT.create(
                "sentence",
                PT.create("word", "abc"),
                PT.create("number", "123"),
                PT.create("word", "def")
        );

        final @NotNull Parser p = words_and_numbers_auto_whitespace_and_comments;

        Assertions.assertEquals(tree, p.parse(" abc 123  def "));
        Assertions.assertEquals(tree, p.parse(" abc 123 (* (*de*)f *) def"));
    }

    @Test
    void testEatA() {
        var tree = PT.create(
                "Aeater",
                "a", "a", "a", "a", "a", "a", "a", "a",
                new ParseFailureNode("bbbbbb", Sym.sym("failure"), 8, 14)
        );

        final @NotNull Parser p = eat_a;
        final @NotNull var text = "aaaaaaaabbbbbb";

        Assertions.assertEquals(tree, p.parse(text, ParsingOptions.getDefault().withEmbedFailureInParseTree(true)));
    }

    @Test
    void testIntOrDouble() {
        var tree = PT.create(
                "Input",
                PT.create("Int", "31"),
                PT.create("Double", "0.2")
        );

        final @NotNull Parser p = int_or_double;
        final @NotNull var text = "31 0.2";

        Assertions.assertEquals(tree, p.parse(text));
    }

    @Test
    void testGreedyRegex() {
        {
            final @NotNull Parser p = Alpha.parser("S = #'\\s*'");
            final @NotNull var text = "     ";
            var tree = PT.create("S", text);

            Assertions.assertEquals(tree, p.parse(text));
        }
        {
            final @NotNull Parser p = Alpha.parser("S = #'a+'");
            final @NotNull var text = "aaaaaa";
            var tree = PT.create("S", text);

            Assertions.assertEquals(tree, p.parse(text));
        }
    }

    @Test
    void testSimpleOrderedChoice() {
        final @NotNull Parser p = Alpha.parser("S = 'a' / ε");

        Assertions.assertEquals(PT.create("S"), p.parse(""));
        Assertions.assertEquals(PT.create("S", "a"), p.parse("a"));
    }

    @Test
    void testStarRepetitionFailures() {
        var p = Alpha.parser("S = 'a'*");

        Assertions.assertTrue(p.parse("AaaAaa").isFailure());
        Assertions.assertTrue(p.parse("").isSuccess());
    }

    @Test
    void testStarRepetitionFailuresTotal() {
        var p = Alpha.parser("S = 'a'*");
        var opts = ParsingOptions.getDefault().withEmbedFailureInParseTree(true);

        Assertions.assertEquals(
                PT.create("S",
                        new ParseFailureNode(
                                "AaaAaa",
                                Sym.sym("failure"),
                                0, 6)),
                p.parse("AaaAaa", opts)
        );

        Assertions.assertTrue(p.parse("", opts).isSuccess());
    }

    @Test
    void testPlusRepetitionFailures() {
        var p = Alpha.parser("S = 'a'+");

        Assertions.assertTrue(p.parse("AaaAaa").isFailure());
        Assertions.assertTrue(p.parse("").isFailure());
    }

    @Test
    void testPlusRepetitionFailuresTotal() {
        var p = Alpha.parser("S = 'a'+");
        var opts = ParsingOptions.getDefault().withEmbedFailureInParseTree(true);

        Assertions.assertEquals(
                PT.create("S",
                        new ParseFailureNode(
                                "AaaAaa",
                                Sym.sym("failure"),
                                0, 6)),
                p.parse("AaaAaa", opts)
        );

        Assertions.assertEquals(PT.create("S"), p.parse("", opts));
    }

    @Test
    void testStringVsStringCi() {
        var grammar = "S = 'a'+";
        var text = "AaaAaa";

        var pStandard1 = Alpha.parser(grammar);
        Assertions.assertTrue(pStandard1.parse(text).isFailure());

        var tree = PT.create("S", "a", "a", "a", "a", "a", "a");

        var pCaseInsensitive = Alpha.parser(grammar,
                ParserCreationOptions
                        .getDefault()
                        .withStringCaseInsensitive(true));
        Assertions.assertEquals(tree, pCaseInsensitive.parse(text));
    }

    @Test
    void testStringExplicitCiFalse() {
        var grammar = "S = 'a'+";
        var text = "AaaAaa";

        var pStandard1 = Alpha.parser(grammar);
        Assertions.assertTrue(pStandard1.parse(text).isFailure());

        var pStandard2 = Alpha.parser(grammar,
                ParserCreationOptions
                        .getDefault()
                        .withStringCaseInsensitive(GlobalCaseInsensitivity.FALSE));
        Assertions.assertTrue(pStandard2.parse(text).isFailure());

        var pStandard3 = Alpha.parser(grammar,
                ParserCreationOptions
                        .getDefault()
                        .withStringCaseInsensitive(false));
        Assertions.assertTrue(pStandard3.parse(text).isFailure());
    }

    @Test
    void testStringExplicitCiTrue() {
        var grammar = "S = 'a'+";
        var text = "AaaAaa";

        var tree = PT.create("S", "a", "a", "a", "a", "a", "a");

        var pCaseInsensitive = Alpha.parser(grammar,
                ParserCreationOptions
                        .getDefault()
                        .withStringCaseInsensitive(GlobalCaseInsensitivity.TRUE));
        Assertions.assertEquals(tree, pCaseInsensitive.parse(text));

        var pCaseInsensitive2 = Alpha.parser(grammar,
                ParserCreationOptions
                        .getDefault()
                        .withStringCaseInsensitive(true));
        Assertions.assertEquals(tree, pCaseInsensitive2.parse(text));
    }

    @Test
    void testAutoWhitespaceExamples() {
        var tree = PT.create("S", "foo", "123");
        var text = "foo 123";

        Assertions.assertEquals(tree, auto_whitespace_example.parse(text));
    }

    @Test
    void testAutoWhitespaceExample2() {
        var tree = PT.create("S", "foo", "123");
        var text = "foo 123";

        Assertions.assertEquals(tree, auto_whitespace_example2.parse(text));
    }

    @Test
    void testRegexFail() {
        Assertions.assertTrue(
                Alpha.parser("f = #'asdf'").parse("asdf").isSuccess());
        Assertions.assertTrue(
                Alpha.parser("f = #'asdf'").parse("").isFailure());
        Assertions.assertFalse(
                Alpha.parser("f = #'asdf'").parse("").isSuccess());
    }

    @Test
    void testCaseInsensitiveRegex() {
        Assertions.assertEquals(
                PT.create("S", "aaa"),
                case_insensitive_regexp.parse("aaa"));
        Assertions.assertEquals(
                PT.create("S", "AAA"),
                case_insensitive_regexp.parse("AAA"));
        Assertions.assertEquals(
                PT.create("S", "aAa"),
                case_insensitive_regexp.parse("aAa"));
    }
}