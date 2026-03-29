package instarun.result;

import org.jetbrains.annotations.NotNull;

public sealed interface InstaParseResult
        permits ParseTree, InstaFailure, ParseFailureNode {
    static @NotNull InstaParseResult make(final @NotNull Object o) {
        return switch (o) {
            case ParseTree objects -> objects.flattenRawProductions();
            case InstaFailure objects -> objects;
            //case TotalParsesFailure objects -> objects;
            default -> throw new IllegalArgumentException(o.getClass().toString());
        };
    }

    default @NotNull ParseTree castToParseSuccess() {
        return (ParseTree) this;
    }

    default @NotNull InstaFailure castToParseFailure() {
        return (InstaFailure) this;
    }
}
