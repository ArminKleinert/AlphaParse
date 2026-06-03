package alphaparse.parsing;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import org.jetbrains.annotations.NotNull;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.TrampolineListenerNode;

/**
 * Represents a negative lookahead. Written {@code !P}.
 * <p>
 * Example: The production {@code S := !'a' ('a'|'b')+} matches any string of 'a' and 'b' which does NOT start with 'a'.
 */
public final class NegativeLookaheadRule extends RuleWithChild {
    private NegativeLookaheadRule(final boolean hide,
                                  final @NotNull ReductionType red,
                                  final @NotNull Rule parser) {
        super(hide, red, parser);
    }

    /**
     * Creates a new instance.
     *
     * @param parser The thing to avoid.
     */
    public NegativeLookaheadRule(final @NotNull Rule parser) {
        super(parser);
    }

    private boolean resultExists_Q(
            final @NotNull Gll runner,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = runner.tramp().getNode(nodeKey);

        if (node == null)
            return false;

        return !node.fullResults().isEmpty() || !node.results().isEmpty();
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, rule);

        if (resultExists_Q(runner, nodeKey)) {
            runner.fail(new TrampolineListenerKey(index, this), index, ParseFailureReason.ofNegated(this, false));
            return;
        }

        runner.pushListener(nodeKey, ignored -> runner.fail(
                new TrampolineListenerKey(index, this), index,
                ParseFailureReason.ofNegated(this, false)));

        final @NotNull Rule p = this;
        runner.pushNegativeListener(nodeKey, () -> {
            if (!resultExists_Q(runner, nodeKey)) {
                runner.pushSuccessMessageWithoutValue(new TrampolineListenerKey(index, p), index);
            }
        });
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        parse(index, runner);
    }

    @Override
    public @NotNull NegativeLookaheadRule withParser(final @NotNull Rule parser) {
        return new NegativeLookaheadRule(hide, red, parser);
    }

    @Override
    public @NotNull NegativeLookaheadRule withHideTag(boolean hide) {
        return isHidden() == hide ? this : new NegativeLookaheadRule(hide, red, parser);
    }

    @Override
    public @NotNull NegativeLookaheadRule withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new NegativeLookaheadRule(hide, red, parser);
    }
}
