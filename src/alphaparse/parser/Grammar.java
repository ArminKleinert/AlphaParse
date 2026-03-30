package alphaparse.parser;

import alphaparse.CombinatorsSource;
import alphaparse.Keyword;
import alphaparse.parser.combinator.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class Grammar extends LinkedHashMap<@NotNull Keyword, Combinator> {
    public Grammar(final @NotNull Map<? extends Keyword, ? extends Combinator> m) {
        super(m);
    }

    public Grammar() {
        super();
    }

    public static @NotNull Grammar fromProductions(final @NotNull Iterable<Map.Entry<Keyword, Combinator>> kvs) {
        final @NotNull Map<Keyword, Combinator> m = new HashMap<>();
        for (Map.Entry<Keyword, Combinator> kv : kvs) {
            m.put(kv.getKey(), kv.getValue());
        }
        return new Grammar(m);
    }

    public Combinator getProduction(final @NotNull Keyword key) {
        return getOrDefault(key, null);
    }

    public @NotNull Combinator getOrMakeNonTerm(final @NotNull Keyword key) {
        final @NotNull Combinator p = getProduction(key);
        if (p == null) return CombinatorsSource.staticMakeNonTerminal(key);
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


    private @NotNull Set<NonTerminal> listNonTerminals() {
        final @NotNull Set<NonTerminal> result = new HashSet<>();
        final @NotNull Set<Combinator> analyzedCombinators = new HashSet<>();
        final @NotNull ArrayList<@NotNull Combinator> combinatorStack = new ArrayList<>(values());
        @NotNull Combinator parser;

        do {
            parser = combinatorStack.removeLast();
            if (analyzedCombinators.contains(parser)) {
                continue;
            }
            analyzedCombinators.add(parser);

            switch (parser) {
                case NonTerminal nonTerminal -> result.add(nonTerminal);
                case CombinatorTerminal ignored -> {
                }
                case CombinatorWithManyParsers combinatorWithManyParsers -> {
                    combinatorStack.addAll(combinatorWithManyParsers.getParsers());
                }
                case CombinatorWithParser combinatorWithParser -> {
                    combinatorStack.add(combinatorWithParser.getParser());
                }
                case OrderedCombinator orderedCombinator -> {
                    combinatorStack.add(orderedCombinator.getParser1());
                    combinatorStack.add(orderedCombinator.getParser2());
                    // Tail-recursion
                }
                default -> {
                    throw new IllegalArgumentException("Unhandled parser class: " + parser.getClass() + " of parser " + parser);
                }
            }
        } while (!combinatorStack.isEmpty());

        return result;
    }

    public @NotNull GrammarInfo analyze() {
        return new GrammarInfo(
                keySet(),
                listNonTerminals().stream().map(NonTerminal::getKeyword).collect(Collectors.toSet()));
    }

    public record GrammarInfo(@NotNull Collection<@NotNull Keyword> definedNTs,
                              @NotNull Collection<@NotNull Keyword> usedNTs) {
        public @NotNull Collection<Keyword> getUnusedNTs() {
            return definedNTs.stream().filter(it -> !usedNTs.contains(it)).collect(Collectors.toSet());
        }

        public @NotNull Collection<Keyword> getUndefinedUsedNTs() {
            return usedNTs.stream().filter(it -> !definedNTs.contains(it)).collect(Collectors.toSet());
        }

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
}
