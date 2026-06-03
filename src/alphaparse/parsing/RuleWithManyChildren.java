package alphaparse.parsing;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Abstraction of {@link Rule} types which wrap multiple other {@link Rule} instances. Examples: choices, concatenations.
 */
public sealed abstract class RuleWithManyChildren
        extends Rule
        permits AlternationRule, ConcatRule, ExclusionRule, OrderedChoiceRule {
    private long bufferedHashCode = Long.MIN_VALUE;
    protected final @NotNull List<Rule> rules;

    protected RuleWithManyChildren(final boolean hide,
                                   final @NotNull ReductionType red,
                                   final @NotNull List<Rule> rules) {
        super(hide, red);
        this.rules = rules;
    }

    /**
     * Set the inner {@link Rule} list used for parsing and returns an instance of the same class.
     *
     * @return The inner {@link Rule} list.
     */
    public @NotNull List<Rule> getRules() {
        return rules;
    }

    @NotNull
    public RuleWithManyChildren unhideContent() {
        return ((RuleWithManyChildren) withHideTag(false)).withParsers(
                getRules()
                        .stream()
                        .map(Rule::unhideContent)
                        .toList());
    }

    /**
     * Set the inner {@link Rule} list used for parsing and returns an instance of the same class.
     *
     * @param parsers The new inner {@link Rule}.
     * @return A new instance.
     */
    public abstract @NotNull RuleWithManyChildren withParsers(final @NotNull List<@NotNull Rule> parsers);

    @Override
    public boolean equals(Object o) {
        if (!getClass().equals(o.getClass())) return false;
        if (hashCode() != o.hashCode()) return false;
        final @NotNull var that = (RuleWithManyChildren) o;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        return Objects.equals(getRules(), that.getRules());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), getRules());
        return (int) bufferedHashCode;
    }
}