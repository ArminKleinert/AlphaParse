package alphaparse.parsing;

import alphaparse.collections.FlatSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 * This class represents a concatenation of productions, written as {@code p1 p2 p3 ...} (where p1, p2, etc. are instances of {@link Rule}).
 * When parsing, it tries to match p1, then p2, then p3 and so on.
 */
public final class ConcatRule extends RuleWithManyChildren {
    private ConcatRule(final boolean hide,
                       final @NotNull ReductionType red,
                       final @NotNull List<Rule> parsers) {
        super(hide, red, parsers);
    }

    /**
     * Create a new instance. Depending on the implementation, allows for buffering or create a different type of rule.
     *
     * @param rules The wrapped rules.
     * @return A rule.
     */
    public static @NotNull Rule create(final @NotNull List<Rule> rules) {
        if (rules.isEmpty())
            return EpsilonTerm.getDefault();
        if (rules.size() == 1)
            return rules.getFirst();

        var compressedResult = new ArrayList<Rule>();
        var simpleBuffered = new HashMap<SimpleRule, SimpleRule>();

        for (@NotNull Rule rule : rules) {
            if (rule instanceof ConcatRule cc) {
                compressedResult.addAll(cc.getRules());
            } else {
                if (rule instanceof SimpleRule sr) {
                    var temp = simpleBuffered.get(sr);

                    if (temp == null) simpleBuffered.put(sr, sr);
                    else rule = temp;
                }
                compressedResult.add(rule);
            }
        }

        return new ConcatRule(defaultHidden, defaultReductionType, compressedResult);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull List<@NotNull Rule> parsers = getRules();
        runner.pushListener(
                new TrampolineListenerKey(index, parsers.getFirst()),
                catListener(FlatSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), runner));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull List<@NotNull Rule> parsers = getRules();
        runner.pushListener(
                new TrampolineListenerKey(index, parsers.getFirst()),
                catFullListener(FlatSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), runner));
    }

    private @NotNull Listener catListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                          final @NotNull List<Rule> parserSequence,
                                          final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                          final @NotNull Gll runner) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.index();
            final @NotNull FlatSeq<Object> newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (parserSequence.isEmpty()) {
                runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
            } else {
                runner.pushListener(
                        new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catListener(
                                newResultsSoFar,
                                parserSequence.subList(1, parserSequence.size()),
                                nodeKey,
                                runner)
                );
            }
        };
    }

    private @NotNull Listener catFullListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                              final @NotNull List<Rule> parserSequence,
                                              final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                              final @NotNull Gll runner) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.index();
            final @NotNull var newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (parserSequence.size() == 1) {
                runner.pushFullListener(
                        new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, List.of(), nodeKey, runner));
            } else if (!parserSequence.isEmpty()) {
                runner.pushListener(
                        new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, parserSequence.subList(1, parserSequence.size()), nodeKey, runner));
            } else {
                runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
            }
        };
    }

    @Override
    public @NotNull Rule withHideTag(boolean hide) {
        return isHidden() == hide ? this : new ConcatRule(hide, getReduction(), getRules());
    }

    @Override
    public @NotNull Rule withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new ConcatRule(isHidden(), red, getRules());
    }

    @Override
    public @NotNull ConcatRule withRules(@NotNull List<@NotNull Rule> rules) {
        return new ConcatRule(isHidden(), getReduction(), rules);
    }
}
