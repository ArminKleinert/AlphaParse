package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the {@code [p]} or {@code p?} operator (where p is an instance of {@link Combinator}).
 * When parsing, the parser contained herein is optional (run zero times or once).
 */
public final class OptionalCombinator extends CombinatorWithParser {
    private OptionalCombinator(final boolean hide, final @NotNull ReductionType red, final @NotNull Combinator parser) {
        super(hide, red, parser);
    }

    /**
     * Creates a new instance. Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param parser The parser.
     * @see CombinatorFactory#optionalCombinator(Combinator)
     */
    public OptionalCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForOpt = new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                runner.nodeListener(nodeKeyForOpt)
        );
        runner.successNull(nodeKeyForOpt, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator parser = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey thisNodeKey = new TrampolineListenerKey(index, this);
        runner.pushFullListener(new TrampolineListenerKey(index, parser), runner.nodeListener(thisNodeKey));
        if (index == runner.tramp().getText().length()) {
            runner.successNull(thisNodeKey, null, index);
        } else {
            runner.fail(thisNodeKey, index, ParseFailureReason.ofOptional(this, true));
        }
    }

    @Override
    public @NotNull OptionalCombinator withParser(final @NotNull Combinator parser) {
        return new OptionalCombinator(hide, red, parser);
    }

    @Override
    public @NotNull OptionalCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new OptionalCombinator(hide, red, parser);
    }

    @Override
    public @NotNull OptionalCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new OptionalCombinator(hide, red, parser);
    }
}
