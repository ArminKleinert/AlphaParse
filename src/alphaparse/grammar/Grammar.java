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
public final class Grammar extends LinkedHashMap<@NotNull Sym, Rule> {
    /**
     * Staring symbol of this grammar. Can be overridden when the Grammar is used in a parser.
     */
    private final @Nullable Sym startSym;

    /**
     * Create a new instance.
     * @param startSym Starting production key.
     * @param m Map of productions. This should be an ordered {@link SequencedMap}.
     */
    public Grammar(final @Nullable Sym startSym, final @NotNull Map<? extends Sym, ? extends Rule> m) {
        super(m);
        this.startSym = startSym;
    }

    /**
     * Creates a new instance from a {@link Map}.
     *
     * @param m Map of productions. This should be an ordered {@link SequencedMap}.
     */
    public Grammar(final @NotNull Map<? extends Sym, ? extends Rule> m) {
        this(m.isEmpty() ? null : m.entrySet().stream().findFirst().get().getKey(), m);
    }

    /**
     * Creates a new instance from a {@link Map}.
     *
     * @param m Map of productions. This should be an ordered {@link SequencedMap}.
     */
    public Grammar(final @NotNull SequencedMap<? extends Sym, ? extends Rule> m) {
        this(m.firstEntry().getKey(), m);
    }

    /**
     * Return the starting production. This might be null.
     * @return The starting production key.
     */
    public @Nullable Sym getStartSym() {
        return startSym;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grammar g)) return false;

        if (g.size() != size())
            return false;

        for (Map.Entry<@NotNull Sym, Rule> symRuleEntry : sequencedEntrySet()) {
            if (!Objects.equals(
                    g.getProduction(symRuleEntry.getKey()),
                    symRuleEntry.getValue())) {
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
    public @Nullable Rule getProduction(final @NotNull Sym key) {
        return getOrDefault(key, null);
    }

    @Override
    public Rule get(Object key) {
        throw new UnsupportedOperationException("Please use getProduction(key) instead of get(key).");
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Replaces all rules that have the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#INITIAL} (the default when created) with rules with the reduction type {@link alphaparse.reduction.ReductionType.ReductionTypesAvailable#TAGGED_PARSE_TREE}.
     *
     * @return A new grammar.
     */
    public @NotNull Grammar applyStandardReductions() {
        final LinkedHashMap<Sym, Rule> m = new LinkedHashMap<>();
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
            final @NotNull Set<NonTerminal> result = new HashSet<>();
            final @NotNull Set<Rule> analyzedRules = new HashSet<>();
            final @NotNull ArrayList<@NotNull Rule> ruleStack = new ArrayList<>(grammar.values());
            @NotNull Rule parser;

            while (!ruleStack.isEmpty()) {
                parser = ruleStack.removeLast();
                if (analyzedRules.contains(parser)) {
                    continue;
                }
                analyzedRules.add(parser);

                switch (parser) {
                    case NonTerminal nonTerminal -> result.add(nonTerminal);
                    case Terminal ignored -> {
                    }
                    case RuleWithManyChildren ruleWithManyChildren ->
                            ruleStack.addAll(ruleWithManyChildren.getRules());
                    case RuleWithChild ruleWithChild -> ruleStack.add(ruleWithChild.getRule());
                    case SpecialSequenceRule ignored -> {
                    }
                }
            }

            return result.stream().map(NonTerminal::getKeyword).collect(Collectors.toSet());
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
        public @NotNull Collection<Rule> collectRules(@Nullable Predicate<Rule> predicate) {
            if (predicate == null) predicate = (it) -> true;
            var result = new LinkedHashSet<Rule>();

            List<Rule> stack = new ArrayList<>(grammar.values());
            while (!stack.isEmpty()) {
                @NotNull Rule top = stack.removeLast();
                if (top instanceof RuleWithChild topC) stack.add((topC).getRule());
                else if (top instanceof RuleWithManyChildren topC) stack.addAll(topC.getRules());

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
    public Map.Entry<Sym, Rule> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Sym, Rule> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule putFirst(Sym k, Rule v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule putLast(Sym k, Rule v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule put(Sym key, Rule value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends @NotNull Sym, ? extends Rule> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super @NotNull Sym, ? super Rule, ? extends Rule> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule putIfAbsent(Sym key, Rule value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(Sym key, Rule oldValue, Rule newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule replace(Sym key, Rule value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule computeIfAbsent(@NotNull Sym key, Function<? super @NotNull Sym, ? extends Rule> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule computeIfPresent(Sym key, BiFunction<? super Sym, ? super Rule, ? extends Rule> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule compute(Sym key, BiFunction<? super Sym, ? super Rule, ? extends Rule> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rule merge(Sym key, Rule value, BiFunction<? super Rule, ? super Rule, ? extends Rule> remappingFunction) {
        throw new UnsupportedOperationException();
    }
}
