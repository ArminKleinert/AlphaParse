package instarun.parser.combinator;

import instarun.Keyword;
import instarun.Gll;
import instarun.trampoline.InstaNodeKey;
import instarun.trampoline.InstaTramp;
import instarun.reduction.ReductionType;
import instarun.result.failure.failureReason.InstaFailureReasonLookahead;
import org.jetbrains.annotations.NotNull;

public final class LookaheadCombinator extends CombinatorWithParser {
    public LookaheadCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    public LookaheadCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator = getParser();
        Gll.pushListener(tramp, new InstaNodeKey(index, combinator),
                GllParserListeners.lookListener(new InstaNodeKey(index, this), tramp));
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        if (index == tramp.getText().length()) {
            parse(index, tramp);
        } else {
            Gll.fail(
                    tramp,
                    new InstaNodeKey(index, this),
                    index,
                    new InstaFailureReasonLookahead(Keyword.intern("end-of-string")));
        }
    }

    @Override
    public @NotNull LookaheadCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new LookaheadCombinator(getParser(), hide1, getReduction());
    }

    @Override
    public @NotNull LookaheadCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new LookaheadCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull LookaheadCombinator withParser(final @NotNull Combinator parser) {
        return new LookaheadCombinator(parser, isHidden(), getReduction());
    }
}
