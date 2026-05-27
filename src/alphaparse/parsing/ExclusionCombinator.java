package alphaparse.parsing;

import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 * Describes the "Syntactic exception" or "except" operator. That is the {@code (p1 - p2)} operator in EBNF (where p1 and p2 are instances of {@link Combinator}).
 * <p>
 * Notation: {@code rule1 - rule2}  (match rule1 except if it also matches rule2)
 * <p>
 * Example
 * <pre>
 *{@code
 *         // Accepts the language {"a", "b", "ab"}
 *         var p = Alpha.parser("S := #'[0-9]+' - '11'"); // Any positive number except 11.
 *         println(p.parse("12"));  // [:S, 12]
 *         println(p.parse("11"));  // Failure
 *}
 * </pre>
 * See also: <a href="https://www.iso.org/standard/26153.html">ISO/IEC 14977:1996</a> and <a href="https://stackoverflow.com/a/35138946">an explanation on StackOverflow</a>.
 */
public final class ExclusionCombinator extends CombinatorWithManyParsers {
    private final @NotNull Combinator parserExpected;
    private final @NotNull Combinator parserExcluded;

    private ExclusionCombinator(final boolean hide, final @NotNull ReductionType red,
                                final @NotNull Combinator parserExpected,
                                final @NotNull Combinator parserExcluded) {
        super(hide, red, List.of(parserExpected, parserExcluded));
        this.parserExpected = parserExpected;
        this.parserExcluded = parserExcluded;
    }

    private ExclusionCombinator(final @NotNull List<Combinator> parsers,
                                    final boolean hide,
                                    final @NotNull ReductionType red) {
        this(hide, red, parsers.get(0), parsers.get(1));
    }

    /**
     * Standard constructor. Represents {@code (parserExpected - parserExcluded)}.
     * @param parserExpected The rule that must be matched.
     * @param parserExcluded The rule that must not be matched.
     */
    public ExclusionCombinator(final @NotNull Combinator parserExpected,
                                    final @NotNull Combinator parserExcluded) {
        this(defaultHidden, ReductionType.standardInitialReduction(), parserExpected, parserExcluded);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, parserExpected);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, parserExcluded);
        final @NotNull Listener listener =
                runner.nodeListener(new TrampolineListenerKey(index, this));
        runner.pushListener(nodeKeyForComb1, listener);
        runner.pushNegativeListener(nodeKeyForComb1, () -> runner.pushListener(nodeKeyForComb2, listener));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, parserExpected);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, parserExcluded);
        final @NotNull Listener listener =
                runner.nodeListener(new TrampolineListenerKey(index, this));
        runner.pushFullListener(nodeKeyForComb1, listener);
        runner.pushNegativeListener(nodeKeyForComb1, () -> runner.pushFullListener(nodeKeyForComb2, listener));
    }

//    private @NotNull Listener exclusionListener(final @NotNull RepetitionCombinator notParser,
//                                          final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
//                                          final @NotNull Gll runner) {
//        return result -> {
//            final @Nullable Object parsedResult = result.getResult();
//            final int continueIndex = result.index();
//
//            final @NotNull FlatSeq<Object> newResultsSoFar = parsedResult instanceof FlatSeq<?>
//                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
//                    : resultsSoFar.append(parsedResult);
//
//            final int newNResultsSoFar = nResultsSoFar + 1;
//
//            if (Integer.max(parser.getMin(), 0) <= newNResultsSoFar && newNResultsSoFar <= parser.getMax())
//                runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
//
//            if (newNResultsSoFar < parser.getMax())
//                runner.pushListener(
//                        new TrampolineListenerKey(continueIndex, parser.getParser()),
//                        exclusionListener(newResultsSoFar, newNResultsSoFar, parser, nodeKey, runner)
//                );
//        };
//    }

    @Override
    public @NotNull ExclusionCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new ExclusionCombinator(hide, this.getReduction(), parserExpected, parserExcluded);
    }

    @Override
    public @NotNull ExclusionCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new ExclusionCombinator(isHidden(), red, parserExpected, parserExcluded);
    }

    @Override
    public @NotNull ExclusionCombinator withParsers(final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.size() != 2)
            throw new IllegalArgumentException("Must pass exactly 2 arguments.");
        return new ExclusionCombinator(isHidden(), getReduction(), parsers.getFirst(), parsers.getLast());
    }
}
