package alphaparse.parser;

import alphaparse.flat.FlatSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * A class representing the right-hand sides of productions.
 */
public abstract sealed class Combinator
        permits CombinatorTerminal, NonTerminalCombinator, CombinatorWithManyParsers, CombinatorWithParser {
    /**
     * Default value for {@link Combinator#isHidden()}.
     */
    protected static final boolean defaultHidden = false;

    protected final boolean hide;
    protected final @NotNull ReductionType red;

    protected Combinator(final boolean hide, final @NotNull ReductionType red) {
        this.hide = hide;
        this.red = red;
    }

    protected Combinator() {
        this(defaultHidden, ReductionType.standardInitialReduction());
    }

    /**
     * Runs the parser from the provided index. The text is in the arguments.
     * <p>
     * Results (successes and failures) are saved using {@link Gll#pushSuccessMessage(TrampolineListenerNode.TrampolineListenerKey, FlatSeq, int)} or {@link Gll#fail(TrampolineListenerNode.TrampolineListenerKey, int, ParseFailureReason)} or some similar function.
     *
     * @param index  The start index.
     * @param runner Helper structure.
     */
    public abstract void parse(final int index, final @NotNull Gll runner);

    /**
     * Runs the parser from the provided index. The text is in the arguments. Unlike {@link Combinator#parse(int, Gll)}, this method tries to parse the text from the index until the end. If the string can't be matched to the end, results in a failure.
     * <p>
     * Results (successes and failures) are saved using {@link Gll#pushSuccessMessage(TrampolineListenerNode.TrampolineListenerKey, String, int)} or {@link Gll#fail(TrampolineListenerNode.TrampolineListenerKey, int, ParseFailureReason)} or some similar function.
     *
     * @param index  The start index.
     * @param runner Helper structure.
     */
    public abstract void fullParse(final int index, final @NotNull Gll runner);

    /**
     * Hides or unhides content in output.
     *
     * @param hide Whether to hide the content.
     * @return An instance of the same class with the hide tag set to the parameter.
     */
    public abstract @NotNull Combinator withHideTag(final boolean hide);

    /**
     * Creates an instance of this class with the reduction type set.
     *
     * @param red The reduction type.
     * @return An instance of the same class with the reduction type set to the parameter.
     */
    public abstract @NotNull Combinator withReduction(final @NotNull ReductionType red);

    /**
     * Check whether the content is hidden in the output.
     *
     * @return true if the content is hidden in the output, false otherwise.
     */
    public boolean isHidden() {
        return hide;
    }

    /**
     * Get the used reduction type.
     *
     * @return The current reduction type.
     */
    public @NotNull ReductionType getReduction() {
        return red;
    }

    /**
     * Hide content in output.
     *
     * @return An instance of the same class with the hide tag set to true.
     * @see #withHideTag(boolean)
     */
    public @NotNull Combinator enableHideTag() {
        return withHideTag(true);
    }

    /**
     * Unhide content in output.
     *
     * @return An instance of the same class with the hide tag set to false.
     * @see #withHideTag(boolean)
     */
    public @NotNull Combinator unhideContent() {
        return withHideTag(false);
    }

    /**
     * Hide the tag associated with this rule.
     * Wrap this combinator around the entire right-hand side.
     *
     * @return A new instance of the same class.
     */
    public @NotNull Combinator hideTag() {
        return withReduction(ReductionType.standardIntermediateReduction());
    }

    // Force children to override this.
    @Override
    public abstract boolean equals(Object o);

    // Force children to override this.
    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        return alphaparse.Print.combinatorToString(this);
    }
}
