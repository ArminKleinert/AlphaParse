package alphaparse.parser;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
public final class BufferForCombinators {
    /**
     * TODO
     */
    public BufferForCombinators() {
    }

    private <T extends Combinator> @NotNull T buffer(final @NotNull Map<@NotNull T, @NotNull T> buff, final @NotNull T c) {
        final T temp = buff.get(c);
        if (temp != null) return temp;
        buff.put(c, c);
        return c;
    }

    /**
     * TODO
     *
     * @param combinator1 TODO
     * @param <T>         TODO
     * @return TODO
     */
    public @NotNull <T extends Combinator> Combinator getOrAdd(final @NotNull T combinator1) {
        return switch (combinator1) {
            case NonTerminalCombinator combinator -> getOrAdd(combinator);
            case TerminalRegexpCombinator combinator -> getOrAdd(combinator);
            case TerminalStringCombinator combinator -> getOrAdd(combinator);
            case ChoiceCombinator combinator -> getOrAdd(combinator);
            case ConcatCombinator combinator -> getOrAdd(combinator);
            case OptionalCombinator combinator -> getOrAdd(combinator);
            case OrderedChoiceCombinator combinator -> getOrAdd(combinator);
            case PlusCombinator combinator -> getOrAdd(combinator);
            case RepetitionCombinator combinator -> getOrAdd(combinator);
            case CombinatorStar combinator -> getOrAdd(combinator);
            case TerminalUnicodeCharCombinator combinator -> getOrAdd(combinator);
            case EpsilonCombinator combinator -> getOrAdd(combinator);
            case NegativeLookaheadCombinator combinator -> getOrAdd(combinator);
            case LookaheadCombinator combinator -> getOrAdd(combinator);
        };
    }

    private final @NotNull Map<@NotNull NonTerminalCombinator, @NotNull NonTerminalCombinator> nonTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull NonTerminalCombinator getOrAdd(final @NotNull NonTerminalCombinator combinator) {
        return buffer(nonTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull TerminalRegexpCombinator, @NotNull TerminalRegexpCombinator> regexpTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull TerminalRegexpCombinator getOrAdd(final @NotNull TerminalRegexpCombinator combinator) {
        return buffer(regexpTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull TerminalStringCombinator, @NotNull TerminalStringCombinator> combStringTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull TerminalStringCombinator getOrAdd(final @NotNull TerminalStringCombinator combinator) {
        return buffer(combStringTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull ChoiceCombinator, @NotNull ChoiceCombinator> alternationCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull ChoiceCombinator getOrAdd(final @NotNull ChoiceCombinator combinator) {
        return buffer(alternationCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull ConcatCombinator, @NotNull ConcatCombinator> catCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull ConcatCombinator getOrAdd(final @NotNull ConcatCombinator combinator) {
        return buffer(catCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull OptionalCombinator, @NotNull OptionalCombinator> alternationCombinators = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull OptionalCombinator getOrAdd(final @NotNull OptionalCombinator combinator) {
        return buffer(alternationCombinators, combinator);
    }

    private final @NotNull Map<@NotNull OrderedChoiceCombinator, @NotNull OrderedChoiceCombinator> orderedCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull OrderedChoiceCombinator getOrAdd(final @NotNull OrderedChoiceCombinator combinator) {
        return buffer(orderedCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull PlusCombinator, @NotNull PlusCombinator> plusCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull PlusCombinator getOrAdd(final @NotNull PlusCombinator combinator) {
        return buffer(plusCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull RepetitionCombinator, @NotNull RepetitionCombinator> repetitionCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull RepetitionCombinator getOrAdd(final @NotNull RepetitionCombinator combinator) {
        return buffer(repetitionCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorStar, @NotNull CombinatorStar> starCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorStar getOrAdd(final @NotNull CombinatorStar combinator) {
        return buffer(starCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull TerminalUnicodeCharCombinator, @NotNull TerminalUnicodeCharCombinator> unicodeCharTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull TerminalUnicodeCharCombinator getOrAdd(final @NotNull TerminalUnicodeCharCombinator combinator) {
        return buffer(unicodeCharTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull EpsilonCombinator, @NotNull EpsilonCombinator> epsilonCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull EpsilonCombinator getOrAdd(final @NotNull EpsilonCombinator combinator) {
        return buffer(epsilonCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull NegativeLookaheadCombinator, @NotNull NegativeLookaheadCombinator> negateCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull NegativeLookaheadCombinator getOrAdd(final @NotNull NegativeLookaheadCombinator combinator) {
        return buffer(negateCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull LookaheadCombinator, @NotNull LookaheadCombinator> lookaheadCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull LookaheadCombinator getOrAdd(final @NotNull LookaheadCombinator combinator) {
        return buffer(lookaheadCombinatorSet, combinator);
    }
}
