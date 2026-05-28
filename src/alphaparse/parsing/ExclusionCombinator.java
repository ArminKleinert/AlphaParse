package alphaparse.parsing;

import alphaparse.Sym;
import alphaparse.grammar.Grammar;
import alphaparse.parsing.combinator_factory.CombinatorFactory;
import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.failure.ParseFailureReason;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 * Describes the "Syntactic exception" or "except" operator. That is the {@code (p1 - p2)} operator in EBNF (where p1 and p2 are instances of {@link Combinator}).
 * <p>
 * Notation: {@code rule1 - rule2}  (match rule1 except if it also matches rule2)
 * <p>
 * Example
 * <pre>
 * {@code
 *         // Accepts the language {"a", "b", "ab"}
 *         var p = Alpha.parser("S := #'[0-9]+' - '11'"); // Any positive number except 11.
 *         println(p.parse("12"));  // [:S, 12]
 *         println(p.parse("11"));  // Failure
 * }
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

    /**
     * Standard constructor. Represents {@code (parserExpected - parserExcluded)}.
     *
     * @param parserExpected The rule that must be matched.
     * @param parserExcluded The rule that must not be matched.
     */
    public ExclusionCombinator(final @NotNull Combinator parserExpected,
                               final @NotNull Combinator parserExcluded) {
        this(defaultHidden, ReductionType.standardInitialReduction(), parserExpected, parserExcluded);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull TrampolineListenerKey nodeKeyForExpect = new TrampolineListenerKey(index, parserExpected);

        runner.pushListener(
                nodeKeyForExpect,
                expectedSuccess -> {
                    var substring = runner.tramp().getText().substring(index, expectedSuccess.index());

                    if (reparsePart(substring, runner).isSuccess()) {
                        runner.fail(nodeKeyForThis, index, ParseFailureReason.ofExclusion(this, false));
                        return;
                    }

                    runner.pushSuccessAgainWithNewKey(nodeKeyForThis, expectedSuccess);
                });
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull TrampolineListenerKey nodeKeyForExpect = new TrampolineListenerKey(index, parserExpected);

        runner.pushListener(
                nodeKeyForExpect,
                expectedSuccess -> {
                    var substring = runner.tramp().getText().substring(index, expectedSuccess.index());

                    if (reparsePart(substring, runner).isSuccess()) {
                        runner.fail(nodeKeyForThis, index, ParseFailureReason.ofExclusion(this, true));
                        return;
                    }

                    runner.pushSuccessAgainWithNewKey(nodeKeyForThis, expectedSuccess);
                });
    }

    private @NotNull AlphaParseResult reparsePart(final @NotNull String subs, final @NotNull Gll runner) {
        final @NotNull var oldGrammar = runner.tramp().getGrammar();
        final @NotNull Sym startSymbol;
        final @NotNull Grammar grammar;

        if (parserExcluded instanceof NonTerminalCombinator) {
            // The rule was already a non-terminal. Simply change starting production.
            startSymbol = ((NonTerminalCombinator) parserExcluded).getKeyword();
            grammar = oldGrammar;
        } else {
            // Find a new start production name which is not in the grammar yet.
            Sym tempSym;
            do {
                tempSym = Sym.sym("RerunForExclusion" + new Random().nextInt(oldGrammar.size() + 1));
            } while (oldGrammar.containsKey(tempSym));
            startSymbol = tempSym;

            // Make new Grammar
            var tempG = new LinkedHashMap<>(oldGrammar);
            tempG.put(startSymbol, parserExcluded);
            grammar = new Grammar(tempG).applyStandardReductions(new CombinatorFactory(true));
        }
        return Gll.parse(grammar, startSymbol, subs, false, false);
    }

    /**
     * The rule that is expected. E.g. in the exclusion rule {@code (A - B)}, it would be {@code A}.
     *
     * @return The expected rule.
     */
    public @NotNull Combinator getParserExpected() {
        return parserExpected;
    }

    /**
     * The rule that is to be excluded. E.g. in the exclusion rule {@code (A - B)}, it would be {@code B}.
     *
     * @return The excluded rule.
     */
    public @NotNull Combinator getParserExcluded() {
        return parserExcluded;
    }

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
