package alphaparse.parser.combinator;

import alphaparse.Gll;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNodeKey;
import alphaparse.trampoline.InstaTramp;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class OrderedCombinator extends Combinator {
    private long bufferedHashCode = Long.MIN_VALUE;
    private final @NotNull Combinator parser1;
    private final @NotNull Combinator parser2;

    public OrderedCombinator(final @NotNull Combinator parser1,
                             final @NotNull Combinator parser2) {
        super();
        this.parser1 = parser1;
        this.parser2 = parser2;
    }

    public OrderedCombinator(final @NotNull Combinator parser1,
                             final @NotNull Combinator parser2,
                             final boolean hide,
                             final @NotNull ReductionType red) {
        super(hide, red);
        this.parser1 = parser1;
        this.parser2 = parser2;
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator1 = getParser1();
        final @NotNull Combinator combinator2 = getParser2();
        final @NotNull TrampolineListenerNodeKey nodeKeyForComb1 = new TrampolineListenerNodeKey(index, combinator1);
        final @NotNull TrampolineListenerNodeKey nodeKeyForComb2 = new TrampolineListenerNodeKey(index, combinator2);
        final @NotNull Listener listener = GllParserListeners.nodeListener(new TrampolineListenerNodeKey(index, this), tramp);
        Gll.pushListener(tramp, nodeKeyForComb1, listener);
        Gll.pushNegativeListener(tramp, nodeKeyForComb1,
                () -> Gll.pushListener(tramp, nodeKeyForComb2, listener));
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator1 = getParser1();
        final @NotNull Combinator combinator2 = getParser2();
        final @NotNull TrampolineListenerNodeKey nodeKeyForComb1 = new TrampolineListenerNodeKey(index, combinator1);
        final @NotNull TrampolineListenerNodeKey nodeKeyForComb2 = new TrampolineListenerNodeKey(index, combinator2);
        final @NotNull Listener listener = GllParserListeners.nodeListener(new TrampolineListenerNodeKey(index, this), tramp);
        Gll.pushFullListener(tramp, nodeKeyForComb1, listener);
        Gll.pushNegativeListener(tramp, nodeKeyForComb1, () -> Gll.pushFullListener(tramp, nodeKeyForComb2, listener));
    }

    public @NotNull Combinator getParser1() {
        return parser1;
    }

    public @NotNull Combinator getParser2() {
        return parser2;
    }

    @Override
    public @NotNull OrderedCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new OrderedCombinator(getParser1(), getParser2(), hide1, this.getReduction());
    }

    @Override
    public @NotNull OrderedCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new OrderedCombinator(getParser1(), getParser2(), isHidden(), red1);
    }

    public @NotNull OrderedCombinator withParsers(final @NotNull Combinator parser1, final @NotNull Combinator parser2) {
        return Objects.equals(this.parser1, parser1) && Objects.equals(this.parser2, parser2)
                ? this
                : new OrderedCombinator(parser1, parser2, isHidden(), getReduction());
    }

    public @NotNull Combinator unhideContent() {
        return withHideTag(false).withParsers(parser1.unhideContent(), parser2.unhideContent());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderedCombinator that)) return false;
        return bufferedHashCode == that.bufferedHashCode &&
                Objects.equals(getParser1(), that.getParser1()) &&
                Objects.equals(getParser2(), that.getParser2());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), parser1, parser2);
        return (int) bufferedHashCode;
    }
}