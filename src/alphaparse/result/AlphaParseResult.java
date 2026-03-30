package alphaparse.result;

import org.jetbrains.annotations.NotNull;

public sealed interface AlphaParseResult
        permits ParseTree, AlphaFailure, ParseFailureNode {
    static @NotNull AlphaParseResult make(final @NotNull Object o) {
        return switch (o) {
            case ParseTree objects -> objects.flattenRawProductions();
            case AlphaFailure objects -> objects;
            //case TotalParsesFailure objects -> objects;
            default -> throw new IllegalArgumentException(o.getClass().toString());
        };
    }

    default @NotNull ParseTree castToParseSuccess() {
        return (ParseTree) this;
    }

    default @NotNull AlphaFailure castToParseFailure() {
        return (AlphaFailure) this;
    }
}
