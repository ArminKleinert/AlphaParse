package alphaparse.reduction;

import alphaparse.Keyword;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A class for output formats
 */
public final class ReductionType {
    private static ReductionType standardIntermediateReduction = null;
    private static ReductionType standardInitialReduction = null;

    /**
     * TODO
     *
     * @return TODO
     */
    public static @NotNull ReductionType standardIntermediateReduction() {
        if (standardIntermediateReduction == null)
            standardIntermediateReduction = new ReductionType(ParseTree.NULL_TAG, ReductionTypesAvailable.INTERMEDIATE, true);
        return standardIntermediateReduction;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public static @NotNull ReductionType standardInitialReduction() {
        if (standardInitialReduction == null)
            standardInitialReduction = new ReductionType(ParseTree.NULL_TAG, ReductionTypesAvailable.INITIAL, true);
        return standardInitialReduction;
    }


    /**
     * TODO
     */
    public enum ReductionTypesAvailable {
        /**
         * The default type.
         */
        INITIAL,
        /**
         * Has the same properties as {@link ReductionTypesAvailable#INITIAL}, but will not be replaced with the default type when finalizing the grammar.
         */
        INTERMEDIATE,
        /**
         * Default type. Represent parse trees as lists. Might be removed in the future.
         */
        OUTPUT
    }

    private final @NotNull Keyword key;
    private final @NotNull ReductionTypesAvailable type;
    private final boolean hiddenOrRaw;

    private ReductionType(final @NotNull Keyword key, final @NotNull ReductionTypesAvailable type, final boolean hiddenOrRaw) {
        this.key = key;
        this.type = type;
        this.hiddenOrRaw = hiddenOrRaw;
    }

    /**
     * TODO
     *
     * @param key TODO
     * @return TODO
     */
    public static @NotNull ReductionType defaultNonRawReduction(final @NotNull Keyword key) {
        return new ReductionType(key, ReductionType.ReductionTypesAvailable.OUTPUT, false);
    }

    /**
     * TODO
     *
     * @param key  TODO
     * @param type TODO
     * @return TODO
     */
    public static @NotNull ReductionType nonTerminalReduction(final @NotNull Keyword key, final @NotNull ReductionType.ReductionTypesAvailable type) {
        return new ReductionType(key, type, type != ReductionTypesAvailable.INITIAL && type != ReductionTypesAvailable.INTERMEDIATE);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final @NotNull ReductionType that = (ReductionType) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, type, hiddenOrRaw);
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull Keyword getKey() {
        return key;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull ReductionTypesAvailable getReductionType() {
        return type;
    }

    @Override
    public @NotNull String toString() {
        return "{" +
                "key=" + key +
                ", type=" + type +
                '}';
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public boolean isHiddenOrRaw() {
        return hiddenOrRaw;
    }
}
