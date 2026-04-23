package alphaparse.parser;

import alphaparse.Keyword;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * TODO
 */
public final class NonTerminal extends Combinator {
    private final @NotNull Keyword keyword;

    /**
     * TODO
     *
     * @param keyword TODO
     */
    public NonTerminal(final @NotNull Keyword keyword) {
        super();
        this.keyword = keyword;
    }

    private NonTerminal(final @NotNull Keyword keyword, final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
        this.keyword = keyword;
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = runner.tramp().getGrammar().getOrMakeNonTerm(this.getKeyword());
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                runner.nodeListener(new TrampolineListenerKey(index, this))
        );
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = runner.tramp().getGrammar().getOrMakeNonTerm(this.getKeyword());
        runner.pushFullListener(
                new TrampolineListenerKey(index, combinator),
                runner.nodeListener(new TrampolineListenerKey(index, this)));
    }

    /**
     * TODO
     *
     * @return TODO
     */
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