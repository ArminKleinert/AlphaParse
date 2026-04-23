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
            case CombinatorNonTerminal combinator -> getOrAdd(combinator);
            case CombinatorTerminalRegexp combinator -> getOrAdd(combinator);
            case CombinatorTerminalString combinator -> getOrAdd(combinator);
            case CombinatorChoice combinator -> getOrAdd(combinator);
            case CombinatorConcatenation combinator -> getOrAdd(combinator);
            case CombinatorOptional combinator -> getOrAdd(combinator);
            case CombinatorOrderedChoice combinator -> getOrAdd(combinator);
            case CombinatorPlus combinator -> getOrAdd(combinator);
            case CombinatorRepetition combinator -> getOrAdd(combinator);
            case CombinatorStar combinator -> getOrAdd(combinator);
            case CombinatorTerminalUnicodeChar combinator -> getOrAdd(combinator);
            case CombinatorEpsilon combinator -> getOrAdd(combinator);
            case CombinatorNegativeLookahead combinator -> getOrAdd(combinator);
            case CombinatorLookahead combinator -> getOrAdd(combinator);
        };
    }

    private final @NotNull Map<@NotNull CombinatorNonTerminal, @NotNull CombinatorNonTerminal> nonTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorNonTerminal getOrAdd(final @NotNull CombinatorNonTerminal combinator) {
        return buffer(nonTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorTerminalRegexp, @NotNull CombinatorTerminalRegexp> regexpTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorTerminalRegexp getOrAdd(final @NotNull CombinatorTerminalRegexp combinator) {
        return buffer(regexpTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorTerminalString, @NotNull CombinatorTerminalString> combStringTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorTerminalString getOrAdd(final @NotNull CombinatorTerminalString combinator) {
        return buffer(combStringTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorChoice, @NotNull CombinatorChoice> alternationCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorChoice getOrAdd(final @NotNull CombinatorChoice combinator) {
        return buffer(alternationCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorConcatenation, @NotNull CombinatorConcatenation> catCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorConcatenation getOrAdd(final @NotNull CombinatorConcatenation combinator) {
        return buffer(catCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorOptional, @NotNull CombinatorOptional> alternationCombinators = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorOptional getOrAdd(final @NotNull CombinatorOptional combinator) {
        return buffer(alternationCombinators, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorOrderedChoice, @NotNull CombinatorOrderedChoice> orderedCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorOrderedChoice getOrAdd(final @NotNull CombinatorOrderedChoice combinator) {
        return buffer(orderedCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorPlus, @NotNull CombinatorPlus> plusCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorPlus getOrAdd(final @NotNull CombinatorPlus combinator) {
        return buffer(plusCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorRepetition, @NotNull CombinatorRepetition> repetitionCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorRepetition getOrAdd(final @NotNull CombinatorRepetition combinator) {
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

    private final @NotNull Map<@NotNull CombinatorTerminalUnicodeChar, @NotNull CombinatorTerminalUnicodeChar> unicodeCharTerminalSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorTerminalUnicodeChar getOrAdd(final @NotNull CombinatorTerminalUnicodeChar combinator) {
        return buffer(unicodeCharTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorEpsilon, @NotNull CombinatorEpsilon> epsilonCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorEpsilon getOrAdd(final @NotNull CombinatorEpsilon combinator) {
        return buffer(epsilonCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorNegativeLookahead, @NotNull CombinatorNegativeLookahead> negateCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorNegativeLookahead getOrAdd(final @NotNull CombinatorNegativeLookahead combinator) {
        return buffer(negateCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CombinatorLookahead, @NotNull CombinatorLookahead> lookaheadCombinatorSet = new HashMap<>();

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public @NotNull CombinatorLookahead getOrAdd(final @NotNull CombinatorLookahead combinator) {
        return buffer(lookaheadCombinatorSet, combinator);
    }
}
