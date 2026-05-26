package alphaparse.parser_options;

import alphaparse.parsing.*;
import alphaparse.parsing.combinator_factory.CombinatorFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/**
 * Rules that can be used when building parsers. Some appear only in EBNF, some only in ABNF.
 * <p>
 * If some kind of rule is not allowed, there are alternatives for most.
 * Check the source code of {@code RuleAlternativesTests} (cannot link here because that file is in the "test" folder).
 */
public enum RulesAvailable {
    /**
     * Regex rules.
     * <p>
     * Notation: {@code #'...'} or {@code #"..."}
     * <p>
     * Possible replacements through other rule types:
     * Can be replaced by using combinations of all other kinds of rules, mainly Strings.
     *
     * @see TerminalRegexpCombinator
     */
    REGEX,

    /**
     * "Once or more" repetition.
     * <p>
     * Notation: {@code rule+}
     * <p>
     * Possible replacements through other rule types:
     * Can be replaced by using {@link #OPTIONAL_REPETITION}.
     *
     * @see PlusCombinator
     */
    PLUS,

    /**
     * "Alternation" or "choice" rule.
     * <p>
     * Notation: {@code rule1 | rule2}
     * <p>
     * Possible replacements through other rule types: None.
     *
     * @see ChoiceCombinator
     */
    ALTERNATION,

    /**
     * "Zero or more" repetition.
     * <p>
     * Notation: {@code {rule}}
     * <p>
     * Possible replacements through other rule types:
     * Can be replaced by using {@link #ALTERNATION} and more productions.
     *
     * @see RulesAvailable#OPTIONAL_REPETITION_STAR
     * @see CombinatorStar
     */
    OPTIONAL_REPETITION,

    /**
     * "Zero or more" repetition. Similar to {@link #OPTIONAL_REPETITION}, but different notation.
     * <p>
     * Notation: {@code rule*}
     * <p>
     * Possible replacements through other rule types: Equivalent to {@link #OPTIONAL_REPETITION}.
     *
     * @see RulesAvailable#OPTIONAL_REPETITION
     * @see CombinatorStar
     */
    OPTIONAL_REPETITION_STAR,

    /**
     * Empty or "end of input" rule. This rule can be inferred by AlphaParse and is thus optional.
     * For formats, see {@link EpsilonCombinator}.
     * <p>
     * Possible replacements through other rule types:
     * Epsilon can be replaced by an empty String terminal.
     *
     * @see EpsilonCombinator
     */
    EPSILON,

    /**
     * "Lookahead" or "expect" rule.
     * <p>
     * Notation: {@code &look rule}.
     * <p>
     * Example: {@code &'a' ('a' | 'b')} means "use the alternation a|b, but start with an 'a'". I can't think of better examples.
     * <p>
     * Possible replacements through other rule types: None.
     *
     * @see LookaheadCombinator
     */
    LOOKAHEAD,

    /**
     * Negative lookahead.
     * <p>
     * Notation: {@code !look rule}
     * <p>
     * Example: {@code !'a' ('a' | 'b')} means "use the alternation a|b, but do NOT start with an 'a'". I can't think of better examples.
     * <p>
     * Possible replacements through other rule types: None.
     *
     * @see NegativeLookaheadCombinator
     */
    NEGATIVE_LOOKAHEAD,

    /**
     * Single-quoted strings are technically not allowed by EBNF.
     * <p>
     * Possible replacements through other rule types:
     * {@code S = 'a'} can be safely replaced by {@code S = "a"}, but this requires escaping the quotation-marks in code.
     *
     * @see TerminalStringCombinator
     */
    SINGLY_QUOTED,

    /**
     * ABNF string prefixes {@code %i"..."} for case insensitivity and {@code %s"..."} for forced case sensitivity.
     * <p>
     * {@code S = %i"..."} is already implicit in ABNF, but needs to be explicit in other formats.
     * {@code S = %s"..."} is already implicit in EBNF, but needs to be explicit in ABNF.
     * <p>
     * Possible replacements through other rule types: None.
     *
     * @see CombinatorFactory#stringTerminal(String, boolean)
     */
    STRING_CASE_SENSITIVITY_PREFIX,

