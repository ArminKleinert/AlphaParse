package alphaparse.parser;

import alphaparse.Keyword;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

public record NonTerminalCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Keyword keyword) implements Combinator {
    public NonTerminalCombinator(final @NotNull Keyword keyword) {
        this(defaultHidden, defaultRed, keyword);
    }

    public Keyword getKeyword() {
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
    public @NotNull NonTerminalCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new NonTerminalCombinator(hide, red, keyword);
    }

    @Override
    public @NotNull NonTerminalCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new NonTerminalCombinator(hide, red, keyword);
    }
}
