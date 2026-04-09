package alphaparse.parser.combinator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class CombinatorBuffer {
    private <T extends Combinator> @NotNull T buffer(final @NotNull Map<@NotNull T, @NotNull T> buff, final @NotNull T c) {
        final T temp = buff.get(c);
        if (temp != null) return temp;
        buff.put(c, c);
        return c;
    }

    public @NotNull <T extends Combinator> Combinator getOrAdd(final @NotNull T combinator1) {
        return switch (combinator1) {
            case NonTerminal combinator -> getOrAdd(combinator);
            case RegexpTerminal combinator -> getOrAdd(combinator);
            case StringTerminal combinator -> getOrAdd(combinator);
            case AlternationCombinator combinator -> getOrAdd(combinator);
            case CatCombinator combinator -> getOrAdd(combinator);
            case OptCombinator combinator -> getOrAdd(combinator);
            case OrderedCombinator combinator -> getOrAdd(combinator);
            case PlusCombinator combinator -> getOrAdd(combinator);
            case RepetitionCombinator combinator -> getOrAdd(combinator);
            case StarCombinator combinator -> getOrAdd(combinator);
            case UnicodeCharTerminal combinator -> getOrAdd(combinator);
            case EpsilonCombinator combinator -> getOrAdd(combinator);
            case NegateCombinator combinator -> getOrAdd(combinator);
            case LookaheadCombinator combinator -> getOrAdd(combinator);
        };
    }

    private final @NotNull Map<@NotNull NonTerminal, @NotNull NonTerminal> nonTerminalSet = new HashMap<>();

    public @NotNull NonTerminal getOrAdd(final @NotNull NonTerminal combinator) {
        return buffer(nonTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull RegexpTerminal, @NotNull RegexpTerminal> regexpTerminalSet = new HashMap<>();

    public @NotNull RegexpTerminal getOrAdd(final @NotNull RegexpTerminal combinator) {
        return buffer(regexpTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull StringTerminal, @NotNull StringTerminal> combStringTerminalSet = new HashMap<>();

    public @NotNull StringTerminal getOrAdd(final @NotNull StringTerminal combinator) {
        return buffer(combStringTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull AlternationCombinator, @NotNull AlternationCombinator> alternationCombinatorSet = new HashMap<>();

    public @NotNull AlternationCombinator getOrAdd(final @NotNull AlternationCombinator combinator) {
        return buffer(alternationCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CatCombinator, @NotNull CatCombinator> catCombinatorSet = new HashMap<>();

    public @NotNull CatCombinator getOrAdd(final @NotNull CatCombinator combinator) {
        return buffer(catCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull OptCombinator, @NotNull OptCombinator> alternationCombinators = new HashMap<>();

    public @NotNull OptCombinator getOrAdd(final @NotNull OptCombinator combinator) {
        return buffer(alternationCombinators, combinator);
    }

    private final @NotNull Map<@NotNull OrderedCombinator, @NotNull OrderedCombinator> orderedCombinatorSet = new HashMap<>();

    public @NotNull OrderedCombinator getOrAdd(final @NotNull OrderedCombinator combinator) {
        return buffer(orderedCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull PlusCombinator, @NotNull PlusCombinator> plusCombinatorSet = new HashMap<>();

    public @NotNull PlusCombinator getOrAdd(final @NotNull PlusCombinator combinator) {
        return buffer(plusCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull RepetitionCombinator, @NotNull RepetitionCombinator> repetitionCombinatorSet = new HashMap<>();

    public @NotNull RepetitionCombinator getOrAdd(final @NotNull RepetitionCombinator combinator) {
        return buffer(repetitionCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull StarCombinator, @NotNull StarCombinator> starCombinatorSet = new HashMap<>();

    public @NotNull StarCombinator getOrAdd(final @NotNull StarCombinator combinator) {
        return buffer(starCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull UnicodeCharTerminal, @NotNull UnicodeCharTerminal> unicodeCharTerminalSet = new HashMap<>();

    public @NotNull UnicodeCharTerminal getOrAdd(final @NotNull UnicodeCharTerminal combinator) {
        return buffer(unicodeCharTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull EpsilonCombinator, @NotNull EpsilonCombinator> epsilonCombinatorSet = new HashMap<>();

    public @NotNull EpsilonCombinator getOrAdd(final @NotNull EpsilonCombinator combinator) {
        return buffer(epsilonCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull NegateCombinator, @NotNull NegateCombinator> negateCombinatorSet = new HashMap<>();

    public @NotNull NegateCombinator getOrAdd(final @NotNull NegateCombinator combinator) {
        return buffer(negateCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull LookaheadCombinator, @NotNull LookaheadCombinator> lookaheadCombinatorSet = new HashMap<>();

    public @NotNull LookaheadCombinator getOrAdd(final @NotNull LookaheadCombinator combinator) {
        return buffer(lookaheadCombinatorSet, combinator);
    }

}
