package alphaparse.result.success;

import alphaparse.flat.FlatSeq;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.TotalParsesFailureNode;
import alphaparse.result.AlphaIntermediateResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A parsed "success" consists of an index and an object carrying more information.
 * Usually, the wrapped object is a {@link ParseTree} (in a {@link AlphaParseSuccessParseResult}). But this class is also used to transport other types of objects.
 */
public sealed abstract class AlphaParseSuccess implements AlphaIntermediateResult permits
        AlphaParseSuccessList,
        AlphaParseSuccessNull,
        AlphaParseSuccessParseResult,
        AlphaParseSuccessString,
        AlphaParseSuccessWithFailure,
        AlphaParseSuccessWithTotalFailure {

    private final int index;

    /**
     * Simple, abstract constructor taking only the index. The wrapped object is defined in the specific subclasses.
     *
     * @param index The index.
     */
    public AlphaParseSuccess(final int index) {
        this.index = index;
    }

    /**
     * Wraps an object in a {@link AlphaParseSuccess} for further use in the parsing algorithm.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The object to be wrapped. Can be null, String, {@link TotalParsesFailureNode}, {@link FlatSeq}, {@link ParseFailureNode} or {@link ParseTree}.
     * @return A success object. The exact type depends on the input.
     * @throws IllegalArgumentException If the input is not one of the listed types.
     */
    public static @NotNull AlphaParseSuccess create(final int index, final @Nullable Object result) {
        return switch (result) {
            case null -> new AlphaParseSuccessNull(index);
            case String s -> new AlphaParseSuccessString(index, s);
            case TotalParsesFailureNode parseTrees -> new AlphaParseSuccessWithTotalFailure(index, parseTrees);
            case FlatSeq<?> objects -> new AlphaParseSuccessList(index, (FlatSeq<Object>) objects);
            case ParseFailureNode parseFailureNode -> new AlphaParseSuccessWithFailure(index, parseFailureNode);
            case ParseTree nodes -> new AlphaParseSuccessParseResult(index, nodes);
            default ->
                    throw new IllegalArgumentException("Cannot create success node from type " + result.getClass());
        };
    }

    /**
     * Creates a {@link AlphaParseSuccess} wrapping a ParseTree.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The {@link ParseTree}.
     * @return The {@link ParseTree} wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseSuccess create(final int index, final @NotNull ParseTree result) {
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

    @Override
    public int index() {
        return index;
    }

    /**
     * TODO
     *
     * @param result TODO
     * @return TODO
     */
    public @NotNull AlphaParseSuccess withResult(final @Nullable Object result) {
        return create(index, result);
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public abstract @Nullable Object getResult();

    @Override
    public String toString() {
        return "AlphaSuccess{" + getResult() + "}";
    }
}