    /**
     * ABNF value range.
     * <p>
     * Notation: {@code %xXXXX} or {@code %xXXXX-XXXX}, {@code %bBBBB} or {@code %bBBBB-BBBB}, {@code %dDDDD} or {@code %dDDDD-DDDD}. "X" denotes a hexadecimal digit, "B" denotes a binary digit, "O" (uppercase o) denotes an octal digit.
     * <p>
     * Possible replacements through other rule types:
     * A value range can be replaced by a regex or an alternation of string terminals.
     * If you need multiple characters outside the range of 16-bit characters, value ranges become useful.
     *
     * @see TerminalUnicodeCharCombinator
     */
    VALUE_RANGE,

    /**
     * ABNF-style choice combinator '/' with the extension that the output should be ordered and deterministic.
     * <p>
     * Notation: {@code rule1 / rule2}
     * <p>
     * Possible replacements through other rule types: None, but {@link #ALTERNATION} is close enough.
     *
     * @see OrderedChoiceCombinator
     */
    ORDERED_CHOICE,

    /**
     * "Zero or once" or "Optional" rule.
     * <p>
     * Notation: {@code [rule]}
     * <p>
     * Possible replacements through other rule types:
     * {@code S = [rule]} can be replaced by {@code S = rule | epsilon}
     *
     * @see RulesAvailable#OPTIONAL_QUERY
     * @see OptionalCombinator
     */
    OPTIONAL,

    /**
     * Similar to {@link #OPTIONAL}, but different notation.
     * <p>
     * Notation: {@code rule?}
     * <p>
     * Possible replacements through other rule types: Equivalent to {@link #OPTIONAL}.
     *
     * @see RulesAvailable#OPTIONAL
     * @see OptionalCombinator
     */
    OPTIONAL_QUERY,

    /**
     * ABNF "counted repetition" or "variable repetition" rule, notated by a star-prefix.
     * <p>
     * Notation: {@code n*m rule} or {@code n* rule} or {@code *m rule} or {@code n rule} or {@code * rule} (this is only available if {@link RulesAvailable#OPTIONAL_REPETITION_STAR} is not allowed).
     * <p>
     * Possible replacements through other rule types:
     * <ul>
     *     <li>{@code n*m rule} can be replaced by using n occurrences of the rule followed by m-n {@link #OPTIONAL} occurrences of the rule</li>
     *     <li>{@code n* rule} can be replaced by using n occurrences of the rule followed by an {@link #OPTIONAL_REPETITION} of the rule</li>
     *     <li>{@code *m rule} can be replaced by using m {@link #OPTIONAL} occurrences of the rule</li>
     *     <li>{@code n rule} is equivalent to n occurrences of the rule</li>
     *     <li>{@code * rule} is equivalent to {@link #OPTIONAL_REPETITION}</li>
     * </ul>
     *
     * @see RepetitionCombinator
     */
    VARIABLE_REPEAT,

    /**
     * Various ABNF rules.
     *
     * <pre>
     * {@code
     * ALPHA  = #"[a-zA-Z]"
     * BIT    = #"[01]"
     * CHAR   = #"[\\u0001-\\u007F]"        // 7-bit ascii, excloding NULL
     * CR     = "\r"                        // Carriage return
     * CRLF   = "\r\n"                      // Carriage return + line feed
     * CTL    = #"[\\u0000-\\u001F|\\u007F]"
     * DIGIT  = #"[0-9]"
     * DQUOTE = "\""                        // Double quote character
     * HEXDIG = #"[0-9a-fA-F]"
     * HTAB   = #"\t"                       // Horizontal tab
     * LF     = #"\n"                       // Line feed
     * LWSP   = *(WSP / CRLF WSP)
     * OCTET  = #"[\\u0000-\\u00FF]"
     * SP     = " "                         // Space
     * VCHAR  = #"[\\u0021-\\u007E]"
     * WSP    = SP / HTAB                   // Space or horizontal tag
     * }
     * </pre>
     * <p>
     * Possible replacements through other rule types: See above.
     */
    ABNF_CORE,

    /**
     * EBNF and ABNF identifiers normally must have the following form: {@code letter (letter|digit|_)+}.
     * <p>
     * With this option, any character can be used except those which are used for other purposes:
     * {@code " ' ! ? + * [ ] ( ) { } < > : = / | # & }
     * <p>
     * This means that the production {@code 🎁 = "a"} becomes legal with this option.
     * <p>
     * Possible replacements through other rule types: N.A.
     */
    EXTENDED_IDENTIFIERS,

    /**
     * Explicit EOF (end-of-file) rule. This can be useful in *very* specific circumstances, for example a negative lookahead which contains an EOF.
     */
    EXPLICIT_EOF;

