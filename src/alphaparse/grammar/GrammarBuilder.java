package alphaparse.grammar;

import alphaparse.Sym;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parsing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class GrammarBuilder {
    public final @NotNull LinkedHashMap<Sym, Combinator> productions;
    public final @NotNull ParserCreationOptions options;

    protected GrammarBuilder(final @NotNull ParserCreationOptions options) {
        productions = new LinkedHashMap<>();
        this.options = options;
    }

    public abstract void make();

    public final @NotNull Grammar build() {
        make();
        compress();
        return new Grammar(productions);
    }

    public final void addProduction(
            final @NotNull Sym lhs, final @NotNull Combinator rhs) {
        var existing = productions.putIfAbsent(lhs, rhs);
        if (existing == null)
            return;

        switch (options.redefinitionOption()) {
            case OVERRIDE -> productions.put(lhs, rhs);
            case ERROR -> throw new IllegalArgumentException(
                    "Production already in grammar: " + lhs);
            case CHOICE -> productions.put(lhs, alternation(existing, rhs));
            case KEEP -> {
            }
        }
    }

    public final void addProduction(
            final @NotNull String lhs, final @NotNull Combinator rhs) {
        addProduction(Sym.sym(lhs), rhs);
    }

    public static @NotNull Combinator staticOf(final @Nullable Object c) {
        return switch (c) {
            case null -> EpsilonCombinator.getDefault();
            case Combinator r -> r;
            case String s -> string(s, false);
            case Pattern p -> new TerminalRegexpCombinator(p);
            case Sym s -> new NonTerminalCombinator(s);
            case List<?> l -> new ConcatCombinator(
                    l.stream().map(GrammarBuilder::staticOf).toList());
            case Set<?> s -> new ChoiceCombinator(
                    s.stream().distinct().map(GrammarBuilder::staticOf).toList());
            default -> throw new IllegalArgumentException(
                    String.valueOf(c.getClass()));
        };
    }

    public final @NotNull Combinator of(final @Nullable Object c) {
        return switch (c) {
            case null -> EpsilonCombinator.getDefault();
            case Combinator r -> r;
            case String s -> string(s);
            case Pattern p -> new TerminalRegexpCombinator(p);
            case Sym s -> new NonTerminalCombinator(s);
            case List<?> l -> new ConcatCombinator(
                    l.stream().map(this::of).toList());
            case Set<?> s -> new ChoiceCombinator(
                    s.stream().distinct().map(this::of).toList());
            default -> throw new IllegalArgumentException(
                    String.valueOf(c.getClass()));
        };
    }

    public static @NotNull Combinator string(
            final @NotNull String s, final boolean explicitCasing) {
        if (s.isEmpty())
            return EpsilonCombinator.getDefault();
        return new TerminalStringCombinator(s, explicitCasing);
    }

    public final @NotNull Combinator string(final @NotNull String s) {
        if (s.isEmpty())
            return EpsilonCombinator.getDefault();

        return switch (options.stringCaseInsensitive()) {
            case TRUE -> new TerminalStringCombinator(s, true);
            case FALSE, DEFAULT -> new TerminalStringCombinator(s, false);
        };
    }

    public final @NotNull Combinator hide(final @Nullable Object o) {
        return of(o).enableHideTag();
    }

    public final @NotNull Combinator epsilon() {
        return EpsilonCombinator.getDefault();
    }

    public final @NotNull Combinator eof() {
        return EOFCombinator.getDefault();
    }

    public final @NotNull Combinator concat(
            final @Nullable Object rule, final @Nullable Object... rules) {
        List<@NotNull Combinator> result = Stream.concat(
                        Stream.of(rule),
                        Arrays.stream(rules))
                .filter(Objects::nonNull) // remove epsilons
                .map(this::of)
                .toList();
        if (result.isEmpty())
            return epsilon();
        if (result.size() == 1)
            return result.getFirst();
        var compressedResult = new ArrayList<Combinator>();
        for (Combinator combinator : result) {
            if (combinator instanceof ConcatCombinator cc)
                compressedResult.addAll(cc.getParsers());
            else
                compressedResult.add(combinator);
        }
        return new ConcatCombinator(compressedResult);
    }

    public final @NotNull Combinator alternation(
            final @Nullable Object rule, final @Nullable Object... rules) {
        List<@NotNull Combinator> result = Stream.concat(
                        Stream.of(rule),
                        Arrays.stream(rules))
                .distinct()
                .map(this::of)
                .distinct()
                .toList();
        if (result.isEmpty())
            return epsilon();
        if (result.size() == 1)
            return result.getFirst();
        var compressedResult = new ArrayList<Combinator>();
        for (Combinator combinator : result) {
            if (combinator instanceof ChoiceCombinator cc)
                compressedResult.addAll(cc.getParsers());
            else
                compressedResult.add(combinator);
        }
        return new ChoiceCombinator(compressedResult);
    }

    public final @NotNull Combinator repeat(
            final @Nullable Object obj, final int min, final int max) {
        if (min < 0 || max < min)
            throw new IllegalArgumentException(
                    "Illegal repetition (min=" + min + ", max=" + max + ")");
        if (min == 0 && max == 0)
            return epsilon();

        var r = of(obj);

        if (r instanceof EpsilonCombinator || (min == 1 && max == 1))
            return r;

        if (r instanceof RepetitionCombinator rc) {
            if (rc.getMin() <= 1 && min <= 1) {
                int newMin = (int) Long.min(
                        Integer.MAX_VALUE,
                        ((long) rc.getMin()) * min);
                int newMax = (int) Long.min(
                        Integer.MAX_VALUE,
                        ((long) rc.getMax()) * max);
                return new RepetitionCombinator(rc.getParser(), newMin, newMax);
            }
        }

        return new RepetitionCombinator(r, min, max);
    }

    public final @NotNull Combinator exclude(
            final @Nullable Object o1, final @Nullable Object o2) {
        if (o1 == o2) return epsilon();
        var r1 = of(o1);
        var r2 = of(o2);
        if (Objects.equals(r1, r2)) return epsilon();
        return new ExclusionCombinator(r1, r2);
    }

    public final @NotNull Combinator repeat(
            final @Nullable Object rule, final int exact) {
        return repeat(rule, exact, exact);
    }

    public final @NotNull Combinator repeatMin(
            final @Nullable Object rule, final int min) {
        return repeat(rule, min, Integer.MAX_VALUE);
    }

    public final @NotNull Combinator repeatMax(
            final @Nullable Object rule, final int max) {
        return repeat(rule, 0, max);
    }

    public final @NotNull Combinator zeroOrMore(final @Nullable Object rule) {
        return repeat(rule, 0, Integer.MAX_VALUE);
    }

    public final @NotNull Combinator onceOrMore(final @Nullable Object rule) {
        return repeat(rule, 1, Integer.MAX_VALUE);
    }

    public final @NotNull Combinator optional(final @Nullable Object rule) {
        return repeat(rule, 0, 1);
    }

    private void compress() {
        var buffer = new HashMap<Combinator, Combinator>();

        for (Map.Entry<Sym, Combinator> symCombinatorEntry
                : productions.entrySet()) {
            var value = symCombinatorEntry.getValue();
            var compressedCombinator = compressCombinator(value);
            if (compressedCombinator != value) // Yes, I need a reference equality check here.
                symCombinatorEntry.setValue(compressedCombinator);
        }
    }

    private @NotNull Combinator compressCombinator(
            final @NotNull Combinator combinator) {
        return switch (combinator) {
            case RepetitionCombinator repetitionCombinator -> {
                var min = repetitionCombinator.getMin();
                var max = repetitionCombinator.getMax();
                var parser = compressCombinator(repetitionCombinator.getParser());

                if (min > 1) {
                    yield repetitionCombinator;
                } else if (min == 1 && max == Integer.MAX_VALUE) {
                    yield new PlusCombinator(parser);
                } else if (min == 0 && max == 1) {
                    yield new OptionalCombinator(parser);
                } else if (min == 0 && max == Integer.MAX_VALUE) {
                    yield new CombinatorStar(parser);
                } else {
                    yield repetitionCombinator;
                }
            }
            case CombinatorWithManyParsers combinatorWithManyParsers ->
                    combinatorWithManyParsers.withParsers(combinatorWithManyParsers
                            .getParsers()
                            .stream()
                            .map(this::compressCombinator)
                            .toList());
            case CombinatorWithParser combinatorWithParser -> combinatorWithParser.withParser(
                    compressCombinator(combinatorWithParser.getParser()));
            case NonTerminalCombinator nonTerminalCombinator -> nonTerminalCombinator;
            case CombinatorTerminal combinatorTerminal -> combinatorTerminal;
            case SimpleCombinator simpleCombinator -> simpleCombinator;
        };
    }
}
