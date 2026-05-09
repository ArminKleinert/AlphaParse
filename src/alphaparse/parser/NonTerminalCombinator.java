package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * This type represents non-terminals.
 */
public final class NonTerminalCombinator extends Combinator {
    private final @NotNull String keyword;

    private NonTerminalCombinator(final boolean hide, final @NotNull ReductionType red, final @NotNull String keyword) {
        super(hide, red);
        this.keyword = keyword;
    }

    /**
     * Creates a new instance from a name. Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param keyword The name.
     * @see CombinatorFactory#makeNonTerminal(String)
     * @see CombinatorFactory#staticMakeNonTerminal(String)
     */
    public NonTerminalCombinator(final @NotNull String keyword) {
        super();
        this.keyword = keyword;
    }

    /**
     * Returns the name.
     *
     * @return The name.
     */
    public @NotNull String getKeyword() {
        return keyword;
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @Nullable Combinator combinator = runner.tramp().getGrammar().getProduction(this.getKeyword());
        if (combinator == null)
            throw new IllegalStateException("Cannot use non terminal. Should be checked when initializing parser.");
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                runner.nodeListener(new TrampolineListenerKey(index, this))
        );
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @Nullable Combinator combinator = runner.tramp().getGrammar().getProduction(this.getKeyword());
        if (combinator == null)
            throw new IllegalStateException("Cannot use non terminal. Should be checked when initializing parser.");
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
