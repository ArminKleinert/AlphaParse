package alphaparse.parser;

import alphaparse.Keyword;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * TODO
 */
public final class NonTerminalCombinator extends Combinator {
    private final @NotNull Keyword keyword;

    private NonTerminalCombinator(final boolean hide, final @NotNull ReductionType red, final @NotNull Keyword keyword) {
        super(hide, red);
        this.keyword = keyword;
    }

    /**
     * TODO
     *
     * @param keyword TODO
     */
    public NonTerminalCombinator(final @NotNull Keyword keyword) {
        super();
        this.keyword = keyword;
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

    @Override
    public @NotNull NonTerminalCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new NonTerminalCombinator(hide, red, keyword);
    }

    @Override
    public @NotNull NonTerminalCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new NonTerminalCombinator(hide, red, keyword);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NonTerminalCombinator that)) return false;
        if (this == that) return true;
        return hide == that.hide && Objects.equals(red, that.red) && Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hide, red, keyword);
    }
}
