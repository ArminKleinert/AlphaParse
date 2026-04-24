package alphaparse.parser;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TODO
 */
public final class Grammar extends LinkedHashMap<@NotNull Keyword, Combinator> {

    /**
     * TODO
     *
     * @param m TODO
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
     * TODO
     *
     * @param kvs TODO
     * @return TODO
     */
    public static @NotNull Grammar fromProductions(final @NotNull List<Map.Entry<Keyword, Combinator>> kvs) {
        final @NotNull SequencedMap<Keyword, Combinator> m = new LinkedHashMap<>();
        for (Map.Entry<Keyword, Combinator> kv : kvs) {
            m.put(kv.getKey(), kv.getValue());
        }
        return new Grammar(m);
    }

    /**
     * TODO
     *
     * @param key TODO
     * @return TODO
     */
    public @Nullable Combinator getProduction(final @NotNull Keyword key) {
        return getOrDefault(key, null);
    }

    /**
     * TODO
     *
     * @param key TODO
     * @return TODO
     */
    public @NotNull Combinator getOrMakeNonTerm(final @NotNull Keyword key) {
        final @Nullable Combinator p = getProduction(key);
        if (p == null) return CombinatorFactory.staticMakeNonTerminal(key);
        return p;
    }

    @Override
    public Combinator get(Object key) {
        throw new UnsupportedOperationException("get " + key);
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
     * TODO
     *
     * @return TODO
     */
    public @NotNull GrammarInfo analyze() {
        return new GrammarInfo(
                keySet(),
                listNonTerminals().stream().map(NonTerminalCombinator::getKeyword).collect(Collectors.toSet()));
    }

    /**
     * TODO
     *
     * @param definedNTs TODO
     * @param usedNTs    TODO
     */
    public record GrammarInfo(@NotNull Collection<@NotNull Keyword> definedNTs,
                              @NotNull Collection<@NotNull Keyword> usedNTs) {
        /**
         * TODO
         *
         * @return TODO
         */
        public @NotNull Collection<Keyword> getUnusedNTs() {
            return definedNTs.stream().filter(it -> !usedNTs.contains(it)).collect(Collectors.toSet());
        }

        /**
         * TODO
         *
         * @return TODO
         */
        public @NotNull Collection<Keyword> getUndefinedUsedNTs() {
            return usedNTs.stream().filter(it -> !definedNTs.contains(it)).collect(Collectors.toSet());
        }

        /**
         * TODO
         *
         * @return TODO
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
     * TODO
     *
     * @param k TODO
     * @param v TODO
     * @return TODO
     */
    public static Map.Entry<Keyword, Combinator> entry(final @NotNull Keyword k, final @NotNull Combinator v) {
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
