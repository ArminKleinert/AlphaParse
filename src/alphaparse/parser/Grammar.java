package alphaparse.parser;

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
public final class Grammar extends LinkedHashMap<@NotNull String, Combinator> {
    /**
     * Creates a new instance from a {@link Map}.
     *
     * @param m The map.
     */
    public Grammar(final @NotNull Map<? extends String, ? extends Combinator> m) {
        super(m);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grammar g)) return false;

        if (g.size() != size())
            return false;

        for (Map.Entry<@NotNull String, Combinator> keywordCombinatorEntry : sequencedEntrySet()) {
            if (!Objects.equals(
                    g.getProduction(keywordCombinatorEntry.getKey()),
                    keywordCombinatorEntry.getValue())) {
                return false;
            }
        }

        return true;
    }

    private static void addProductionWithRedefinitionOptionChoice(
            final @NotNull SequencedMap<String, Combinator> m,
            final @NotNull List<Map.Entry<String, Combinator>> kvs
    ) {
        for (Map.Entry<String, Combinator> kv : kvs) {
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

    /**
     * Options for deciding what to do when a production is added that already exists. This enum is specifically used in {@link Grammar#fromProductions(List, RedefinitionOption)}.
     */
    public enum RedefinitionOption {
        /**
         * Ignore existing. Replace and forget.
         * <p>
         * Example: Adding Grammar productions "S = A" and "S = "B" results in "S = B" and discards the first.
         */
        OVERRIDE,
        /**
         * Throw exception if a duplicate is added.
         * <p>
         * Example: Adding Grammar productions "S = A" and "S = "B" results in an error.
         */
        ERROR,
        /**
         * Throw exception if a duplicate is added.
         * <p>
         * Example: Adding Grammar productions "S = A" and "S = "B" creates a new production "S = A | B".
         */
        CHOICE,
        /**
         * Keep old value.
         * <p>
         * Example: Adding Grammar productions "S = A" and "S = "B" keeps "S = A".
         */
        KEEP;

        final static RedefinitionOption defaultOption = OVERRIDE;
    }

    /**
     * Creates a new Grammar from productions. The productions are represented as a list of {@link Map.Entry} instances or any other pair-like type. The keys are the left-hand sides, the values are the right-hand sides.
     *
     * @param kvs The productions.
     * @return A new Grammar.
     */
    public static @NotNull Grammar fromProductions(
            final @NotNull List<Map.Entry<String, Combinator>> kvs,
            @Nullable Grammar.RedefinitionOption redefinitionOption) {
        final @NotNull SequencedMap<String, Combinator> m = new LinkedHashMap<>();

        if (redefinitionOption == null)
            redefinitionOption = RedefinitionOption.defaultOption;

        // Using an assignment here is not strictly necessary. I use it to force the switch to be exhaustive by default.
        @SuppressWarnings("unused")
        var usedOpt = switch (redefinitionOption) {
            case RedefinitionOption.OVERRIDE -> { // Ignore existing
                for (Map.Entry<String, Combinator> kv : kvs)
                    m.put(kv.getKey(), kv.getValue());
                yield RedefinitionOption.OVERRIDE;
            }
            case RedefinitionOption.ERROR -> { // Throw if any production exists with this name
                for (Map.Entry<String, Combinator> kv : kvs)
                    if (m.putIfAbsent(kv.getKey(), kv.getValue()) != null)
                        throw new IllegalArgumentException("Duplicate production: " + kv.getKey());
                yield RedefinitionOption.ERROR;
            }
            case RedefinitionOption.CHOICE -> { // Make a choice combinator if exists
                addProductionWithRedefinitionOptionChoice(m, kvs);
                yield RedefinitionOption.CHOICE;
            }
            case RedefinitionOption.KEEP -> {
                for (Map.Entry<String, Combinator> kv : kvs)
                    m.putIfAbsent(kv.getKey(), kv.getValue());
                yield RedefinitionOption.KEEP;
            }
        };

        return new Grammar(m);
    }

    /**
     * Gets the right-hand side of the production associated with the key.
     *
     * @param key The right-hand side of the production.
     * @return The production or null if not found.
     */
    public @Nullable Combinator getProduction(final @NotNull String key) {
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
     * Replaces all combinators that have the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#INITIAL} (the default when created) with combinators with the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#TAGGED_PARSE_TREE}.
     *
     * @return A new grammar.
     */
    public @NotNull Grammar applyStandardReductions(final @NotNull CombinatorFactory cf) {
        final LinkedHashMap<String, Combinator> m = new LinkedHashMap<>();
        this.forEach((prodKey, pars) -> {
            if (pars.getReduction().getReductionType() == ReductionType.ReductionTypesAvailable.INITIAL) {
                pars = cf.buffer(pars.withReduction(ReductionType.defaultNonRawReduction(prodKey)));
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
        return new GrammarInfo(
                keySet(),
                listNonTerminals().stream().map(NonTerminalCombinator::getKeyword).collect(Collectors.toSet()));
    }

    /**
     * Holds some information about a grammar.
     *
     * @param definedNTs All non-terminals of the Grammar.
     * @param usedNTs    All non-terminals which appear on the right-hand side of any production.
     */
    public record GrammarInfo(@NotNull Collection<@NotNull String> definedNTs,
                              @NotNull Collection<@NotNull String> usedNTs) {
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
        public @NotNull Collection<String> getUnusedNTs() {
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
        public @NotNull Collection<String> getUndefinedUsedNTs() {
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
            if (!(o instanceof GrammarInfo(Collection<String> nTs, Collection<String> ts))) return false;
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

    @Override
    public Grammar reversed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<String, Combinator> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<String, Combinator> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putFirst(String k, Combinator v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putLast(String k, Combinator v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator put(String key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends @NotNull String, ? extends Combinator> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super @NotNull String, ? super Combinator, ? extends Combinator> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator putIfAbsent(String key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(String key, Combinator oldValue, Combinator newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator replace(String key, Combinator value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator computeIfAbsent(@NotNull String key, Function<? super @NotNull String, ? extends Combinator> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator computeIfPresent(String key, BiFunction<? super String, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator compute(String key, BiFunction<? super String, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Combinator merge(String key, Combinator value, BiFunction<? super Combinator, ? super Combinator, ? extends Combinator> remappingFunction) {
        throw new UnsupportedOperationException();
    }
}
