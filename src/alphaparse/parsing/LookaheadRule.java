package alphaparse.parsing;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import org.jetbrains.annotations.NotNull;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 * This class represents the ABNF "lookahead" operator {@code &p} (where p is an instance of {@link Rule}).
 */
public final class LookaheadRule extends RuleWithChild {
    private LookaheadRule(final boolean hide, final @NotNull ReductionType red, final @NotNull Rule parser) {
        super(hide, red, parser);
    }

    /**
     * Creates a new instance.
     *
     * @param parser The inner parser.
     */
    public LookaheadRule(final @NotNull Rule parser) {
        super(parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule = getParser();
        final @NotNull var nodeKey = new TrampolineListenerKey(index, this);
        runner.pushListener(new TrampolineListenerKey(index, rule),
                ignored -> runner.pushSuccessMessageWithoutValue(nodeKey, index));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        if (index == runner.tramp().getText().length()) {
            parse(index, runner);
        } else {
            runner.fail(
                    new TrampolineListenerKey(index, this),
                    index,
                    ParseFailureReason.ofLookahead(this, false));
        }
    }

    @Override
    public @NotNull LookaheadRule withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new LookaheadRule(hide, red, parser);
    }

    @Override
    public @NotNull LookaheadRule withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new LookaheadRule(hide, red, parser);
    }

    @Override
    public @NotNull LookaheadRule withParser(final @NotNull Rule parser) {
        return new LookaheadRule(hide, red, parser);
    }
}
