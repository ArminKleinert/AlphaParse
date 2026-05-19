package alphaparse.parser_options;

import alphaparse.parser.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/**
 * Rules that can be used when building parsers. Some appear only in EBNF, some only in ABNF.
 */
public enum RulesAvailable {
    /**
     * Regex rules.
     * <p>
     * Notation: {@code #'...'} or {@code #"..."}
     *
     * @see TerminalRegexpCombinator
     */
    REGEX,

    /**
     * "Once or more" repetition.
     * <p>
     * Notation: {@code rule+}
     *
     * @see PlusCombinator
     */
    PLUS,

    /**
     * "Alternation" or "choice" rule.
     * <p>
     * Notation: {@code rule1 | rule2}
     *
     * @see ChoiceCombinator
     */
    CHOICE,

    /**
     * "Zero or more" repetition.
     * <p>
     * Notation: {@code {rule}} or {@code rule*}
     *
     * @see CombinatorStar
     */
    STAR,

    /**
     * Empty or "end of input" rule. This rule can be inferred by Alphaparse and is thus optional.
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
     *
     * @see NegativeLookaheadCombinator
     */
    NEGATIVE_LOOKAHEAD,

    /**
     * Singly quoted strings are technically not allowed by EBNF.
     *
     * @see TerminalStringCombinator
     */
    SINGLY_QUOTED,

    /**
     * ABNF char range.
     * <p>
     * Notation: {@code %xXXXX} or {@code %xXXXX-XXXX}, {@code %bBBBB} or {@code %bBBBB-BBBB}, {@code %dDDDD} or {@code %dDDDD-DDDD}. "X" denotes a hexadecimal digit, "B" denotes a binary digit, "O" (uppercase o) denotes an octal digit.
     *
     * @see TerminalUnicodeCharCombinator
     */
    CHAR_RANGE,

    /**
     * ABNF-style choice combinator '/' with the extension that the output should be ordered and deterministic.
     * <p>
     * Notation:  {@code rule1 / rule2}
     *
     * @see OrderedChoiceCombinator
     */
    ORDERED_CHOICE,

    /**
     * "Zero or once" or "Optional" rule.
     * <p>
     * Notation: {@code [rule]} or {@code rule?}
     *
     * @see OptionalCombinator
     */
    OPTIONAL,

    /**
     * ABNF "Counted repetition" rule, notated by a star-prefix.
     * <p>
     * Notation: {@code n*m rule} or {@code n* rule} or {@code *m rule}
     *
     * @see RepetitionCombinator
     */
    COUNTED_REPEAT,

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
     */
    ABNF_CORE,

    /**
     * EBNF and ABNF identifiers normally must have the following form: {@code letter (letter|digit|_)+}.
     * <p>
     * With this option, any character can be used except those which are used for other purposes:
     * {@code " ' ! ? + * [ ] ( ) { } < > : = / | # & }
     * <p>
     * This means that the production {@code 🎁 = "a"} becomes legal with this option.
     */
    EXTENDED_IDENTIFIERS;

    /**
     * Rules that appear in EBNF.
     * <p>
     * {@link RulesAvailable#CHOICE},
     * {@link RulesAvailable#EPSILON},
     * {@link RulesAvailable#LOOKAHEAD},
     * {@link RulesAvailable#NEGATIVE_LOOKAHEAD},
     * {@link RulesAvailable#OPTIONAL},
     * {@link RulesAvailable#PLUS},
     * {@link RulesAvailable#REGEX},
     * {@link RulesAvailable#SINGLY_QUOTED},
     * {@link RulesAvailable#STAR}
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> EBNF_RULES() {
        return Set.of(
                CHOICE, EPSILON, LOOKAHEAD, NEGATIVE_LOOKAHEAD, OPTIONAL, PLUS,
                REGEX, SINGLY_QUOTED, STAR);
    }

    /**
     * Rules that appear in ABNF.
     * <p>
     * {@link RulesAvailable#ABNF_CORE},
     * {@link RulesAvailable#CHAR_RANGE},
     * {@link RulesAvailable#COUNTED_REPEAT},
     * {@link RulesAvailable#OPTIONAL},
     * {@link RulesAvailable#ORDERED_CHOICE},
     * {@link RulesAvailable#PLUS},
     * {@link RulesAvailable#REGEX}
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see ParserCreationOptions
     */
    public static @NotNull @Unmodifiable Set<RulesAvailable> ABNF_RULES() {
        return Set.of(
                ABNF_CORE, CHAR_RANGE, COUNTED_REPEAT, OPTIONAL, ORDERED_CHOICE,
                PLUS, REGEX);
    }

    /**
     * The standard set of rules that Alphaparse allows.
     * <p>
     * {@link RulesAvailable#CHAR_RANGE},
     * {@link RulesAvailable#CHOICE},
     * {@link RulesAvailable#COUNTED_REPEAT},
     * {@link RulesAvailable#EPSILON},
     * {@link RulesAvailable#EXTENDED_IDENTIFIERS},
     * {@link RulesAvailable#LOOKAHEAD},
     * {@link RulesAvailable#NEGATIVE_LOOKAHEAD},
     * {@link RulesAvailable#OPTIONAL},
     * {@link RulesAvailable#ORDERED_CHOICE},
     * {@link RulesAvailable#PLUS},
     * {@link RulesAvailable#REGEX},
     * {@link RulesAvailable#SINGLY_QUOTED},
     * {@link RulesAvailable#STAR}
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> DEFAULT_RULES() {
        return Set.of(
                CHAR_RANGE, CHOICE, COUNTED_REPEAT, EPSILON, EXTENDED_IDENTIFIERS,
                LOOKAHEAD, NEGATIVE_LOOKAHEAD, OPTIONAL, ORDERED_CHOICE, PLUS,
                REGEX, SINGLY_QUOTED, STAR);
    }
}
