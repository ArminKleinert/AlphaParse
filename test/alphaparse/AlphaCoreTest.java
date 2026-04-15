package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.parser.Parser;
import alphaparse.parser.combinator.AlternationCombinator;
import alphaparse.parser.combinator.NonTerminal;
import alphaparse.reduction.ReductionType;
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

    final @NotNull Parser as_and_bs_enlive = Alpha.parser(
            """
                    S = AB*
                    AB = A B
                    A = 'a'+
                    B = 'b'+""",
            new Alpha.ParserCreationOptions(ReductionType.ReductionTypesAvailable.ENLIVE));

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

    final @NotNull Parser addition = Alpha.parser(
            """
                    plus = plus <'+'> plus | num
                    num = #'[0-9]'+
                    """);

    final @NotNull Parser addition_e = Alpha.parser(
            """
                    plus = plus <'+'> plus | num
                    num = '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'
                    """,
            new Alpha.ParserCreationOptions(ReductionType.ReductionTypesAvailable.ENLIVE));

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

    final @NotNull Parser words_and_numbers_enlive = Alpha.parser(
            """
                    sentence = token (<whitespace> token)*
                    <token> = word | number
                    whitespace = #'\\s+'
                    word = letter+
                    number = digit+
                    <letter> = #'[a-zA-Z]'
                    <digit> = #'[0-9]'
                    """,
            new Alpha.ParserCreationOptions(ReductionType.ReductionTypesAvailable.ENLIVE));

    final @NotNull Parser words_and_numbers_enlive_defparser = Alpha.parser(
            """
                    sentence = token (<whitespace> token)*
                    <token> = word | number
                    whitespace = #'\\s+'
                    word = letter+
                    number = digit+
                    <letter> = #'[a-zA-Z]'
                    <digit> = #'[0-9]'
                    """,
            new Alpha.ParserCreationOptions(ReductionType.ReductionTypesAvailable.ENLIVE));

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

    final @NotNull Parser abc = Alpha.parser(
            """
                    S = &(A 'c') 'a'+ B
                    A = 'a' A? 'b'
                    <B> = 'b' B? 'c'
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

    final @NotNull Parser ord_test = Alpha.parser(
            """
                    S = Even / Odd
                    Even = 'aa'*
                    Odd = 'a'+
                    """);

    final @NotNull Parser ord2_test = Alpha.parser(
            """
                    S = token (<ws> token)*
                    ws = #'\\s+'
                    keyword = 'hello' | 'bye'
                    identifier = #'\\S+'
                    token = keyword / identifier
                    """
    );

    final @NotNull Parser even_odd = Alpha.parser(
            """
                    S = Even | Odd
                    eos = !#'.'
                    Even = 'aa'*
                    Odd = !(Even eos) 'a'+
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

//    final @NotNull Parser  combo_build_example  = Alpha.parser(
//            (merge
//    {:S (alternation_combinator (make-non_terminal :A) (make_non_terminal :B);}
//      (ebnf "A = 'a'*")
//    {:B (ebnf "'b'+")})
//            :start :S);
//
//    final @NotNull Parser  tricky_ebnf_build = Alpha.parser(
//          (merge
//    {:S (alternation_combinator (make_non_terminal :A) (make_non_terminal :B);}
//      (ebnf "<A> = '='*")
//    {:B (ebnf "'b' '='")})
//            :start :S);
//final @NotNull Parser  tricky_ebnf_build = Alpha.parser(Grammar.fromProductions(
//        Grammar.entry(Keyword.intern("S"), new AlternationCombinator(List.of(new NonTerminal(Keyword.intern("A")), new NonTerminal(Keyword.intern("B"))))),
//        Grammar.entry(Keyword.intern())
//));
final @NotNull Parser  tricky_ebnf_build = Alpha.parser("""
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

    final @NotNull Parser whitespace_or_comments_v1 = Alpha.parser(
            """
                    ws-or-comment = #'\\s+' | comment
                    comment = '(*' inside-comment* '*)'
                    inside-comment =  ( !('*)' | '(*') #'.' ) | comment
                    """);

    final @NotNull Parser whitespace_or_comments_v2 = Alpha.parser(
            """
                    ws-or-comments = #'\\s+' | comments
                    comments = comment+
                    comment = '(*' inside-comment* '*)'
                    inside-comment =  !( '*)' | '(*' ) #'.' | comment
                    """);

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
                    Int = #'[0_9]+';
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
    public void as_and_bs() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree as_and_bs_aaaaabbbaaaabb_tree = new ParseTree(
                "S",
                new ParseTree("AB", new ParseTree("A", "a", "a", "a", "a", "a"), new ParseTree("B", "b", "b", "b")),
                new ParseTree("AB", new ParseTree("A", "a", "a", "a", "a"), new ParseTree("B", "b", "b"))
        );

        Assertions.assertEquals(
                as_and_bs_aaaaabbbaaaabb_tree,
                Alpha.parse(as_and_bs, text)
        );

        Assertions.assertEquals(
                Alpha.parse(as_and_bs, text),
                as_and_bs.parse(text)
        );

        Alpha.ParsingOptions options = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                as_and_bs.parse(text),
                as_and_bs.parse(text, options)
        );
    }

    @Test
    public void as_and_bs_hiccup() {
        final @NotNull String text = "aaaaabbbaaaabb";
        final @NotNull ParseTree as_and_bs_aaaaabbbaaaabb_tree = new ParseTree(
                "S",
                new ParseTree("AB", new ParseTree("A", "a", "a", "a", "a", "a"), new ParseTree("B", "b", "b", "b")),
                new ParseTree("AB", new ParseTree("A", "a", "a", "a", "a"), new ParseTree("B", "b", "b"))
        );
        var abTag = Keyword.intern("AB");
        var aTag = Keyword.intern("A");
        var bTag = Keyword.intern("B");
        final @NotNull List<Object> tree = List.of(
                Keyword.intern("S"),
                List.of(abTag, List.of(aTag, "a", "a", "a", "a", "a"), List.of(bTag, "b", "b", "b")),
                List.of(abTag, List.of(aTag, "a", "a", "a", "a"), List.of(bTag, "b", "b"))
        );

        Assertions.assertEquals(
                tree,
                as_and_bs_aaaaabbbaaaabb_tree.hiccup()
        );

        Assertions.assertEquals(
                tree,
                Alpha.parse(as_and_bs, text).castToParseSuccess().hiccup()
        );

        var options = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                tree,
                as_and_bs.parse(text, options).castToParseSuccess().hiccup()
        );
    }

    @Test
    public void as_and_bs_enlive_test() {
        // TODO
    }

    @Test
    public void as_and_bs_variation1() {
        final @NotNull String text = "aaaaabbbaaaabb";

        var res = new ParseTree("S",
                new ParseTree("AB", "a", "a", "a", "a", "a", "b", "b", "b"),
                new ParseTree("AB", "a", "a", "a", "a", "b", "b"));

        Assertions.assertEquals(
                res,
                as_and_bs_variation1.parse(text));

        var options = Alpha.ParsingOptions.optMemory();
        Assertions.assertEquals(
                res,
                as_and_bs_variation1.parse(text, options));
    }

    @Test
    public void as_and_bs_variation2() {
        final @NotNull String text = "aaaaabbbaaaabb";

        var res = new ParseTree(
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
    public void paren_ab_test() {
        var text = "(aba)";
        var tree = new ParseTree("paren-wrapped",
                "(",
                new ParseTree("seq-of-A-or-B", "a", "b", "a"),
                ")");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab, text), paren_ab.parse(text));
    }

    @Test
    public void paren_ab_hide_parens_test() {
        var text = "(aba)";
        var tree = new ParseTree("paren-wrapped",
                new ParseTree("seq-of-A-or-B", "a", "b", "a"));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_parens, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_parens, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab_hide_parens, text), paren_ab_hide_parens.parse(text));
    }

    @Test
    public void paren_ab_manually_flattened_test() {
        var text = "(aba)";
        var tree = new ParseTree("paren-wrapped", "a", "b", "a");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_manually_flattened, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_manually_flattened, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab_manually_flattened, text), paren_ab_manually_flattened.parse(text));
    }

    @Test
    public void paren_ab_hide_tag_test() {
        var text = "(aba)";
        var tree = new ParseTree("paren-wrapped", "a", "b", "a");

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_tag, text));

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_tag, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(Alpha.parse(paren_ab_hide_tag, text), paren_ab_hide_tag.parse(text));
    }

    @Test
    public void paren_ab_hide_both_tags_test() {
        var text = "(aba)";

        // Sadly, AlphaParse can not output untagged trees.
        // The expected result for Instaparse is as follows:
        //    (paren-ab-hide-both-tags "(aba)") ;=> ("a" "b" "a")
        var tree = new ParseTree(ParseTree.NULL_TAG_NAME, "a", "b", "a");

        // That raw output can be achieved by manual conversion:
        Assertions.assertEquals(List.of("a", "b", "a"), tree.hiccup());

        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_both_tags, text));
        Assertions.assertEquals(tree, Alpha.parse(paren_ab_hide_both_tags, text, Alpha.ParsingOptions.optMemory()));
        Assertions.assertEquals(Alpha.parse(paren_ab_hide_both_tags, text), paren_ab_hide_both_tags.parse(text));
    }

    @Test
    public void parser_as_function() {
        var text = "aaaa";
        var grammar = "S = 'a' S / '' ";
        var tree =
                new ParseTree("S", "a", new ParseTree("S", "a", new ParseTree("S", "a", new ParseTree("S", "a", new ParseTree("S")))));

        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, null));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void parser_as_function_2() {
        var text = "aaaa";
        var grammar = "S = S 'a' / Epsilon";
        var tree =
                new ParseTree("S", new ParseTree("S", new ParseTree("S", new ParseTree("S", new ParseTree("S"), "a"), "a"), "a"), "a");

        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, null));
        Assertions.assertEquals(tree, Alpha.parser(grammar).apply(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void ambiguous_parses() {
        var text = "aaaaaa";

        var treesAmbiguous = List.of(
                new ParseTree("S", new ParseTree("A", "a"), new ParseTree("A", "a", "a", "a", "a", "a")),
                new ParseTree("S", new ParseTree("A", "a", "a", "a", "a", "a", "a"), new ParseTree("A")),
                new ParseTree("S", new ParseTree("A", "a", "a"), new ParseTree("A", "a", "a", "a", "a")),
                new ParseTree("S", new ParseTree("A", "a", "a", "a"), new ParseTree("A", "a", "a", "a")),
                new ParseTree("S", new ParseTree("A", "a", "a", "a", "a"), new ParseTree("A", "a", "a")),
                new ParseTree("S", new ParseTree("A", "a", "a", "a", "a", "a"), new ParseTree("A", "a")),
                new ParseTree("S", new ParseTree("A"), new ParseTree("A", "a", "a", "a", "a", "a", "a"))
        );

        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text));
        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesAmbiguous, Alpha.parses(ambiguous, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text));
        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesAmbiguous, ambiguous.parses(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void unambiguous_parses() {
        var text = "aaaaaa";


        var treesUnambiguous = List.of(
                new ParseTree("S", new ParseTree("A", text), new ParseTree("A", ""))
        );

        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text));
        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesUnambiguous, Alpha.parses(not_ambiguous, text, Alpha.ParsingOptions.optMemory()));

        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text));
        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(treesUnambiguous, not_ambiguous.parses(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void lookahead_example() {
        var text = "abaaaab";
        var tree = new ParseTree("S", "a", "b", "a", "a", "a", "a", "b");
        Assertions.assertEquals(tree, lookahead_example.parse(text));
        Assertions.assertEquals(tree, lookahead_example.parse(text, Alpha.ParsingOptions.getDefault()));
        Assertions.assertEquals(tree, lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void lookahead_example_failure() {
        var text = "bbaaaab";

        Assertions.assertFalse(lookahead_example.parse(text).isSuccess());
        Assertions.assertTrue(lookahead_example.parse(text).isFailure());

        Assertions.assertEquals(
                lookahead_example.parse(text),
                lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void negative_lookahead_example() {
        var text = "bbaaaab";
        var tree = new ParseTree("S", "b", "b", "a", "a", "a", "a", "b");

        Assertions.assertEquals(tree, negative_lookahead_example.parse(text));

        Assertions.assertEquals(
                tree,
                negative_lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void negative_lookahead_example_failure() {
        var text = "abaaaab";

        Assertions.assertFalse(negative_lookahead_example.parse(text).isSuccess());
        Assertions.assertTrue(negative_lookahead_example.parse(text).isFailure());

        Assertions.assertEquals(
                negative_lookahead_example.parse(text),
                negative_lookahead_example.parse(text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void char_range_example_failure() {
        // TODO

        /*
        (insta/parses
               (insta/parser
                 "Regex = (CharNonRange | Range) +
                  Range = Char <'-'> Char
                  CharNonRange = Char ! ('-' Char)
                  Char = #'[-x]' | 'c' (! 'd') 'x'")
               "x-cx")
         '([:Regex [:Range [:Char "x"] [:Char "c" "x"]]])
         */
    }

    @Test
    public void ambiguous_tokenizer_test() {
        var text = "defn my cond";
        var trees = Set.of(
                new ParseTree("sentence", new ParseTree("identifier", "defn"), new ParseTree("identifier", "my"), new ParseTree("identifier", "cond")),
                new ParseTree("sentence", new ParseTree("keyword", "defn"), new ParseTree("identifier", "my"), new ParseTree("identifier", "cond")),
                new ParseTree("sentence", new ParseTree("identifier", "defn"), new ParseTree("identifier", "my"), new ParseTree("keyword", "cond")),
                new ParseTree("sentence", new ParseTree("keyword", "defn"), new ParseTree("identifier", "my"), new ParseTree("keyword", "cond"))
        );

        Assertions.assertEquals(trees, new HashSet<>(Alpha.parses(ambiguous_tokenizer, text)));
    }

    @Test
    public void unambiguous_tokenizer_test() {
        var text = "defn my cond";
        var trees = List.of(
                new ParseTree("sentence", new ParseTree("keyword", "defn"), new ParseTree("identifier", "my"), new ParseTree("keyword", "cond"))
        );

        Assertions.assertEquals(trees, Alpha.parses(unambiguous_tokenizer, text));
    }

    @Test
    public void preferential_tokenizer_test() {
        var text = "defn my cond";
        var trees = Set.of(
                new ParseTree("sentence", new ParseTree("keyword", "defn"), new ParseTree("identifier", "my"), new ParseTree("keyword", "cond")),

                new ParseTree("sentence", new ParseTree("identifier", "defn"), new ParseTree("identifier", "my"), new ParseTree("keyword", "cond")),

                new ParseTree("sentence", new ParseTree("keyword", "defn"), new ParseTree("identifier", "my"), new ParseTree("identifier", "cond")),

                new ParseTree("sentence", new ParseTree("identifier", "defn"), new ParseTree("identifier", "my"), new ParseTree("identifier", "cond"))
        );

        Assertions.assertEquals(trees, new HashSet<>(Alpha.parses(preferential_tokenizer, text)));
    }

    @Test
    public void repeated_a_test() {
        var text = "aaaaaa";
        var trees = List.of(
                new ParseTree("S", "a", "a", "a", "a", "a", "a")
        );

        Assertions.assertEquals(trees, Alpha.parses(repeated_a, text));

        var partialOpts = Alpha.ParsingOptions.getDefault().withPartialSetTo(true);
        var treesPartial = List.of(
                new ParseTree("S", "a"),
                new ParseTree("S", "a", "a"),
                new ParseTree("S", "a", "a", "a"),
                new ParseTree("S", "a", "a", "a", "a"),
                new ParseTree("S", "a", "a", "a", "a", "a"),
                new ParseTree("S", "a", "a", "a", "a", "a", "a")
        );

        Assertions.assertEquals(treesPartial, Alpha.parses(repeated_a, text, partialOpts));
    }

    @Test
    public void words_and_numbers_one_character_at_a_time_test() {
        var text = "abc 123 def";
        var tree = new ParseTree("sentence",
                new ParseTree("word", "a", "b", "c"),
                new ParseTree("number", "1", "2", "3"),
                new ParseTree("word", "d", "e", "f"));

        Assertions.assertEquals(tree, Alpha.parse(words_and_numbers_one_character_at_a_time, text));
        Assertions.assertEquals(tree, Alpha.parse(words_and_numbers_one_character_at_a_time, text, Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void arithmetic_grammar_test() {
        var text = "1-2/(3-4)+5*6";
        var tree = new ParseTree("expr",
                new ParseTree("add",
                        new ParseTree("sub",
                                new ParseTree("number", "1"),
                                new ParseTree("div",
                                        new ParseTree("number", "2"),
                                        new ParseTree("sub",
                                                new ParseTree("number", "3"),
                                                new ParseTree("number", "4")))),
                        new ParseTree("mul",
                                new ParseTree("number", "5"),
                                new ParseTree("number", "6")))
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
    public void tricky_ebnf_build_test() {
        var text1 = "===";
        var text2 = "b=";
        var tree1 = new ParseTree("S", "=", "=", "="        );
        var tree2 = new ParseTree("S", new ParseTree("B", "b","="));

        Assertions.assertEquals(tree1, Alpha.parse(tricky_ebnf_build, text1));
        Assertions.assertEquals(tree2, Alpha.parse(tricky_ebnf_build, text2));
    }

    @Test
    public void testFail() {
        Assertions.assertEquals(
                new ParseTree("S", "a")
                , Alpha.parser("S = A\n<A> = 'a'").parse("a"));
        Assertions.assertEquals(
                new ParseTree("S", "a")
                , Alpha.parser("S = A\n<A> = 'a'").parse("a", Alpha.ParsingOptions.optMemory()));
        Assertions.assertEquals(
                new ParseTree("S", new ParseTree("A")),
                Alpha.parser("S = A\nA = <'a'>").parse("a"));
        Assertions.assertEquals(
                new ParseTree("S", new ParseTree("A")),
                Alpha.parser("S = A\nA = <'a'>").parse("a", Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testOptionalRepeat() {
        var parser = Alpha.parser("S = ('a'?)+");
        var tree = new ParseTree("S");

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
        var tree = new ParseTree("a",
                new ParseTree("b", "b"),
                new ParseTree("c", "c"));

        Assertions.assertEquals(tree, parser.parse("bc"));
        Assertions.assertEquals(tree, parser.parse("bc", Alpha.ParsingOptions.optMemory()));
    }

    @Test
    public void testUnhide1() {
        var text = "(ababa)";
        var treeNormal = new ParseTree("paren-wrapped",
                new ParseTree("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"));
        var treeWParen = new ParseTree("paren-wrapped",
                "(",
                new ParseTree("seq-of-A-or-B",
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
        var treeWTag = new ParseTree("paren-wrapped",
                new ParseTree("seq-of-A-or-B",
                        "a", "b", "a", "b", "a"));
        var treeWAll = new ParseTree("paren-wrapped",
                "(",
                new ParseTree("seq-of-A-or-B",
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
                new ParseTree("S"),
                Alpha.parser("S = eps").parse(""));
        Assertions.assertEquals(
                new ParseTree("S"),
                Alpha.parser("S = epsilon").parse(""));
        Assertions.assertEquals(
                new ParseTree("S"),
                Alpha.parser("S = Epsilon").parse(""));
        Assertions.assertEquals(
                new ParseTree("S"),
                Alpha.parser("S = EPSILON").parse(""));
        Assertions.assertEquals(
                new ParseTree("S"),
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
        var treeWithoutTokenTag = new ParseTree(
                "sentence",
                new ParseTree("word","ab"),
                new ParseTree("number","123"),
                new ParseTree("word","cd")
        );
        var treeFull = new ParseTree(
                "sentence",
                new ParseTree("token", new ParseTree("word","ab")),
                new ParseTree("whitespace"," "),
                new ParseTree("token", new ParseTree("number","123")),
                new ParseTree("whitespace"," "),
                new ParseTree("token", new ParseTree("word","cd"))
        );
        Assertions.assertEquals(treeWithoutTokenTag, words_and_numbers.parse(text));

        Assertions.assertEquals(treeFull, words_and_numbers.parse(text, Alpha.ParsingOptions.getDefault().withUnhideOptionsSetTo(Alpha.UnhideOptions.all)));
    }

    @Test
    public void testWordsAndNumbersAutoWhitespace() {
        var tree = new ParseTree(
                "sentence",
                new ParseTree("word","ab"),
                new ParseTree("number","123"),
                new ParseTree("word","cd")
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
        var tree = new ParseTree(
                "sentence",
                new ParseTree("word","abc"),
                new ParseTree("number","123"),
                new ParseTree("word","def")
        );


        final @NotNull Parser whitespace_or_comments = Alpha.parser(
                """
                        ws-or-comments = #'\\s+' | comments
                        comments = comment+
                        comment = '(*' inside-comment* '*)'
                        inside-comment =  !'*)' !'(*' #'.' | comment
                        """);

        final @NotNull Parser p = Alpha.parser(
                """
                        sentence = token*
                        <token> = word | number
                        word = #'[a-zA-Z]+'
                        number = #'[0-9]+'
                        """,
                new Alpha.ParserCreationOptions(whitespace_or_comments));
        IO2.println(p);
        IO2.println(p.grammar().analyze());
        IO2.println(whitespace_or_comments.parse("a (**)"));

        Assertions.assertEquals(tree, p.parse(" abc 123  def "));
        Assertions.assertEquals(tree, p.parse(" abc 123 (* def *) def"));
    }
}