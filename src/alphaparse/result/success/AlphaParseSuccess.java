package alphaparse.result.success;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.TotalParsesFailureNode;
import alphaparse.result.AlphaIntermediateResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public sealed abstract class AlphaParseSuccess implements AlphaIntermediateResult permits
        AlphaParseSuccessList,
        AlphaParseSuccessNull,
        AlphaParseSuccessParseResult,
        AlphaParseSuccessString,
        AlphaParseSuccessWithFailure,
        AlphaParseSuccessWithTotalFailure {

    private final int index;

    public AlphaParseSuccess(final int index) {
        this.index = index;
    }

    public static @NotNull AlphaParseSuccess create(int index, final @Nullable Object result) {
        return switch (result) {
            case null -> new AlphaParseSuccessNull(index);
            case String s -> new AlphaParseSuccessString(index, s);
            case TotalParsesFailureNode parseTrees -> new AlphaParseSuccessWithTotalFailure(index, parseTrees);
            case AutoFlattenSeq<?> objects -> new AlphaParseSuccessList(index, (AutoFlattenSeq<Object>) objects);
            case ParseFailureNode parseFailureNode -> new AlphaParseSuccessWithFailure(index, parseFailureNode);
            case ParseTree nodes -> new AlphaParseSuccessParseResult(index, nodes);
            default ->
                    throw new UnsupportedOperationException("Cannot create success node from type " + result.getClass());
        };
    }

    public static @NotNull AlphaParseSuccess create(int index, final @NotNull ParseTree result) {
        return new AlphaParseSuccessParseResult(index, result);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof @NotNull AlphaParseSuccess that)) return false;
        return Objects.equals(index, that.index) && Objects.equals(getResult(), that.getResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, getResult());
    }

    public int index() {
        return index;
    }

    public @NotNull AlphaParseSuccess withResult(final @Nullable Object result) {
        return create(index, result);
    }

    public abstract @Nullable Object getResult();

    @Override
    public String toString() {
        return "AlphaSuccess{" + getResult() + "}";
    }
}
