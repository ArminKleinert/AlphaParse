package alphaparse.parser;

import org.jetbrains.annotations.NotNull;

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
     * Notation: {@code %xXXXX} or {@code %xXXXX-XXXX}, {@code %bBBBB} or {@code %bBBBB-BBBB}, {@code %oOOOO} or {@code %oOOOO-OOOO}. "X" denotes a hexadecimal digit, "B" denotes a binary digit, "O" (uppercase o) denotes an octal digit.
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
     */
    ABNF_CORE;

    /**
     * Rules that appear in EBNF.
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see alphaparse.Alpha.ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> ebnf() {
        return Set.of(
                REGEX, PLUS, CHOICE, STAR, EPSILON, LOOKAHEAD, NEGATIVE_LOOKAHEAD, SINGLY_QUOTED, OPTIONAL);
    }

    /**
     * Rules that appear in ABNF.
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see alphaparse.Alpha.ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> abnf() {
        return Set.of(
                REGEX, PLUS, CHAR_RANGE, ORDERED_CHOICE, ABNF_CORE, COUNTED_REPEAT, OPTIONAL);
    }

    /**
     * The standard set of rules that Alphaparse allows.
     *
     * @return A set of rule types to allow when constructing a parser.
     * @see alphaparse.Alpha.ParserCreationOptions
     */
    public static @NotNull Set<RulesAvailable> defaultRules() {
        return Set.of(
                REGEX, PLUS, CHOICE, STAR, EPSILON, LOOKAHEAD, NEGATIVE_LOOKAHEAD, SINGLY_QUOTED, ORDERED_CHOICE, COUNTED_REPEAT, OPTIONAL);
    }
}
