package alphaparse.parsing;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.Print;
import alphaparse.collections.FlatSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing the right-hand sides of productions.
 */
public abstract sealed class Rule
        permits RuleWithManyChildren, RuleWithChild, SimpleRule {
    /**
     * Default value for {@link Rule#isHidden()}.
     */
    protected static final boolean defaultHidden = false;

    /**
     * Default value for {@link Rule#getReduction()}.
     */
    protected static final ReductionType defaultReductionType = ReductionType.standardInitialReduction();

    protected final boolean hide;
    protected final @NotNull ReductionType red;

    protected Rule(final boolean hide, final @NotNull ReductionType red) {
        this.hide = hide;
        this.red = red;
    }

    /**
     * Runs the parser from the provided index. The text is in the arguments.
     * <p>
     * Results (successes and failures) are saved using {@link Gll#pushSuccessMessage(TrampolineListenerKey, FlatSeq, int)} or {@link Gll#fail(TrampolineListenerKey, int, ParseFailureReason)} or some similar function.
     *
     * @param index  The start index.
     * @param runner Helper structure.
     */
    public abstract void parse(final int index, final @NotNull Gll runner);

    /**
     * Runs the parser from the provided index. The text is in the arguments. Unlike {@link Rule#parse(int, Gll)}, this method tries to parse the text from the index until the end. If the string can't be matched to the end, results in a failure.
     * <p>
     * Results (successes and failures) are saved using {@link Gll#pushSuccessMessage(TrampolineListenerKey, String, int)} or {@link Gll#fail(TrampolineListenerKey, int, ParseFailureReason)} or some similar function.
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
    public abstract @NotNull Rule withHideTag(final boolean hide);

    /**
     * Creates an instance of this class with the reduction type set.
     *
     * @param red The reduction type.
     * @return An instance of the same class with the reduction type set to the parameter.
     */
    public abstract @NotNull Rule withReduction(final @NotNull ReductionType red);

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
    public @NotNull Rule enableHideTag() {
        return withHideTag(true);
    }

    /**
     * Unhide content in output.
     *
     * @return An instance of the same class with the hide tag set to false.
     * @see #withHideTag(boolean)
     */
    public @NotNull Rule unhideContent() {
        return withHideTag(false);
    }

    /**
     * Hide the tag associated with this rule.
     * Wrap this rule around the entire right-hand side.
     *
     * @return A new instance of the same class.
     */
    public @NotNull Rule hideTag() {
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
        return Print.ruleToString(this);
    }

    /*
     *Some methods to make constructing combinators easier.
     */

    /**
     * Repeat {@code this} {@code min} to {@code max} times.
     *
     * @return An {@link VariableRepetitionRule} or something that would give equivalent output.
     * @see VariableRepetitionRule#create(Rule, int, int)
     */
    public final @NotNull Rule repeat(final int min, final int max) {
        return VariableRepetitionRule.create(this, min, max);
    }

    /**
     * Repeat {@code this} {@code min} to {@code 2^31-1} (max int) times.
     *
     * @return An {@link VariableRepetitionRule} or something that would give equivalent output.
     * @see VariableRepetitionRule#create(Rule, int, int)
     */
    public final @NotNull Rule repeatLeast(final int min) {
        return VariableRepetitionRule.create(this, min, Integer.MAX_VALUE);
    }

    /**
     * Repeat {@code this} zero to {@code max} times.
     *
     * @return An {@link VariableRepetitionRule} or something that would give equivalent output.
     * @see VariableRepetitionRule#create(Rule, int, int)
     */
    public final @NotNull Rule repeatMost(final int max) {
        return VariableRepetitionRule.create(this, 0, max);
    }

    /**
     * Creates an {@link ZeroOrMoreRule} wrapping {@code this} or something that would give equivalent output.
     *
     * @return An {@link ZeroOrMoreRule} or something that would give equivalent output.
     * @see ZeroOrMoreRule#create(Rule)
     */
    public final @NotNull Rule zeroOrMore() {
        return ZeroOrMoreRule.create(this);
    }

    /**
     * Creates an {@link OnceOrMoreRule} wrapping {@code this} or something that would give equivalent output.
     *
     * @return An {@link OnceOrMoreRule} or something that would give equivalent output.
     * @see OnceOrMoreRule#create(Rule)
     */
    public final @NotNull Rule onceOrMore() {
        return OnceOrMoreRule.create(this);
    }

    /**
     * Creates an {@link OptionalRule} wrapping {@code this} or something that would give equivalent output.
     *
     * @return An {@link OptionalRule} or something that would give equivalent output.
     * @see OptionalRule#create(Rule)
     */
    public final @NotNull Rule optional() {
        return OptionalRule.create(this);
    }

    /**
     * Creates an {@link ExclusionRule} or something that would give equivalent output. {@code this} is the first rule, the parameter is the excluded rule.
     *
     * @param rule The rule to exclude.
     * @return An {@link ExclusionRule} or something that would give equivalent output.
     * @see ExclusionRule#create(Rule, Rule)
     */
    public final @NotNull Rule butNot(final @NotNull Rule rule) {
        return ExclusionRule.create(this, rule);
    }

    /**
     * Creates an {@link ConcatRule} with {@code this} as the first element or something that would give equivalent output.
     *
     * @param rules The other rules.
     * @return An {@link ConcatRule} or something that would give equivalent output.
     * @see ConcatRule#create(List)
     */
    public final @NotNull Rule andThen(final @NotNull Rule... rules) {
        List<Rule> res = new ArrayList<>();
        res.add(this);
        res.addAll(Arrays.asList(rules));
        return ConcatRule.create(res);
    }

    /**
     * Creates an {@link AlternationRule} with {@code this} as the first element or something that would give equivalent output.
     *
     * @param rules The other rules.
     * @return An {@link AlternationRule} or something that would give equivalent output.
     * @see AlternationRule#create(List)
     */
    public final @NotNull Rule or(final @NotNull Rule... rules) {
        List<Rule> res = new ArrayList<>();
        res.add(this);
        res.addAll(Arrays.asList(rules));
        return AlternationRule.create(res);
    }
}
