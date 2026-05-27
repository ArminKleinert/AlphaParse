package alphaparse.parsing;

import alphaparse.parsing.combinator_factory.CombinatorFactory;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 * Represents a special sequence. The typical EBNF-notation is {@code ?...?} where {@code ...} stands for some free-form text.
 * For example, {@code S = ?any whitespace except newline?} means what it says. This allows some interaction with the program around the parser.
 * <p>
 * You should not use this class unless it is very important!
 */
public final class TerminalSpecialSequenceCombinator extends CombinatorTerminal {
    private final @NotNull String description;
    private final @NotNull Function<@NotNull String, Optional<String>> function;

    private TerminalSpecialSequenceCombinator(final boolean hide,
                                              final @NotNull ReductionType red,
                                              final @NotNull String description,
                                              final @NotNull Function<@NotNull String, Optional<String>> function) {
        super(hide, red);
        this.function = function;
        this.description = description;
    }

    /**
     * Creates a new instance. Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param description The description of the special sequence.
     * @param function    The function which does what the description says.
     * @see CombinatorFactory#specialSequence(String, Function)
     */
    public TerminalSpecialSequenceCombinator(
            final @NotNull String description,
            final @NotNull Function<@NotNull String, Optional<String>> function) {
        super();
        this.function = function;
        this.description = description;
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull String text = runner.tramp().getText().substring(index);
        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        var res = function.apply(text);

        if (res.isEmpty()) {
            runner.fail(nodeKey, index, ParseFailureReason.ofSpecialSequence(this, false));
            return;
        }

        runner.pushSuccessMessage(nodeKey, res.get(), index + res.get().length());
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull String text = runner.tramp().getText().substring(index);
        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        var res = function.apply(text);

        if (res.isEmpty()) {
            runner.fail(nodeKey, index, ParseFailureReason.ofSpecialSequence(this, true));
            return;
        }

        runner.pushSuccessMessage(nodeKey, res.get(), index + res.get().length());
    }

    @Override
    public @NotNull TerminalSpecialSequenceCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new TerminalSpecialSequenceCombinator(hide, red, description, function);
    }

    @Override
    public @NotNull TerminalSpecialSequenceCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new TerminalSpecialSequenceCombinator(hide, red, description, function);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TerminalSpecialSequenceCombinator that)) return false;
        if (this == that) return true;
        return hide == that.hide
                && Objects.equals(red, that.red)
                && Objects.equals(function, that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hide, red, function);
    }

    @Override
    public String toString() {
        return description;
    }
}
