package alphaparse.result;

import org.jetbrains.annotations.NotNull;

public sealed interface AlphaParseResult
        permits ParseTree, AlphaParseFailure, ParseFailureNode {
    static @NotNull AlphaParseResult make(final @NotNull Object o) {
        return switch (o) {
            case ParseTree objects -> objects;
            case AlphaParseFailure objects -> objects;
            default -> throw new IllegalArgumentException(o.getClass().toString());
        };
    }

    default boolean isSuccess() {
        return this instanceof ParseTree;
    }

    default boolean isFailure() {
        return !isSuccess();
    }

    default @NotNull ParseTree castToParseSuccess() {
        return (ParseTree) this;
    }

    default @NotNull AlphaParseFailure castToParseFailure() {
        return (AlphaParseFailure) this;
    }
}
