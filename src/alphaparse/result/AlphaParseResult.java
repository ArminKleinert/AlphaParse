package alphaparse.result;

import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public sealed interface AlphaParseResult
        permits ParseTree, AlphaParseFailure, ParseFailureNode {
    /**
     * TODO
     *
     * @param o TODO
     * @return TODO
     */
    static @NotNull AlphaParseResult make(final @NotNull Object o) {
        return switch (o) {
            case ParseTree objects -> objects;
            case AlphaParseFailure objects -> objects;
            default -> throw new IllegalArgumentException(o.getClass().toString());
        };
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default boolean isSuccess() {
        return this instanceof ParseTree;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default boolean isFailure() {
        return !isSuccess();
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default @NotNull ParseTree castToParseSuccess() {
        return (ParseTree) this;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default @NotNull AlphaParseFailure castToParseFailure() {
        return (AlphaParseFailure) this;
    }
}
