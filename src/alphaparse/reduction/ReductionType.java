package alphaparse.reduction;

import alphaparse.Keyword;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A class for output formats
 */
public final class ReductionType {
    private static ReductionType rawNonTerminalReduction = null;
    private static ReductionType nullReduction = null;

    /**
     * TODO
     *
     * @return TODO
     */
    public static @NotNull ReductionType rawNonTerminalReduction() {
        if (rawNonTerminalReduction == null)
            rawNonTerminalReduction = new ReductionType(ParseTree.NULL_TAG, ReductionTypesAvailable.RAW, true);
        return rawNonTerminalReduction;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public static @NotNull ReductionType nullReduction() {
        if (nullReduction == null)
            nullReduction = new ReductionType(ParseTree.NULL_TAG, ReductionTypesAvailable.nullType, true);
        return nullReduction;
    }


    /**
     * TODO
     */
    public enum ReductionTypesAvailable {
        /**
         * The default type.
         */
        NONE,
        /**
         * Default type. Represent parse trees as lists. Might be removed in the future.
         */
        HICCUP,
        /**
         * Has the same properties as {@link ReductionTypesAvailable#NONE}, but will not be replaced with the default type when finalizing the grammar.
         */
        RAW,
        /**
         * Represent parse trees as maps. Not implemented. Might be removed in the future.
         */
        ENLIVE;
        /**
         * Default output format.
         */
        public static final ReductionTypesAvailable defaultType = HICCUP;
        /**
         * The default type when creating instances of {@link alphaparse.parser.Combinator}.
         */
        public static final ReductionTypesAvailable nullType = NONE;
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
        return new ReductionType(key, ReductionType.ReductionTypesAvailable.defaultType, false);
    }

    /**
     * TODO
     *
     * @param key  TODO
     * @param type TODO
     * @return TODO
     */
    public static @NotNull ReductionType nonTerminalReduction(final @NotNull Keyword key, final @NotNull ReductionType.ReductionTypesAvailable type) {
        return new ReductionType(key, type, type != ReductionTypesAvailable.NONE && type != ReductionTypesAvailable.RAW);
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
