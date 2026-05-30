package alphaparse.parsing.combinator_factory;

import alphaparse.Sym;
import alphaparse.parsing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * As long as this buffer exists, the following is true:
 * {@code if (Objects.equals(x, y)) { buffer.getOrAdd(x) == buffer.getOrAdd(y); }}
 */
final class BufferForCombinators {
    BufferForCombinators() {
    }

    @NotNull Combinator getOrAdd(final @NotNull Combinator c1) {
        return switch (c1) {
            case NonTerminalCombinator c -> getOrAdd(c);
            case TerminalRegexpCombinator c -> getOrAdd(c);
            case TerminalStringCombinator c -> getOrAdd(c);
            case ChoiceCombinator c -> getOrAdd(c);
            case ConcatCombinator c -> getOrAdd(c);
            case OptionalCombinator c -> getOrAdd(c);
            case OrderedChoiceCombinator c -> getOrAdd(c);
            case PlusCombinator c -> getOrAdd(c);
            case RepetitionCombinator c -> getOrAdd(c);
            case CombinatorStar c -> getOrAdd(c);
            case TerminalUnicodeCharCombinator c -> getOrAdd(c);
            case NegativeLookaheadCombinator c -> getOrAdd(c);
            case LookaheadCombinator c -> getOrAdd(c);
            case EpsilonCombinator c -> c;
            case TerminalSpecialSequenceCombinator c -> c;
            case ExclusionCombinator c-> getOrAdd(c);
            case EOFCombinator eofCombinator -> eofCombinator;
        };
    }

    private final @NotNull Map<@NotNull NonTerminalCombinator, @NotNull NonTerminalCombinator>
            nonTerminalSet = new HashMap<>();

    @NotNull NonTerminalCombinator getOrAdd(final @NotNull NonTerminalCombinator c) {
        final var temp = nonTerminalSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull ExclusionCombinator, @NotNull ExclusionCombinator>
            exclusionSet = new HashMap<>();

    @NotNull ExclusionCombinator getOrAdd(final @NotNull ExclusionCombinator c) {
        final var temp = exclusionSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull TerminalRegexpCombinator, @NotNull TerminalRegexpCombinator>
            regexpTerminalSet = new HashMap<>();

    @NotNull TerminalRegexpCombinator getOrAdd(final @NotNull TerminalRegexpCombinator c) {
        final var temp = regexpTerminalSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull TerminalStringCombinator, @NotNull TerminalStringCombinator>
            combStringTerminalSet = new HashMap<>();

    @NotNull TerminalStringCombinator getOrAdd(final @NotNull TerminalStringCombinator c) {
        final var temp = combStringTerminalSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull ChoiceCombinator, @NotNull ChoiceCombinator>
            alternationCombinatorSet = new HashMap<>();

    @NotNull ChoiceCombinator getOrAdd(final @NotNull ChoiceCombinator c) {
        final var temp = alternationCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull ConcatCombinator, @NotNull ConcatCombinator>
            catCombinatorSet = new HashMap<>();

    @NotNull ConcatCombinator getOrAdd(final @NotNull ConcatCombinator c) {
        final var temp = catCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull OptionalCombinator, @NotNull OptionalCombinator>
            alternationCombinators = new HashMap<>();

    @NotNull OptionalCombinator getOrAdd(final @NotNull OptionalCombinator c) {
        final var temp = alternationCombinators.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull OrderedChoiceCombinator, @NotNull OrderedChoiceCombinator>
            orderedCombinatorSet = new HashMap<>();

    @NotNull OrderedChoiceCombinator getOrAdd(final @NotNull OrderedChoiceCombinator c) {
        final var temp = orderedCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull PlusCombinator, @NotNull PlusCombinator>
            plusCombinatorSet = new HashMap<>();

    @NotNull PlusCombinator getOrAdd(final @NotNull PlusCombinator c) {
        final var temp = plusCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull RepetitionCombinator, @NotNull RepetitionCombinator>
            repetitionCombinatorSet = new HashMap<>();

    @NotNull RepetitionCombinator getOrAdd(final @NotNull RepetitionCombinator c) {
        final var temp = repetitionCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull CombinatorStar, @NotNull CombinatorStar>
            starCombinatorSet = new HashMap<>();

    @NotNull CombinatorStar getOrAdd(final @NotNull CombinatorStar c) {
        final var temp = starCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull TerminalUnicodeCharCombinator, @NotNull TerminalUnicodeCharCombinator>
            unicodeCharTerminalSet = new HashMap<>();

    @NotNull TerminalUnicodeCharCombinator getOrAdd(final @NotNull TerminalUnicodeCharCombinator c) {
        final var temp = unicodeCharTerminalSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull EpsilonCombinator, @NotNull EpsilonCombinator>
            epsilonCombinatorSet = new HashMap<>();

    @NotNull EpsilonCombinator getOrAdd(final @NotNull EpsilonCombinator c) {
        final var temp = epsilonCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull NegativeLookaheadCombinator, @NotNull NegativeLookaheadCombinator>
            negateCombinatorSet = new HashMap<>();

    @NotNull NegativeLookaheadCombinator getOrAdd(final @NotNull NegativeLookaheadCombinator c) {
        final var temp = negateCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull LookaheadCombinator, @NotNull LookaheadCombinator>
            lookaheadCombinatorSet = new HashMap<>();

    @NotNull LookaheadCombinator getOrAdd(final @NotNull LookaheadCombinator c) {
        final var temp = lookaheadCombinatorSet.putIfAbsent(c, c);
        return temp == null ? c : temp;
    }

    private final @NotNull Map<@NotNull Sym, @NotNull NonTerminalCombinator>
            symToNtSet = new HashMap<>();

    @Nullable NonTerminalCombinator nt(@NotNull Sym keyword) {
        return symToNtSet.get(keyword);
    }

    void putNt(@NotNull NonTerminalCombinator temp) {
        symToNtSet.put(temp.getKeyword(), temp);
    }
}
