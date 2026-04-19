package alphaparse;

import alphaparse.parser.Parser;
import alphaparse.reduction.ReductionType;
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

//    final @NotNull Parser as_and_bs_enlive = Alpha.parser(
//            """
//                    S = AB*
//                    AB = A B
//                    A = 'a'+
//                    B = 'b'+""",
//            new Alpha.ParserCreationOptions(ReductionType.ReductionTypesAvailable.ENLIVE));

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

//    final @NotNull Parser addition = Alpha.parser(
//            """
//                    plus = plus <'+'> plus | num
//                    num = #'[0-9]'+
//                    """);

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

    final @NotNull Parser repeated_a = Alpha.parser(
            """
                    S = 'a'+
                    """);

    final @NotNull Parser lookahead_example = Alpha.parser(
            """
                    S = &'ab' ('a' | 'b')+
                    """);

    final @NotNull Parser negative_lookahead_example = Alpha.parser(
            """
                    S = !'ab' ('a' | 'b')+
                    """);

//    final @NotNull Parser abc = Alpha.parser(
//            """
//                    S = &(A 'c') 'a'+ B
//                    A = 'a' A? 'b'
//                    <B> = 'b' B? 'c'
//                    """);

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

//    final @NotNull Parser ord_test = Alpha.parser(
//            """
//                    S = Even / Odd
//                    Even = 'aa'*
//                    Odd = 'a'+
//                    """);

//    final @NotNull Parser ord2_test = Alpha.parser(
//            """
//                    S = token (<ws> token)*
//                    ws = #'\\s+'
//                    keyword = 'hello' | 'bye'
//                    identifier = #'\\S+'
//                    token = keyword / identifier
//                    """
//    );

