package alphaparse.parsing;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the {@code [p]} or {@code p?} operator (where p is an instance of {@link Rule}).
 * When parsing, the parser contained herein is optional (run zero times or once).
 */
public final class OptionalRule extends RuleWithChild {
    private OptionalRule(final boolean hide, final @NotNull ReductionType red, final @NotNull Rule parser) {
        super(hide, red, parser);
    }

    /**
     * Creates a new instance.
     *
     * @param parser The parser.
     */
    public OptionalRule(final @NotNull Rule parser) {
        super(parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForOpt = new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, rule),
                runner.nodeListener(nodeKeyForOpt)
        );
        runner.pushSuccessMessageWithoutValue(nodeKeyForOpt, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule parser = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey thisNodeKey = new TrampolineListenerKey(index, this);
        runner.pushFullListener(new TrampolineListenerKey(index, parser), runner.nodeListener(thisNodeKey));
        if (index == runner.tramp().getText().length()) {
            runner.pushSuccessMessageWithoutValue(thisNodeKey, index);
        } else {
            runner.fail(thisNodeKey, index, ParseFailureReason.ofOptional(this, true));
        }
    }

    @Override
    public @NotNull OptionalRule withParser(final @NotNull Rule parser) {
        return new OptionalRule(hide, red, parser);
    }

    @Override
    public @NotNull OptionalRule withHideTag(boolean hide) {
        return isHidden() == hide ? this : new OptionalRule(hide, red, parser);
    }

    @Override
    public @NotNull OptionalRule withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new OptionalRule(hide, red, parser);
    }
}
