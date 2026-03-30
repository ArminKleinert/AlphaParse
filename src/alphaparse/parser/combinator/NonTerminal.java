package alphaparse.parser.combinator;

import alphaparse.Keyword;
import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NonTerminal extends Combinator {
    private final @NotNull Keyword keyword;

    public NonTerminal(final @NotNull Keyword keyword) {
        super();
        this.keyword = keyword;
    }

    public NonTerminal(final @NotNull Keyword keyword, final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
        this.keyword = keyword;
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator = tramp.getGrammar().getOrMakeNonTerm(this.getKeyword());
        Gll.pushListener(
                tramp,
                new TrampolineListenerKey(index, combinator),
                GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp)
        );
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator = tramp.getGrammar().getOrMakeNonTerm(this.getKeyword());
        Gll.pushFullListener(
                tramp,
                new TrampolineListenerKey(index, combinator),
                GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp));
    }

    public @NotNull Keyword getKeyword() {
        return keyword;
    }

    @Override
    public @NotNull NonTerminal withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new NonTerminal(getKeyword(), hide1, this.getReduction());
    }

    @Override
    public @NotNull NonTerminal withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new NonTerminal(getKeyword(), isHidden(), red1);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NonTerminal that)) return false;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        return Objects.equals(getKeyword(), that.getKeyword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getReduction(), isHidden(), keyword);
    }
}