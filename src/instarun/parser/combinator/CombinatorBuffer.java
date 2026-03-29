package instarun.parser.combinator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CombinatorBuffer {
    private <T extends Combinator> @NotNull T buffer(final @NotNull Map<@NotNull T, @NotNull T> buff, final @NotNull T c) {
        final T temp = buff.get(c);
        if (temp != null) return temp;
        buff.put(c, c);
        return c;
    }

    public @NotNull <T extends Combinator> Combinator getOrAdd(final @NotNull T combinator) {
        return switch (combinator) {
            case NonTerminal that -> getOrAdd(that);
            case RegexpTerminal that -> getOrAdd(that);
            case StringTerminal that -> getOrAdd(that);
            case StringCaseInsensitiveTerminal that -> getOrAdd(that);
            case AlternationCombinator that -> getOrAdd(that);
            case CatCombinator that -> getOrAdd(that);
            case OptCombinator that -> getOrAdd(that);
            case OrderedCombinator that -> getOrAdd(that);
            case PlusCombinator that -> getOrAdd(that);
            case RepetitionCombinator that -> getOrAdd(that);
            case StarCombinator that -> getOrAdd(that);
            case UnicodeCharTerminal that -> getOrAdd(that);
            case EpsilonCombinator that -> getOrAdd(that);
            case NegateCombinator that -> getOrAdd(that);
            case LookaheadCombinator that -> getOrAdd(that);
            default -> throw new IllegalArgumentException();
        };
        //return combinator;
    }

    private final @NotNull Map<@NotNull NonTerminal, @NotNull NonTerminal> nonTerminalSet = new HashMap<>();

    public final @NotNull NonTerminal getOrAdd(final @NotNull NonTerminal combinator) {
        return buffer(nonTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull RegexpTerminal, @NotNull RegexpTerminal> regexpTerminalSet = new HashMap<>();

    public final @NotNull RegexpTerminal getOrAdd(final @NotNull RegexpTerminal combinator) {
        return buffer(regexpTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull StringTerminal, @NotNull StringTerminal> stringTerminalSet = new HashMap<>();

    public final @NotNull StringTerminal getOrAdd(final @NotNull StringTerminal combinator) {
        return buffer(stringTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull StringCaseInsensitiveTerminal, @NotNull StringCaseInsensitiveTerminal> stringCaseInsensitiveTerminalSet = new HashMap<>();

    public final @NotNull StringCaseInsensitiveTerminal getOrAdd(final @NotNull StringCaseInsensitiveTerminal combinator) {
        return buffer(stringCaseInsensitiveTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull AlternationCombinator, @NotNull AlternationCombinator> alternationCombinatorSet = new HashMap<>();

    public final @NotNull AlternationCombinator getOrAdd(final @NotNull AlternationCombinator combinator) {
        return buffer(alternationCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CatCombinator, @NotNull CatCombinator> catCombinatorSet = new HashMap<>();

    public final @NotNull CatCombinator getOrAdd(final @NotNull CatCombinator combinator) {
        return buffer(catCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull OptCombinator, @NotNull OptCombinator> alternationCombinators = new HashMap<>();

    public final @NotNull OptCombinator getOrAdd(final @NotNull OptCombinator combinator) {
        return buffer(alternationCombinators, combinator);
    }

    private final @NotNull Map<@NotNull OrderedCombinator, @NotNull OrderedCombinator> orderedCombinatorSet = new HashMap<>();

    public final @NotNull OrderedCombinator getOrAdd(final @NotNull OrderedCombinator combinator) {
        return buffer(orderedCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull PlusCombinator, @NotNull PlusCombinator> plusCombinatorSet = new HashMap<>();

    public final @NotNull PlusCombinator getOrAdd(final @NotNull PlusCombinator combinator) {
        return buffer(plusCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull RepetitionCombinator, @NotNull RepetitionCombinator> repetitionCombinatorSet = new HashMap<>();

    public final @NotNull RepetitionCombinator getOrAdd(final @NotNull RepetitionCombinator combinator) {
        return buffer(repetitionCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull StarCombinator, @NotNull StarCombinator> starCombinatorSet = new HashMap<>();

    public final @NotNull StarCombinator getOrAdd(final @NotNull StarCombinator combinator) {
        return buffer(starCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull UnicodeCharTerminal, @NotNull UnicodeCharTerminal> unicodeCharTerminalSet = new HashMap<>();

    public final @NotNull UnicodeCharTerminal getOrAdd(final @NotNull UnicodeCharTerminal combinator) {
        return buffer(unicodeCharTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull EpsilonCombinator, @NotNull EpsilonCombinator> epsilonCombinatorSet = new HashMap<>();

    public final @NotNull EpsilonCombinator getOrAdd(final @NotNull EpsilonCombinator combinator) {
        return buffer(epsilonCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull NegateCombinator, @NotNull NegateCombinator> negateCombinatorSet = new HashMap<>();

    public final @NotNull NegateCombinator getOrAdd(final @NotNull NegateCombinator combinator) {
        return buffer(negateCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull LookaheadCombinator, @NotNull LookaheadCombinator> lookaheadCombinatorSet = new HashMap<>();

    public final @NotNull LookaheadCombinator getOrAdd(final @NotNull LookaheadCombinator combinator) {
        return buffer(lookaheadCombinatorSet, combinator);
    }

}
