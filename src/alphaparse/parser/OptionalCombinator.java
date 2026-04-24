package alphaparse.parser;

import alphaparse.Keyword;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonOptional;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This class represents the {@code [p]} or {@code p?} operator (where p is an instance of {@link Combinator}).
 * When parsing, the parser contained herein is optional (run zero times or once).
 */
public final class OptionalCombinator extends CombinatorWithParser {
    private OptionalCombinator(final boolean hide, final @NotNull ReductionType red, final @NotNull Combinator parser) {
        super(hide, red, parser);
    }

    /**
     * Creates a new instance.
     *
     * @param parser The parser.
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
        runner.success(nodeKeyForOpt, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator parser = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey thisNodeKey = new TrampolineListenerKey(index, this);
        runner.pushFullListener(new TrampolineListenerKey(index, parser), runner.nodeListener(thisNodeKey));
        if (index == runner.tramp().getText().length()) {
            runner.success(thisNodeKey, null, index);
        } else {
            runner.fail(thisNodeKey, index, new ParseFailureReasonOptional(Keyword.intern("end-of-string")));
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

//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof OptionalCombinator that)) return false;
//        if (this==that ) return true;
//        return hide() == that.hide() && Objects.equals(red(), that.red()) && Objects.equals(parser(),that.parser());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(hide(), red(),parser());
//    }
}
