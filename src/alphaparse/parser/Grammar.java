package alphaparse.parser;

import alphaparse.Alpha;
import alphaparse.Keyword;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A type representing a Grammar.
 */
public final class Grammar extends LinkedHashMap<@NotNull Keyword, Combinator> {

    /**
     * Creates a new instance from a {@link Map}.
     *
     * @param m The map.
     */
    public Grammar(final @NotNull Map<? extends Keyword, ? extends Combinator> m) {
        super(m);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grammar g)) return false;

        if (g.size() != size())
            return false;

        for (Map.Entry<@NotNull Keyword, Combinator> keywordCombinatorEntry : entrySet()) {
            if (!Objects.equals(
                    g.getProduction(keywordCombinatorEntry.getKey()),
                    keywordCombinatorEntry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a new Grammar from productions. The productions are represented as a list of {@link Map.Entry} instances or any other pair-like type. The keys are the left-hand sides, the values are the right-hand sides. For creating productions, {@link Grammar#entry(Keyword, Combinator)} can be used.
     *
     * @param kvs The productions.
     * @return A new Grammar.
     * @see Grammar#entry(Keyword, Combinator)
     */
    public static @NotNull Grammar fromProductions(
            final @NotNull List<Map.Entry<Keyword, Combinator>> kvs,
            final @NotNull Grammar.RedefinitionOption redefinitionOption) {
        final @NotNull SequencedMap<Keyword, Combinator> m = new LinkedHashMap<>();
//        for (Map.Entry<Keyword, Combinator> kv : kvs)
//            m.put(kv.getKey(), kv.getValue());

        if (redefinitionOption == RedefinitionOption.OVERRIDE) { // Ignore existing
            for (Map.Entry<Keyword, Combinator> kv : kvs)
                m.put(kv.getKey(), kv.getValue());
        } else if (redefinitionOption == RedefinitionOption.ERROR) { // Throw if any production exists with this name
            for (Map.Entry<Keyword, Combinator> kv : kvs)
                if (m.putIfAbsent(kv.getKey(), kv.getValue()) != null)
                    throw new IllegalArgumentException("Duplicate production: " + kv.getKey());
        } else if (redefinitionOption == RedefinitionOption.CHOICE) { // Make a choice combinator if exists
            for (Map.Entry<Keyword, Combinator> kv : kvs) {
                var existing = m.get(kv.getKey()); // Fetch existing production

                // There was no production previously -> Just add
                if (existing == null) {
                    m.put(kv.getKey(), kv.getValue());
                    continue;
                }

                // Production already existed.

                // Buffer the old one
                var newValue = kv.getValue();

                // The existing and the new production are the same -> no point in creating a choice combinator
                if (Objects.equals(existing, newValue))
                    continue;

                // Make a new choice combinator
                final @NotNull ChoiceCombinator choice;

                if (existing instanceof ChoiceCombinator) {
                    // The existing production was not a choice combinator -> Combine old and new
                    final @NotNull var combs = new ArrayList<>(((ChoiceCombinator) existing).getParsers());
                    combs.add(newValue);
                    choice = new ChoiceCombinator(combs);
                } else {
                    // The existing production was not a choice combinator
                    choice = new ChoiceCombinator(List.of(existing, newValue));
                }

                // Add new to grammar.
                m.put(kv.getKey(), choice);
            }
        }

        return new Grammar(m);
    }

    /**
     * Creates a new Grammar from productions. The productions are represented as a list of {@link Map.Entry} instances or any other pair-like type. The keys are the left-hand sides, the values are the right-hand sides. For creating productions, {@link Grammar#entry(Keyword, Combinator)} can be used.
     *
     * @param kvs The productions.
     * @return A new Grammar.
     * @see Grammar#entry(Keyword, Combinator)
     */
    public static @NotNull Grammar fromProductions(
            final @NotNull List<Map.Entry<Keyword, Combinator>> kvs) {
        final @NotNull SequencedMap<Keyword, Combinator> m = new LinkedHashMap<>();
        for (Map.Entry<Keyword, Combinator> kv : kvs) {
            m.put(kv.getKey(), kv.getValue());
        }
        return new Grammar(m);
    }

    /**
     * Gets the right-hand side of the production associated with the key.
     *
     * @param key The right-hand side of the production.
     * @return The production or null if not found.
     */
    public @Nullable Combinator getProduction(final @NotNull Keyword key) {
        return getOrDefault(key, null);
    }

    /**
     * Tries to find the production associated with a key. If none exists, return a new {@link NonTerminalCombinator} for the key.
     *
     * @param key The key.
     * @return The left-hand side associated with the key or a new {@link NonTerminalCombinator} if no production could be found.
     * @see Grammar#getProduction(Keyword)
     * @see NonTerminalCombinator
     */
    public @NotNull Combinator getOrMakeNonTerm(final @NotNull Keyword key) {
        final @Nullable Combinator p = getProduction(key);
        if (p == null) return CombinatorFactory.staticMakeNonTerminal(key);
        return p;
    }

    @Override
    public Combinator get(Object key) {
        throw new UnsupportedOperationException("Please use getProduction(key) instead of get(key).");
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private @NotNull Set<NonTerminalCombinator> listNonTerminals() {
        final @NotNull Set<NonTerminalCombinator> result = new HashSet<>();
        final @NotNull Set<Combinator> analyzedCombinators = new HashSet<>();
        final @NotNull ArrayList<@NotNull Combinator> combinatorStack = new ArrayList<>(values());
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
                case CombinatorWithManyParsers combinatorWithManyParsers ->
                        combinatorStack.addAll(combinatorWithManyParsers.getParsers());
                case CombinatorWithParser combinatorWithParser -> combinatorStack.add(combinatorWithParser.getParser());
            }
        }

        return result;
    }

    /**
     * Replaces all combinators that have the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#INITIAL} (the default when created) with combinators with the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#OUTPUT}.
     *
     * @return A new grammar.
     */
    public @NotNull Grammar applyStandardReductions() {
        final List<Map.Entry<Keyword, Combinator>> m = new ArrayList<>();
        this.forEach((prodKey, pars) -> {
            if (pars.getReduction().getReductionType() == ReductionType.ReductionTypesAvailable.INITIAL)
                pars = pars.withReduction(ReductionType.defaultNonRawReduction(prodKey));

            m.add(Grammar.entry(prodKey, pars));
        });
        return Grammar.fromProductions(m);
    }

    /**
     * Returns some information about the Grammar.
     *
     * @return An instance of {@link GrammarInfo}.
     * @see GrammarInfo
     */
    public @NotNull GrammarInfo analyze() {
        return new GrammarInfo(
                keySet(),
                listNonTerminals().stream().map(NonTerminalCombinator::getKeyword).collect(Collectors.toSet()));
    }

    public enum RedefinitionOption {
        OVERRIDE,
        ERROR,
        CHOICE,
    }

    /**
     * Holds some information about a grammar.
     *
     * @param definedNTs All non-terminals of the Grammar.
     * @param usedNTs    All non-terminals which appear on the right-hand side of any production.
     */
    public record GrammarInfo(@NotNull Collection<@NotNull Keyword> definedNTs,
                              @NotNull Collection<@NotNull Keyword> usedNTs) {
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
        public @NotNull Collection<Keyword> getUnusedNTs() {
            return definedNTs.stream().filter(it -> !usedNTs.contains(it)).collect(Collectors.toSet());
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
        public @NotNull Collection<Keyword> getUndefinedUsedNTs() {
            return usedNTs.stream().filter(it -> !definedNTs.contains(it)).collect(Collectors.toSet());
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

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof GrammarInfo(Collection<Keyword> nTs, Collection<Keyword> ts))) return false;
            return Objects.equals(definedNTs(), nTs) && Objects.equals(usedNTs(), ts);
        }

        @Override
        public int hashCode() {
            return Objects.hash(definedNTs(), usedNTs());
        }

        @Override
        public @NotNull String toString() {
            return "GrammarInfo[" +
                    "definedNTs=" + definedNTs +
                    ", usedNTs=" + usedNTs +
                    ", unusedNTs=" + getUnusedNTs() +
                    ", undefinedUsedNTs=" + getUndefinedUsedNTs() +
                    ", isValid=" + isValid() +
                    ']';
        }
    }

    /**
     * Creates an instance of {@link Map.Entry}, specifically for use with {@link Grammar#fromProductions(List)}.
     *
     * @param k Key.
     * @param v Value.
     * @return An entry.
     */
    public static @NotNull Map.Entry<Keyword, Combinator> entry(final @NotNull Keyword k, final @NotNull Combinator v) {
        return new Map.Entry<>() {
            final @NotNull Keyword key = k;
            @NotNull Combinator value = v;

            @Override
            public @NotNull Keyword getKey() {
                return key;
            }

            @Override
            public @NotNull Combinator getValue() {
                return value;
            }

            @Override
            public Combinator setValue(final @NotNull Combinator combinator) {
                final Combinator old = value;
                value = combinator;
                return old;
            }

            @Override
            public int hashCode() {
                return Objects.hash(key, value);
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof Map.Entry<?, ?> that)) return false;
                return Objects.equals(getKey(), that.getKey()) &&
                        Objects.equals(getValue(), that.getValue());
            }
        };
    }

    @Override
    public Grammar reversed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Keyword, Combinator> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Keyword, Combinator> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putFirst(Keyword k, Combinator v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putLast(Keyword k, Combinator v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator put(Keyword key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends @NotNull Keyword, ? extends Combinator> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super @NotNull Keyword, ? super Combinator, ? extends Combinator> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putIfAbsent(Keyword key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(Keyword key, Combinator oldValue, Combinator newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator replace(Keyword key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator computeIfAbsent(@NotNull Keyword key, Function<? super @NotNull Keyword, ? extends Combinator> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator computeIfPresent(Keyword key, BiFunction<? super Keyword, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator compute(Keyword key, BiFunction<? super Keyword, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator merge(Keyword key, Combinator value, BiFunction<? super Combinator, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }
}
