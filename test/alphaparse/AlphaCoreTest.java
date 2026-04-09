package alphaparse;

import alphaparse.parser.Parser;
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
                    seq_of-A-or-B = ('a' | 'b')*
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
            new Alpha.ParserCreationOptions(Keyword.intern("Input")));

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

}