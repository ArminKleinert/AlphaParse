package alphaparse.grammar;

import alphaparse.Sym;
import alphaparse.parsing.*;
import alphaparse.parsing.combinator_factory.CombinatorFactory;
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
public final class Grammar extends LinkedHashMap<@NotNull Sym, Combinator> {
    /**
     * Creates a new instance from a {@link Map}.
     *
     * @param m The map.
     */
    public Grammar(final @NotNull Map<? extends Sym, ? extends Combinator> m) {
        super(m);
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

    private static void addProductionWithRedefinitionOptionChoice(
            final @NotNull SequencedMap<Sym, Combinator> m,
            final @NotNull List<Map.Entry<Sym, Combinator>> kvs
    ) {
        for (Map.Entry<Sym, Combinator> kv : kvs) {
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
     * Creates a new Grammar from productions. The productions are represented as a list of {@link Map.Entry} instances or any other pair-like type. The keys are the left-hand sides, the values are the right-hand sides.
     *
     * @param kvs                The productions.
     * @param redefinitionOption Option for what to do if a production appears more than once.
     * @return A new Grammar.
     * @see RedefinitionOption
     */
    public static @NotNull Grammar fromProductions(
            final @NotNull List<Map.Entry<Sym, Combinator>> kvs,
            @Nullable RedefinitionOption redefinitionOption) {
        final @NotNull SequencedMap<Sym, Combinator> m = new LinkedHashMap<>();

        if (redefinitionOption == null)
            redefinitionOption = RedefinitionOption.defaultOption;

        // Using an assignment here is not strictly necessary. I use it to force the switch to be exhaustive by default.
        @SuppressWarnings("unused")
        var usedOpt = switch (redefinitionOption) {
            case RedefinitionOption.OVERRIDE -> { // Ignore existing
                for (Map.Entry<Sym, Combinator> kv : kvs)
                    m.put(kv.getKey(), kv.getValue());
                yield RedefinitionOption.OVERRIDE;
            }
            case RedefinitionOption.ERROR -> { // Throw if any production exists with this name
                for (Map.Entry<Sym, Combinator> kv : kvs)
                    if (m.putIfAbsent(kv.getKey(), kv.getValue()) != null)
                        throw new IllegalArgumentException("Duplicate production: " + kv.getKey());
                yield RedefinitionOption.ERROR;
            }
            case RedefinitionOption.CHOICE -> { // Make a choice combinator if exists
                addProductionWithRedefinitionOptionChoice(m, kvs);
                yield RedefinitionOption.CHOICE;
            }
            case RedefinitionOption.KEEP -> {
                for (Map.Entry<Sym, Combinator> kv : kvs)
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
     * @param cf Used to buffer any created combinators.
     * @return A new grammar.
     */
    public @NotNull Grammar applyStandardReductions(final @NotNull CombinatorFactory cf) {
        final LinkedHashMap<Sym, Combinator> m = new LinkedHashMap<>();
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
    public record GrammarInfo(@NotNull Collection<@NotNull Sym> definedNTs,
                              @NotNull Collection<@NotNull Sym> usedNTs) {
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
        public @NotNull Collection<Sym> getUndefinedUsedNTs() {
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
            if (!(o instanceof GrammarInfo(Collection<Sym> nTs, Collection<Sym> ts))) return false;
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
