package alphaparse.result;

import alphaparse.Keyword;
import alphaparse.parser.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * TODO
 */
public final class ParseConverterUtils {
    private ParseConverterUtils() {
    }

    /**
     * TODO
     *
     * @param pt TODO
     * @return TODO
     */
    public static @NotNull ParseTree parseTreeFromHiccup(final @NotNull List<?> pt) {
        if (pt.isEmpty()) throw new IllegalArgumentException();
        final @NotNull var tag = pt.getFirst();
        if (!(tag instanceof Keyword keyTag)) throw new IllegalArgumentException();
        final @NotNull var content = pt.stream().skip(1).map(ParseConverterUtils::parseTreeFromHiccup).toList();
        return ParseTree.create(new Node.NodeTreeTag(keyTag), content);
    }

    private static @NotNull Node parseTreeFromHiccup(final @Nullable Object pt) {
        if (pt instanceof List<?>) return new Node.NodeParseTree(parseTreeFromHiccup((List<?>) pt));
        return Node.of(pt);
    }

    /**
     * TODO
     *
     * @param m TODO
     * @return TODO
     */
    public static @NotNull ParseTree parseTreeFromEnlive(final @NotNull Map<?, ?> m) {
        if (!(m.size() == 2 &&
                m.containsKey(Keyword.intern("tag")) &&
                m.containsKey(Keyword.intern("content")))) {
            throw new IllegalArgumentException();
        }

        final var tag = m.get(Keyword.intern("tag"));
        final var content = m.get(Keyword.intern("content"));

        if (!(tag instanceof Keyword && content instanceof List<?>))
            throw new IllegalArgumentException();

        return ParseTree.create(
                new Node.NodeTreeTag((Keyword) tag),
                ((List<?>) content).stream().map(ParseConverterUtils::parseTreeFromEnlive).toList()
        );
    }

    private static @NotNull Node parseTreeFromEnlive(final Object pt) {
        if (pt instanceof Map<?, ?>) return new Node.NodeParseTree(parseTreeFromEnlive((Map<?, ?>) pt));
        return Node.of(pt);
    }

    private static @NotNull Map<Keyword, Object> mapFromTagAndContents(
            final @NotNull Combinator combinator,
            final @NotNull Function<Combinator, String> classTagLookup,
            final @NotNull Object... otherKeyAndValues) {
        final @NotNull String tag = classTagLookup.apply(combinator);
        final boolean isHidden = combinator.isHidden();
        final @NotNull ReductionType reductionType = combinator.getReduction();
        if ((otherKeyAndValues.length & 1) == 1)
            throw new IllegalArgumentException();
        final @NotNull Map<Keyword, Object> m = new HashMap<>();
        m.put(Keyword.intern("tag"), tag);
        if (isHidden)
            m.put(Keyword.intern("hide"), isHidden);
        if (!(Objects.equals(reductionType.getReductionType(), ReductionType.ReductionTypesAvailable.NONE)))
            m.put(Keyword.intern("red"), reductionType);
        for (int i = 0; i < otherKeyAndValues.length; i += 2) {
            if (!(otherKeyAndValues[i] instanceof Keyword))
                throw new IllegalArgumentException();
            m.put((Keyword) otherKeyAndValues[i], otherKeyAndValues[i + 1]);
        }
        return m;
    }

    private static @NotNull Map<Keyword, Object> parserToMap(
            final @NotNull Combinator combinator,
            final @NotNull Function<Combinator, String> classTagLookup) {
        return switch (combinator) {
            case CombinatorWithManyParsers c ->
                    mapFromTagAndContents(c, classTagLookup, Keyword.intern("parsers"), c.getParsers());
            case RepetitionCombinator c ->
                    mapFromTagAndContents(c, classTagLookup, Keyword.intern("parser"), c.getParser(),
                            Keyword.intern("min"), c.getMin(), Keyword.intern("max"), c.getMax());
            case CombinatorWithParser c ->
                    mapFromTagAndContents(c, classTagLookup, Keyword.intern("parser"), c.getParser());
            case EpsilonCombinator c -> mapFromTagAndContents(c, classTagLookup);
            case NonTerminal c -> mapFromTagAndContents(c, classTagLookup, Keyword.intern("keyword"), c.getKeyword());
            case RegexpTerminal c -> mapFromTagAndContents(c, classTagLookup, Keyword.intern("regexp"), c.getRegexp());
            case StringTerminal c -> mapFromTagAndContents(c, classTagLookup, Keyword.intern("string"), c.getString());
            case UnicodeCharTerminal c -> mapFromTagAndContents(c, classTagLookup,
                    Keyword.intern("lo"), c.getLo(), Keyword.intern("hi"), c.getHi());
        };
    }

    /**
     * TODO
     *
     * @param combinator TODO
     * @return TODO
     */
    public static @NotNull Map<Keyword, Object> parserToMap(final @NotNull Combinator combinator) {
        final @NotNull Function<Combinator, String> classTagLookup = (combinator1) ->
                switch (combinator1) {
                    case AlternationCombinator ignored -> "alt";
                    case CatCombinator ignored -> "cat";
                    case EpsilonCombinator ignored -> "eps";
                    case LookaheadCombinator ignored -> "look";
                    case NegateCombinator ignored -> "neg";
                    case NonTerminal ignored -> "nt";
                    case OptCombinator ignored -> "opt";
                    case OrderedCombinator ignored -> "ord";
                    case PlusCombinator ignored -> "plus";
                    case RegexpTerminal ignored -> "regex";
                    case RepetitionCombinator ignored -> "rep";
                    case StarCombinator ignored -> "star";
                    case StringTerminal st -> st.isCaseInsensitive() ? "string-ci" : "string";
                    case UnicodeCharTerminal ignored -> "char";
                };
        return parserToMap(combinator, classTagLookup);
    }

    /**
     * TODO
     *
     * @param parser TODO
     * @return TODO
     */
    public static @NotNull Map<Keyword, Object> parserToMap(final @NotNull Parser parser) {
        final @NotNull Function<Combinator, String> classTagLookup = (combinator1) ->
                switch (combinator1) {
                    case AlternationCombinator ignored -> "alt";
                    case CatCombinator ignored -> "cat";
                    case EpsilonCombinator ignored -> "eps";
                    case LookaheadCombinator ignored -> "look";
                    case NegateCombinator ignored -> "neg";
                    case NonTerminal ignored -> "nt";
                    case OptCombinator ignored -> "opt";
                    case OrderedCombinator ignored -> "ord";
                    case PlusCombinator ignored -> "plus";
                    case RegexpTerminal ignored -> "regex";
                    case RepetitionCombinator ignored -> "rep";
                    case StarCombinator ignored -> "star";
                    case StringTerminal st -> st.isCaseInsensitive() ? "string-ci" : "string";
                    case UnicodeCharTerminal ignored -> "char";
                };
        final @NotNull var start = parser.startProduction();
        final @NotNull var grammar = new LinkedHashMap<>();
        parser.grammar().entrySet().stream().map((kcEntry) ->
                new AbstractMap.SimpleImmutableEntry<>(kcEntry.getKey(), parserToMap(kcEntry.getValue(), classTagLookup))
        ).forEach(kcEntry ->
                grammar.put(kcEntry.getKey(), kcEntry.getValue())
        );
        return Map.of(Keyword.intern("start"), start, Keyword.intern("grammar"), grammar);
    }
}
