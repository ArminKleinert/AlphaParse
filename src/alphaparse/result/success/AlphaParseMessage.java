package alphaparse.result.success;

import alphaparse.flat.FlatSeq;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A parsed "result" consists of an index and an object carrying more information.
 * Usually, the wrapped object is a {@link ParseTree} or a string. But this class is also used to transport other types of objects.
 * For specifics, see {@link AlphaParseMessage#getResult()}.
 */
public final class AlphaParseMessage {
    private final int index;
    private final Object result;

    /**
     * Simple, abstract constructor taking only the index. The wrapped object is defined in the specific subclasses.
     *
     * @param index The index.
     */
    private AlphaParseMessage(final int index, final @Nullable Object result) {
        this.index = index;
        this.result = result;
    }

    /**
     * Creates an instance wrapping a ParseTree.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The {@link ParseTree}.
     * @return The {@link ParseTree} wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseMessage create(final int index, final @NotNull ParseTree result) {
        return new AlphaParseMessage(index, result);
    }

    /**
     * Creates an instance wrapping a String.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The String.
     * @return The String wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseMessage create(final int index, final @NotNull String result) {
        return new AlphaParseMessage(index, result);
    }

    /**
     * Creates an instance wrapping a nothing. This is done when a rule finished successfully, but does not need to carry information.
     *
     * @param index The last index of the previous parse, exclusive.
     * @return An instance wrapping null.
     */
    public static @NotNull AlphaParseMessage create(final int index) {
        return new AlphaParseMessage(index, null);
    }

    /**
     * Creates an instance wrapping a FlatSeq.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The {@link FlatSeq}.
     * @return The {@link FlatSeq} wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseMessage create(final int index, final @NotNull FlatSeq<?> result) {
        return new AlphaParseMessage(index, result);
    }

    /**
     * Creates an instance wrapping a ParseFailureNode.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The {@link ParseFailureNode}.
     * @return The {@link ParseFailureNode} wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseMessage create(final int index, final @NotNull ParseFailureNode result) {
        return new AlphaParseMessage(index, result);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof @NotNull AlphaParseMessage that)) return false;
        return Objects.equals(index, that.index) && Objects.equals(getResult(), that.getResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, getResult());
    }

    /**
     * The index of the message object.
     *
     * @return The index as an integer.
     */
    public int index() {
        return index;
    }

    /**
     * Creates a new instance with the current index.
     *
     * @return A new instance with the current index.
     */
    public @NotNull AlphaParseMessage reset() {
        return create(index);
    }

    /**
     * The wrapped object.
     * <p>
     * This is always {@code null} or an instance of {@link String}, {@link ParseTree}, {@link ParseFailureNode} or {@link FlatSeq}.
     * If the object is a {@link FlatSeq}, the contents are any of the classes mentioned above.
     *
     * @return The wrapped object.
     */
    public @Nullable Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AlphaParseMessage.class.getSimpleName() + "[", "]")
                .add("index=" + index)
                .add("result=" + result)
                .toString();
    }
}
