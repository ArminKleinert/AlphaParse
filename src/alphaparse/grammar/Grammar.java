package alphaparse.grammar;

import alphaparse.Sym;
import alphaparse.parsing.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A type representing a Grammar.
 */
public final class Grammar extends LinkedHashMap<@NotNull Sym, Combinator> {
    private final @Nullable Sym startSym;

    public Grammar(final @Nullable Sym startSym, final @NotNull Map<? extends Sym, ? extends Combinator> m) {
        super(m);
        this.startSym = startSym;
    }

    /**
     * Creates a new instance from a {@link Map}.
     *
     * @param m The map.
     */
    public Grammar(final @NotNull Map<? extends Sym, ? extends Combinator> m) {
        this(m.isEmpty() ? null : m.entrySet().stream().findFirst().get().getKey(), m);
    }

    public Grammar(final @NotNull SequencedMap<? extends Sym, ? extends Combinator> m) {
        this(m.firstEntry().getKey(), m);
    }

    public @Nullable Sym getStartSym() {
        return startSym;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grammar g)) return false;

        if (g.size() != size())
            return false;

        for (Map.Entry<@NotNull Sym, Combinator> keywordCombinatorEntry : sequencedEntrySet()) {
            if (!Objects.equals(
                    g.getProduction(keywordCombinatorEntry.getKey()),
                    keywordCombinatorEntry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the right-hand side of the production associated with the key.
     *
     * @param key The right-hand side of the production.
     * @return The production or null if not found.
     */
    public @Nullable Combinator getProduction(final @NotNull Sym key) {
        return getOrDefault(key, null);
    }

    @Override
    public Combinator get(Object key) {
        throw new UnsupportedOperationException("Please use getProduction(key) instead of get(key).");
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Replaces all combinators that have the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#INITIAL} (the default when created) with combinators with the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#TAGGED_PARSE_TREE}.
     *
     * @return A new grammar.
     */
    public @NotNull Grammar applyStandardReductions() {
        final LinkedHashMap<Sym, Combinator> m = new LinkedHashMap<>();
        this.forEach((prodKey, pars) -> {
            if (pars.getReduction().getReductionType() == ReductionType.ReductionTypesAvailable.INITIAL) {
                pars = pars.withReduction(ReductionType.defaultNonRawReduction(prodKey));
            }
            m.put(prodKey, pars);
        });
        return new Grammar(m);
    }

    /**
     * Returns some information about the Grammar.
     *
     * @return An instance of {@link GrammarInfo}.
     * @see GrammarInfo
     */
    public @NotNull GrammarInfo analyze() {
        return new GrammarInfo(this);
    }

    /**
     * Holds some information about a grammar.
     *
     * @param grammar The grammar.
     */
    public record GrammarInfo(@NotNull Grammar grammar) {
        /**
         * All non-terminals of the Grammar.
         *
         * @return A collection of all non-terminals of the Grammar.
         */

        public @NotNull Collection<@NotNull Sym> definedNTs() {
            return grammar.keySet();
        }

        /**
         * All non-terminals which appear on the right-hand side of any production.
         *
         * @return A collection of all non-terminals which appear on the right-hand side of any production.
         */
        public @NotNull Collection<@NotNull Sym> usedNTs() {
            final @NotNull Set<NonTerminalCombinator> result = new HashSet<>();
            final @NotNull Set<Combinator> analyzedCombinators = new HashSet<>();
            final @NotNull ArrayList<@NotNull Combinator> combinatorStack = new ArrayList<>(grammar.values());
            @NotNull Combinator parser;

            while (!combinatorStack.isEmpty()) {
                parser = combinatorStack.removeLast();
                if (analyzedCombinators.contains(parser)) {
                    continue;
                }
                analyzedCombinators.add(parser);

                switch (parser) {
                    case NonTerminalCombinator nonTerminalCombinator -> result.add(nonTerminalCombinator);
                    case CombinatorTerminal ignored -> {
                    }
                    case SimpleCombinator ignored -> {
                    }
                    case CombinatorWithManyParsers combinatorWithManyParsers ->
                            combinatorStack.addAll(combinatorWithManyParsers.getParsers());
                    case CombinatorWithParser combinatorWithParser -> combinatorStack.add(combinatorWithParser.getParser());
                }
            }

            return result.stream().map(NonTerminalCombinator::getKeyword).collect(Collectors.toSet());
        }

        /**
         * Returns all NTs that are defined but unused. Effectively, this is a list of unused productions.
         * <p>
         * Note that the implementation makes no guarantees about the type, except that it's a {@link List} or {@link Set}.
         * However, each key appears only once in the collection.
         * </p>
         * <p>
         * Example:
         * S : 'a' 'b'
         * A : 'c'
         * The key "A" is defined, but never used.
         * </p>
         *
         * @return A collection of unused non-terminals.
         */
        public @NotNull Collection<Sym> getUnusedNTs() {
            return definedNTs().stream().filter(it -> !usedNTs().contains(it)).collect(Collectors.toSet());
        }

        /**
         * Returns a Collection of keys that are used, but not defined. For any valid grammar, the returned collection is empty.
         * <p>
         * Example:
         * S : A 'b'
         * The key "A" is used, but never defined.
         * </p>
         *
         * @return A collection which is hopefully empty.
         */
        public @NotNull Collection<Sym> getUndefinedUsedNTs() {
            return usedNTs().stream().filter(it -> !definedNTs().contains(it)).collect(Collectors.toSet());
        }

        /**
         * Checks whether the Grammar is valid.
         *
         * @return true or false
         * @see GrammarInfo#getUndefinedUsedNTs()
         */
        public boolean isValid() {
            return getUndefinedUsedNTs().isEmpty();
        }

        /**
         * This method collects all rules contained in the grammar.
         * <pre>
         * {@code
         *   // Pseudocode
         *   collectRules(S := ('0' | '1')*) == Set['0', '1', ('0'|'1'), ('0'|'1')*]
         * }
         * </pre>
         *
         * @param predicate A predicate which can be used to collect only rules which match it.
         * @return A collection of all rules contained in the grammar.
         */
        public @NotNull Collection<Combinator> collectRules(@Nullable Predicate<Combinator> predicate) {
            if (predicate == null) predicate = (it) -> true;
            var result = new LinkedHashSet<Combinator>();

            List<Combinator> stack = new ArrayList<>(grammar.values());
            while (!stack.isEmpty()) {
                @NotNull Combinator top = stack.removeLast();
                if (top instanceof CombinatorWithParser topC) stack.add((topC).getParser());
                else if (top instanceof CombinatorWithManyParsers topC) stack.addAll(topC.getParsers());

                if (predicate.test(top)) result.add(top);
            }

            return result;
        }

        @Override
        public @NotNull String toString() {
            return "GrammarInfo[" +
                    "definedNTs=" + definedNTs() +
                    ", usedNTs=" + usedNTs() +
                    ", unusedNTs=" + getUnusedNTs() +
                    ", undefinedUsedNTs=" + getUndefinedUsedNTs() +
                    ", isValid=" + isValid() +
                    ']';
        }
    }

    @Override
    public Grammar reversed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Sym, Combinator> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Sym, Combinator> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putFirst(Sym k, Combinator v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putLast(Sym k, Combinator v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator put(Sym key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends @NotNull Sym, ? extends Combinator> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super @NotNull Sym, ? super Combinator, ? extends Combinator> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putIfAbsent(Sym key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(Sym key, Combinator oldValue, Combinator newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator replace(Sym key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator computeIfAbsent(@NotNull Sym key, Function<? super @NotNull Sym, ? extends Combinator> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator computeIfPresent(Sym key, BiFunction<? super Sym, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator compute(Sym key, BiFunction<? super Sym, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator merge(Sym key, Combinator value, BiFunction<? super Combinator, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }
}