//    final @NotNull Parser even_odd = Alpha.parser(
//            """
//                    S = Even | Odd
//                    eos = !#'.'
//                    Even = 'aa'*
//                    Odd = !(Even eos) 'a'+
//                    """);

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
            new Alpha.ParserCreationOptions(Keyword.intern("S"))
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
            new Alpha.ParserCreationOptions(whitespace));

    final @NotNull Parser words_and_numbers_auto_whitespace = Alpha.parser(
            """
                    sentence = token+
                    <token> = word | number
                    word = #'[a-zA-Z]+'
                    number = #'[0-9]+'
                    """,
            new Alpha.ParserCreationOptions(whitespace));

    final @NotNull Parser auto_whitespace_example2 = Alpha.parser(
            """
                    S = A B
                    <A> = 'foo'
                    <B> = #'\\d+'
                    """,
            new Alpha.ParserCreationOptions(
                    Alpha.getPredefinedWhitespaceParser(Keyword.intern("standard"))));

    final @NotNull Parser words_and_numbers_auto_whitespace2 = Alpha.parser(
            """
                    sentence = token+
                    <token> = word | number
                    word = #'[a-zA-Z]+'
                    number = #'[0-9]+'
                    """,
            new Alpha.ParserCreationOptions(
                    Alpha.getPredefinedWhitespaceParser(Keyword.intern("standard"))));

    final @NotNull Parser whitespace_or_comments = Alpha.parser(
            """
                    ws-or-comments = #'\\s+' | comments
                    comments = comment+
                    comment = '(*' inside-comment* '*)'
                    inside-comment =  !'*)' !'(*' #'.' | comment
                    """,
            new Alpha.ParserCreationOptions(whitespace));

    final @NotNull Parser words_and_numbers_auto_whitespace_and_comments = Alpha.parser(
            """
                    sentence = token+
                    <token> = word | number
                    word = #'[a-zA-Z]+'
                    number = #'[0-9]+'
                    """,
            new Alpha.ParserCreationOptions(whitespace_or_comments));

    final @NotNull Parser eat_a = Alpha.parser("Aeater = #'[a]'+",
            new Alpha.ParserCreationOptions(ReductionType.ReductionTypesAvailable.ENLIVE));

    final @NotNull Parser int_or_double = Alpha.parser(
            """
                    ws = #'\\s+';
                    Int = #'[0-9]+';
                    Double = #'[0-9]+\\.[0-9]*|\\.[0-9]+';
                    <ConstExpr> = Int | Double;
                    Input = ConstExpr <ws> ConstExpr;
                    """,
            new Alpha.ParserCreationOptions(Keyword.intern("Input")));

    final @NotNull Parser case_insensitive_regexp = Alpha.parser(
            """
                    S = #'(?i)a+'
                    """);

    @Test
    public void testAsAndBs() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree tree = ParseTree.create(
                "S",
                ParseTree.create("AB", ParseTree.create("A", "a", "a", "a", "a", "a"), ParseTree.create("B", "b", "b", "b")),
                ParseTree.create("AB", ParseTree.create("A", "a", "a", "a", "a"), ParseTree.create("B", "b", "b"))
        );

        Assertions.assertEquals(
                tree,
                Alpha.parse(as_and_bs, text)
        );

        Assertions.assertEquals(
                tree,
                as_and_bs.parse(text)
        );

        Alpha.ParsingOptions options = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                tree,
                as_and_bs.parse(text, options)
        );
    }

    @Test
    public void testAsAndBsRegex() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree tree = ParseTree.create(
                "S",
                ParseTree.create("AB", ParseTree.create("A", "a", "a", "a", "a", "a"), ParseTree.create("B", "b", "b", "b")),
                ParseTree.create("AB", ParseTree.create("A", "a", "a", "a", "a"), ParseTree.create("B", "b", "b"))
        );

        Assertions.assertEquals(
                tree,
                Alpha.parse(as_and_bs_regex, text)
        );

        Assertions.assertEquals(
                tree,
                as_and_bs_regex.parse(text)
        );

        Alpha.ParsingOptions options = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                tree,
                as_and_bs_regex.parse(text, options)
        );
    }

    @Test
    public void testAsAndBsHiccup() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree tree = ParseTree.create(
                "S",
                ParseTree.create("AB", ParseTree.create("A", "a", "a", "a", "a", "a"), ParseTree.create("B", "b", "b", "b")),
                ParseTree.create("AB", ParseTree.create("A", "a", "a", "a", "a"), ParseTree.create("B", "b", "b"))
        );
        var abTag = Keyword.intern("AB");
        var aTag = Keyword.intern("A");
        var bTag = Keyword.intern("B");
        final @NotNull List<Object> treeHiccup = List.of(
                Keyword.intern("S"),
                List.of(abTag, List.of(aTag, "a", "a", "a", "a", "a"), List.of(bTag, "b", "b", "b")),
                List.of(abTag, List.of(aTag, "a", "a", "a", "a"), List.of(bTag, "b", "b"))
        );

        Assertions.assertEquals(
                treeHiccup,
                tree.hiccup()
        );

        Assertions.assertEquals(
                treeHiccup,
                Alpha.parse(as_and_bs, text).castToParseSuccess().hiccup()
        );

        var options = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                treeHiccup,
                as_and_bs.parse(text, options).castToParseSuccess().hiccup()
        );
    }

    @Test
    public void testAsAndBsVariation1() {
        final @NotNull String text = "aaaaabbbaaaabb";

        var res = ParseTree.create("S",
                ParseTree.create("AB", "a", "a", "a", "a", "a", "b", "b", "b"),
                ParseTree.create("AB", "a", "a", "a", "a", "b", "b"));

        Assertions.assertEquals(
                res,
                as_and_bs_variation1.parse(text));

        var options = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                res,
                as_and_bs_variation1.parse(text, options));
    }

    @Test
    public void testAsAndBsVariation2() {
        final @NotNull String text = "aaaaabbbaaaabb";

        var res = ParseTree.create(
                "S",
                "a", "a", "a", "a", "a", "b", "b", "b", "a", "a", "a", "a", "b", "b");
        var resList = List.of(
                Keyword.intern("S"),
                "a", "a", "a", "a", "a", "b", "b", "b", "a", "a", "a", "a", "b", "b");

        Assertions.assertEquals(
                res,
                as_and_bs_variation2.parse(text));
        Assertions.assertEquals(
                resList,
                as_and_bs_variation2.parse(text).castToParseSuccess().hiccup());

        var memOpt = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                res,
                as_and_bs_variation2.parse(text, memOpt));
        Assertions.assertEquals(
                resList,
                as_and_bs_variation2.parse(text, memOpt).castToParseSuccess().hiccup());
    }

    @Test
    public void testParenAb() {
        var text = "(aba)";
        var tree = ParseTree.create("paren-wrapped",
                "(",
                ParseTree.create("seq-of-A-or-B", "a", "b", "a"),
                ")");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab, text), paren_ab.parse(text));
    }

    @Test
    public void testParenAbHideParens() {
        var text = "(aba)";
        var tree = ParseTree.create("paren-wrapped",
                ParseTree.create("seq-of-A-or-B", "a", "b", "a"));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_parens, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_parens, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab_hide_parens, text), paren_ab_hide_parens.parse(text));
    }

    @Test
    public void testParenAbManuallyFlattened() {
        var text = "(aba)";
        var tree = ParseTree.create("paren-wrapped", "a", "b", "a");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_manually_flattened, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_manually_flattened, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab_manually_flattened, text), paren_ab_manually_flattened.parse(text));
    }

    @Test
    public void testParenAbHideTag() {
        var text = "(aba)";
        var tree = ParseTree.create("paren-wrapped", "a", "b", "a");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_tag, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_tag, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab_hide_tag, text), paren_ab_hide_tag.parse(text));
    }

    @Test
    public void testParenAbHideBothTags() {
        var text = "(aba)";

        // Sadly, AlphaParse can not output untagged trees.
        // The expected result for Instaparse is as follows:
        //    (paren-ab-hide-both-tags "(aba)") ;=> ("a" "b" "a")
        var tree = ParseTree.create(ParseTree.NULL_TAG_NAME, "a", "b", "a");

        // That raw output can be achieved by manual conversion:
        Assertions.assertEquals(List.of("a", "b", "a"), tree.hiccup());

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_both_tags, text));
        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_both_tags, text, Alpha.ParsingOptions.optMemory()));
        Assertions.assertEquals(Alpha.parse(paren_ab_hide_both_tags, text), paren_ab_hide_both_tags.parse(text));
    }

    @Test
    public void testParserAsFunction() {
        var text = "aaaa";
        var grammar = "S = 'a' S / '' ";
        var tree =
                ParseTree.create("S", "a", ParseTree.create("S", "a", ParseTree.create("S", "a", ParseTree.create("S", "a", ParseTree.create("S")))));

        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, null));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testParserAsFunction2() {
        var text = "aaaa";
        var grammar = "S = S 'a' / Epsilon";
        var tree =
                ParseTree.create("S", ParseTree.create("S", ParseTree.create("S", ParseTree.create("S", ParseTree.create("S"), "a"), "a"), "a"), "a");

        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, null));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testAmbiguousParses() {
        var text = "aaaaaa";

        var treesAmbiguous = List.of(
                ParseTree.create("S", ParseTree.create("A", "a"), ParseTree.create("A", "a", "a", "a", "a", "a")),
                ParseTree.create("S", ParseTree.create("A", "a", "a", "a", "a", "a", "a"), ParseTree.create("A")),
                ParseTree.create("S", ParseTree.create("A", "a", "a"), ParseTree.create("A", "a", "a", "a", "a")),
                ParseTree.create("S", ParseTree.create("A", "a", "a", "a"), ParseTree.create("A", "a", "a", "a")),
                ParseTree.create("S", ParseTree.create("A", "a", "a", "a", "a"), ParseTree.create("A", "a", "a")),
                ParseTree.create("S", ParseTree.create("A", "a", "a", "a", "a", "a"), ParseTree.create("A", "a")),
                ParseTree.create("S", ParseTree.create("A"), ParseTree.create("A", "a", "a", "a", "a", "a", "a"))
        );

        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text));
        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text));
        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testUnambiguousParses() {
        var text = "aaaaaa";


        var treesUnambiguous = List.of(
                ParseTree.create("S", ParseTree.create("A", text), ParseTree.create("A", ""))
        );

        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text));
        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text));
        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testLookaheadExample() {
        var text = "abaaaab";
        var tree = ParseTree.create("S", "a", "b", "a", "a", "a", "a", "b");
        Assertions.assertEquals(tree, lookahead_example.parse(text));
        Assertions.assertEquals(tree, lookahead_example.parse(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(tree, lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testLookaheadExampleFailure() {
        var text = "bbaaaab";

        Assertions.assertFalse(lookahead_example.parse(text).isSuccess());
        Assertions.assertTrue(lookahead_example.parse(text).isFailure());

        Assertions.assertEquals(
                lookahead_example.parse(text),
                lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testNegativeLookaheadExample() {
        var text = "bbaaaab";
        var tree = ParseTree.create("S", "b", "b", "a", "a", "a", "a", "b");

        Assertions.assertEquals(tree, negative_lookahead_example.parse(text));

        Assertions.assertEquals(
                tree,
                negative_lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testNegativeLookaheadExampleFailure() {
        var text = "abaaaab";

        Assertions.assertFalse(negative_lookahead_example.parse(text).isSuccess());
        Assertions.assertTrue(negative_lookahead_example.parse(text).isFailure());

        Assertions.assertEquals(
                negative_lookahead_example.parse(text),
                negative_lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testCharRangeExampleFailure() {
        var p = Alpha.parser("""
                Regex = (CharNonRange | Range) +
                Range = Char <'-'> Char
                CharNonRange = Char ! ('-' Char)
        Char = #'[-x]' | 'c' (! 'd') 'x'
        """);

        var tree = ParseTree.create("Regex",
                ParseTree.create("Range",
                        ParseTree.create("Char", "x"),
                        ParseTree.create("Char", "c", "x")));

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
    public void testAmbiguousTokenizer() {
        var text = "defn my cond";
        var trees = Set.of(
                ParseTree.create("sentence", ParseTree.create("identifier", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("identifier", "cond")),
                ParseTree.create("sentence", ParseTree.create("keyword", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("identifier", "cond")),
                ParseTree.create("sentence", ParseTree.create("identifier", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("keyword", "cond")),
                ParseTree.create("sentence", ParseTree.create("keyword", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("keyword", "cond"))
        );

        Assertions.assertEquals(trees, new HashSet<>(Alpha.parses(ambiguous_tokenizer, text)));
    }

    @Test
    public void testUnambiguousTokenizer() {
        var text = "defn my cond";
        var trees = List.of(
                ParseTree.create("sentence", ParseTree.create("keyword", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("keyword", "cond"))
        );

        Assertions.assertEquals(trees, Alpha.parses(unambiguous_tokenizer, text));
    }

    @Test
    public void testPreferentialTokenizer() {
        var text = "defn my cond";
        var trees = Set.of(
                ParseTree.create("sentence", ParseTree.create("keyword", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("keyword", "cond")),

                ParseTree.create("sentence", ParseTree.create("identifier", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("keyword", "cond")),

                ParseTree.create("sentence", ParseTree.create("keyword", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("identifier", "cond")),

                ParseTree.create("sentence", ParseTree.create("identifier", "defn"), ParseTree.create("identifier", "my"), ParseTree.create("identifier", "cond"))
        );

        Assertions.assertEquals(trees, new HashSet<>(Alpha.parses(preferential_tokenizer, text)));
    }

    @Test
    public void testRepeatedA() {
        var text = "aaaaaa";
        var trees = List.of(
                ParseTree.create("S", "a", "a", "a", "a", "a", "a")
        );

        Assertions.assertEquals(trees, Alpha.parses(repeated_a, text));

        var partialOpts = Alpha.ParsingOptions.getDefault().withPartialSetTo(true);
        var treesPartial = List.of(
                ParseTree.create("S", "a"),
                ParseTree.create("S", "a", "a"),
                ParseTree.create("S", "a", "a", "a"),
                ParseTree.create("S", "a", "a", "a", "a"),
                ParseTree.create("S", "a", "a", "a", "a", "a"),
                ParseTree.create("S", "a", "a", "a", "a", "a", "a")
        );

        Assertions.assertEquals(treesPartial, Alpha.parses(repeated_a, text, partialOpts));
    }

    @Test
    public void testWordsAndNumbersOneCharacterAtATime() {
        var text = "abc 123 def";
        var tree = ParseTree.create("sentence",
                ParseTree.create("word", "a", "b", "c"),
                ParseTree.create("number", "1", "2", "3"),
                ParseTree.create("word", "d", "e", "f"));

        Assertions.assertEquals(tree, Alpha.parse(words_and_numbers_one_character_at_a_time, text));
        Assertions.assertEquals(tree, Alpha.parse(words_and_numbers_one_character_at_a_time, text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testArithmeticGrammar() {
        var text = "1-2/(3-4)+5*6";
        var tree = ParseTree.create("expr",
                ParseTree.create("add",
                        ParseTree.create("sub",
                                ParseTree.create("number", "1"),
                                ParseTree.create("div",
                                        ParseTree.create("number", "2"),
                                        ParseTree.create("sub",
                                                ParseTree.create("number", "3"),
                                                ParseTree.create("number", "4")))),
                        ParseTree.create("mul",
                                ParseTree.create("number", "5"),
                                ParseTree.create("number", "6")))
        );

        Assertions.assertEquals(tree, Alpha.parse(arithmetic, text));
        Assertions.assertEquals(tree, Alpha.parse(arithmetic, text, Alpha.ParsingOptions.optMemory()));
    }

    /*
             (tricky-ebnf-build "===")
             [:S "=" "=" "="]

             (tricky-ebnf-build "b=")
             [:S [:B "b" "="]]
     */

    @Test
    public void testTrickyEbnfBuild() {
        var text1 = "===";
        var text2 = "b=";
        var tree1 = ParseTree.create("S", "=", "=", "=");
        var tree2 = ParseTree.create("S", ParseTree.create("B", "b", "="));

        Assertions.assertEquals(tree1, Alpha.parse(tricky_ebnf_build, text1));
        Assertions.assertEquals(tree2, Alpha.parse(tricky_ebnf_build, text2));
    }

    @Test
    public void testFail() {
        Assertions.assertEquals(
                ParseTree.create("S", "a")
                , Alpha.parser("S = A\n<A> = 'a'").parse("a"));
        Assertions.assertEquals(
                ParseTree.create("S", "a")
                , Alpha.parser("S = A\n<A> = 'a'").parse("a", Alpha.ParsingOptions.optMemory()));
        Assertions.assertEquals(
                ParseTree.create("S", ParseTree.create("A")),
                Alpha.parser("S = A\nA = <'a'>").parse("a"));
        Assertions.assertEquals(
                ParseTree.create("S", ParseTree.create("A")),
                Alpha.parser("S = A\nA = <'a'>").parse("a", Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testOptionalRepeat() {
        var parser = Alpha.parser("S = ('a'?)+");
        var tree = ParseTree.create("S");

        Assertions.assertEquals(tree, parser.parse(""));
        Assertions.assertEquals(tree, parser.parse("", Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testDot() {
        var parser = Alpha.parser("""
                a = b c .
                b = 'b' .
                c = 'c' .
                """);
        var tree = ParseTree.create("a",
                ParseTree.create("b", "b"),
                ParseTree.create("c", "c"));

        Assertions.assertEquals(tree, parser.parse("bc"));
        Assertions.assertEquals(tree, parser.parse("bc", Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testUnhide1() {
        var text = "(ababa)";
        var treeNormal = ParseTree.create("paren-wrapped",
                ParseTree.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"));
        var treeWParen = ParseTree.create("paren-wrapped",
                "(",
                ParseTree.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"),
                ")");
        var p = paren_ab_hide_parens;

        Assertions.assertEquals(treeNormal, p.parse(text));
        Assertions.assertEquals(treeWParen,
                p.parse(text, Alpha.ParsingOptions.getDefault().withUnhideOptionsSetTo(Alpha.UnhideOptions.content)));
        Assertions.assertEquals(treeWParen,
                p.parse(text, Alpha.ParsingOptions.getDefault().withUnhideOptionsSetTo(Alpha.UnhideOptions.all)));
        Assertions.assertEquals(treeNormal,
                p.parse(text, Alpha.ParsingOptions.getDefault().withUnhideOptionsSetTo(Alpha.UnhideOptions.tags)));
    }

    @Test
    public void testUnhide2() {
        var text = "(ababa)";
        var treeWTag = ParseTree.create("paren-wrapped",
                ParseTree.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"));
        var treeWAll = ParseTree.create("paren-wrapped",
                "(",
                ParseTree.create("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"),
                ")");
        var p = paren_ab_hide_tag;

        Assertions.assertEquals(treeWTag,
                p.parse(text, Alpha.ParsingOptions.getDefault().withUnhideOptionsSetTo(Alpha.UnhideOptions.tags)));

        Assertions.assertEquals(treeWAll,
                p.parse(text, Alpha.ParsingOptions.getDefault().withUnhideOptionsSetTo(Alpha.UnhideOptions.all)));
    }

    @Test
    public void testEps() {
        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = eps").parse(""));
        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = epsilon").parse(""));
        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = Epsilon").parse(""));
        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = EPSILON").parse(""));
        Assertions.assertEquals(
                ParseTree.create("S"),
                Alpha.parser("S = ε").parse(""));
    }

    @Test
    public void testEpsFail() {
        Assertions.assertTrue(
                Alpha.parser("S = eps")
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = epsilon")
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = Epsilon")
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = EPSILON")
                        .parse("a")
                        .isFailure());
        Assertions.assertTrue(
                Alpha.parser("S = ε")
                        .parse("a")
                        .isFailure());
    }

    @Test
    public void testWordsAndNumbers() {
        var text = "ab 123 cd";
        var treeWithoutTokenTag = ParseTree.create(
                "sentence",
                ParseTree.create("word", "ab"),
                ParseTree.create("number", "123"),
                ParseTree.create("word", "cd")
        );
        var treeFull = ParseTree.create(
                "sentence",
                ParseTree.create("token", ParseTree.create("word", "ab")),
                ParseTree.create("whitespace", " "),
                ParseTree.create("token", ParseTree.create("number", "123")),
                ParseTree.create("whitespace", " "),
                ParseTree.create("token", ParseTree.create("word", "cd"))
        );
        Assertions.assertEquals(treeWithoutTokenTag, words_and_numbers.parse(text));

        Assertions.assertEquals(treeFull, words_and_numbers.parse(text, Alpha.ParsingOptions.getDefault().withUnhideOptionsSetTo(Alpha.UnhideOptions.all)));
    }

    @Test
    public void testWordsAndNumbersAutoWhitespace() {
        var tree = ParseTree.create(
                "sentence",
                ParseTree.create("word", "ab"),
                ParseTree.create("number", "123"),
                ParseTree.create("word", "cd")
        );

        var p = words_and_numbers_auto_whitespace;
        Assertions.assertEquals(tree, p.parse("ab 123 cd"));
        Assertions.assertEquals(tree, p.parse(" ab 123 cd "));

        var p2 = words_and_numbers_auto_whitespace2;
        Assertions.assertEquals(tree, p2.parse("ab 123 cd"));
        Assertions.assertEquals(tree, p2.parse(" ab 123 cd "));
    }

    @Test
    public void testWordsAndNumbersAutoWhitespaceAndComments() {
        var tree = ParseTree.create(
                "sentence",
                ParseTree.create("word", "abc"),
                ParseTree.create("number", "123"),
                ParseTree.create("word", "def")
        );

        final @NotNull Parser p = words_and_numbers_auto_whitespace_and_comments;

        Assertions.assertEquals(tree, p.parse(" abc 123  def "));
        Assertions.assertEquals(tree, p.parse(" abc 123 (* (*de*)f *) def"));
    }

    @Test
    public void testEatA() {
        var tree = ParseTree.create(
                "Aeater",
                "a", "a", "a", "a", "a", "a", "a", "a",
                new ParseFailureNode("bbbbbb", Keyword.intern("failure"), 8, 14)
        );

        final @NotNull Parser p = eat_a;
        final @NotNull var text = "aaaaaaaabbbbbb";

        Assertions.assertEquals(tree, p.parse(text, Alpha.ParsingOptions.getDefault().withTotalParseSetTo(true)));
    }

    @Test
    public void testIntOrDouble() {
        var tree = ParseTree.create(
                "Input",
                ParseTree.create("Int", "31"),
                ParseTree.create("Double", "0.2")
        );

        final @NotNull Parser p = int_or_double;
        final @NotNull var text = "31 0.2";

        Assertions.assertEquals(tree, p.parse(text));
    }

    @Test
    public void testGreedyRegex() {
        {
            final @NotNull Parser p = Alpha.parser("S = #'\\s*'");
            final @NotNull var text = "     ";
            var tree = ParseTree.create("S", text);

            Assertions.assertEquals(tree, p.parse(text));
            Assertions.assertEquals(tree, p.parse(text, Alpha.ParsingOptions.optMemory()));
        }
        {
            final @NotNull Parser p = Alpha.parser("S = #'a+'");
            final @NotNull var text = "aaaaaa";
            var tree = ParseTree.create("S", text);

            Assertions.assertEquals(tree, p.parse(text));
            Assertions.assertEquals(tree, p.parse(text, Alpha.ParsingOptions.optMemory()));
        }
    }

    @Test
    public void testSimpleOrderedChoice() {
        final @NotNull Parser p = Alpha.parser("S = 'a' / eps");

        Assertions.assertEquals(ParseTree.create("S"), p.parse(""));
        Assertions.assertEquals(ParseTree.create("S", "a"), p.parse("a"));
    }

    @Test
    public void testStarRepetitionFailures() {
        var p = Alpha.parser("S = 'a'*");

        Assertions.assertTrue(p.parse("AaaAaa").isFailure());
        Assertions.assertTrue(p.parse("").isSuccess());
    }

    @Test
    public void testStarRepetitionFailuresTotal() {
        var p = Alpha.parser("S = 'a'*");
        var opts = Alpha.ParsingOptions.getDefault().withTotalParseSetTo(true);

        Assertions.assertEquals(
                ParseTree.create("S",
                        new ParseFailureNode(
                                "AaaAaa",
                                Keyword.intern("failure"),
                                0, 6)),
                p.parse("AaaAaa", opts)
        );

        Assertions.assertTrue(p.parse("", opts).isSuccess());
    }

    @Test
    public void testPlusRepetitionFailures() {
        var p = Alpha.parser("S = 'a'+");

        Assertions.assertTrue(p.parse("AaaAaa").isFailure());
        Assertions.assertTrue(p.parse("").isFailure());
    }

    @Test
    public void testPlusRepetitionFailuresTotal() {
        var p = Alpha.parser("S = 'a'+");
        var opts = Alpha.ParsingOptions.getDefault().withTotalParseSetTo(true);

        Assertions.assertEquals(
                ParseTree.create("S",
                        new ParseFailureNode(
                                "AaaAaa",
                                Keyword.intern("failure"),
                                0, 6)),
                p.parse("AaaAaa", opts)
        );

        Assertions.assertEquals(ParseTree.create("S"), p.parse("", opts));
    }

    @Test
    public void testStringVsStringCi() {
        var grammar = "S = 'a'+";
        var text = "AaaAaa";

        var pStandard1 = Alpha.parser(grammar);
        Assertions.assertTrue(pStandard1.parse(text).isFailure());

        var tree = ParseTree.create("S", "a", "a", "a", "a", "a", "a");

        var pCaseInsensitive = Alpha.parser(grammar,
                Alpha.ParserCreationOptions
                        .getDefault()
                        .withCaseInsensitivity(true));
        Assertions.assertEquals(tree, pCaseInsensitive.parse(text));
    }

    @Test
    public void testStringExplicitCiFalse() {
        var grammar = "S = 'a'+";
        var text = "AaaAaa";

        var pStandard1 = Alpha.parser(grammar);
        Assertions.assertTrue(pStandard1.parse(text).isFailure());

        var pStandard2 = Alpha.parser(grammar,
                Alpha.ParserCreationOptions
                        .getDefault()
                        .withCaseInsensitivity(GlobalCaseInsensitivity.FALSE));
        Assertions.assertTrue(pStandard2.parse(text).isFailure());

        var pStandard3 = Alpha.parser(grammar,
                Alpha.ParserCreationOptions
                        .getDefault()
                        .withCaseInsensitivity(false));
        Assertions.assertTrue(pStandard3.parse(text).isFailure());
    }

    @Test
    public void testStringExplicitCiTrue() {
        var grammar = "S = 'a'+";
        var text = "AaaAaa";

        var tree = ParseTree.create("S", "a", "a", "a", "a", "a", "a");

        var pCaseInsensitive = Alpha.parser(grammar,
                Alpha.ParserCreationOptions
                        .getDefault()
                        .withCaseInsensitivity(GlobalCaseInsensitivity.TRUE));
        Assertions.assertEquals(tree, pCaseInsensitive.parse(text));

        var pCaseInsensitive2 = Alpha.parser(grammar,
                Alpha.ParserCreationOptions
                        .getDefault()
                        .withCaseInsensitivity(true));
        Assertions.assertEquals(tree, pCaseInsensitive2.parse(text));
    }

    @Test
    public void testAutoWhitespaceExamples() {
        var tree = ParseTree.create("S", "foo", "123");
        var text = "foo 123";

        Assertions.assertEquals(tree, auto_whitespace_example.parse(text));
        Assertions.assertEquals(tree, auto_whitespace_example2.parse(text));
    }

    @Test
    public void testRegexFail() {
        Assertions.assertTrue(
                Alpha.parser("f = #'asdf'").parse("asdf").isSuccess());
        Assertions.assertTrue(
                Alpha.parser("f = #'asdf'").parse("").isFailure());
        Assertions.assertFalse(
                Alpha.parser("f = #'asdf'").parse("").isSuccess());
    }

    @Test
    public void testCaseInsensitiveRegex() {
        Assertions.assertEquals(
                ParseTree.create("S", "aaa"),
                case_insensitive_regexp.parse("aaa"));
        Assertions.assertEquals(
                ParseTree.create("S", "AAA"),
                case_insensitive_regexp.parse("AAA"));
        Assertions.assertEquals(
                ParseTree.create("S", "aAa"),
                case_insensitive_regexp.parse("aAa"));
    }
}