    /**
     * Rules that appear in EBNF.
     * <p>
     * {@link RulesAvailable#ALTERNATION},
     * {@link RulesAvailable#EPSILON},
     * {@link RulesAvailable#OPTIONAL},
     * {@link RulesAvailable#OPTIONAL_REPETITION},
     * {@link RulesAvailable#REGEX},
     * {@link RulesAvailable#SINGLY_QUOTED},
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> pureEbnfRules() {
        return Set.of(
                ALTERNATION,
                EPSILON,
                OPTIONAL,
                OPTIONAL_REPETITION,
                REGEX,
                SINGLY_QUOTED);
    }

    /**
     * Rules that appear in EBNF.
     * <p>
     * {@link RulesAvailable#ALTERNATION},
     * {@link RulesAvailable#EPSILON},
     * {@link RulesAvailable#LOOKAHEAD},
     * {@link RulesAvailable#NEGATIVE_LOOKAHEAD},
     * {@link RulesAvailable#OPTIONAL},
     * {@link RulesAvailable#OPTIONAL_QUERY},
     * {@link RulesAvailable#OPTIONAL_REPETITION},
     * {@link RulesAvailable#OPTIONAL_REPETITION_STAR},
     * {@link RulesAvailable#PLUS},
     * {@link RulesAvailable#REGEX},
     * {@link RulesAvailable#SINGLY_QUOTED}
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> ebnfRules() {
        return Set.of(
                ALTERNATION,
                EPSILON,
                LOOKAHEAD,
                NEGATIVE_LOOKAHEAD,
                OPTIONAL,
                OPTIONAL_QUERY,
                OPTIONAL_REPETITION,
                OPTIONAL_REPETITION_STAR,
                PLUS,
                REGEX,
                SINGLY_QUOTED);
    }

    /**
     * Rules that appear in ABNF.
     * <p>
     * {@link RulesAvailable#ABNF_CORE},
     * {@link RulesAvailable#VALUE_RANGE},
     * {@link RulesAvailable#OPTIONAL},
     * {@link RulesAvailable#ORDERED_CHOICE},
     * {@link RulesAvailable#REGEX},
     * {@link RulesAvailable#STRING_CASE_SENSITIVITY_PREFIX},
     * {@link RulesAvailable#VARIABLE_REPEAT}
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see ParserCreationOptions
     */
    public static @NotNull @Unmodifiable Set<RulesAvailable> abnfRules() {
        return Set.of(
                ABNF_CORE,
                VALUE_RANGE,
                OPTIONAL,
                ORDERED_CHOICE,
                REGEX,
                STRING_CASE_SENSITIVITY_PREFIX,
                VARIABLE_REPEAT);
    }

    /**
     * The standard set of rules that AlphaParse allows.
     * <p>
     * {@link RulesAvailable#ALTERNATION},
     * {@link RulesAvailable#VALUE_RANGE},
     * {@link RulesAvailable#EPSILON},
     * {@link RulesAvailable#EXTENDED_IDENTIFIERS},
     * {@link RulesAvailable#LOOKAHEAD},
     * {@link RulesAvailable#NEGATIVE_LOOKAHEAD},
     * {@link RulesAvailable#OPTIONAL},
     * {@link RulesAvailable#OPTIONAL_QUERY},
     * {@link RulesAvailable#OPTIONAL_REPETITION},
     * {@link RulesAvailable#OPTIONAL_REPETITION_STAR},
     * {@link RulesAvailable#ORDERED_CHOICE},
     * {@link RulesAvailable#PLUS},
     * {@link RulesAvailable#REGEX},
     * {@link RulesAvailable#SINGLY_QUOTED},
     * {@link RulesAvailable#STRING_CASE_SENSITIVITY_PREFIX},
     * {@link RulesAvailable#VARIABLE_REPEAT}
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> defaultRules() {
        return Set.of(
                ALTERNATION,
                VALUE_RANGE,
                EPSILON,
                EXTENDED_IDENTIFIERS,
                LOOKAHEAD,
                NEGATIVE_LOOKAHEAD,
                OPTIONAL,
                OPTIONAL_QUERY,
                OPTIONAL_REPETITION,
                OPTIONAL_REPETITION_STAR,
                ORDERED_CHOICE,
                PLUS,
                REGEX,
                SINGLY_QUOTED,
                STRING_CASE_SENSITIVITY_PREFIX,
                VARIABLE_REPEAT);
    }
}
