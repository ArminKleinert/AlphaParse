package alphaparse;

import alphaparse.parser.Parser;
import alphaparse.reduction.ReductionType;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        Alpha.ParsingOptions options = Alpha.ParsingOptions.getDefault().withOptMemorySetTo(true);
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
        var tree = new ParseTree(ParseTree.NULL_TAG_NAME, "a", "b", "a");

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

}