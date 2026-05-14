package alphaparse.result.success;

import alphaparse.flat.FlatSeq;
import alphaparse.result.Node;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A parsed "result" consists of an index and an object carrying more information.
 * Usually, the wrapped object is a {@link ParseTree} or a string. But this class is also used to transport other types of objects.
 */
public sealed abstract class AlphaParseMessage {
    private final int index;
    private final Object result;

    /**
     * Simple, abstract constructor taking only the index. The wrapped object is defined in the specific subclasses.
     *
     * @param index The index.
     */
    protected AlphaParseMessage(final int index, final @Nullable Object result) {
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
        return new ParseTreeMessage(index, result);
    }

    /**
     * Creates an instance wrapping a String.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The String.
     * @return The String wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseMessage create(final int index, final @NotNull String result) {
        return new StringMessage(index, result);
    }

    /**
     * Creates an instance wrapping a nothing. This is done when a rule finished successfully, but does not need to carry information.
     *
     * @param index The last index of the previous parse, exclusive.
     * @return An instance wrapping null
     */
    public static @NotNull AlphaParseMessage create(final int index) {
        return new NoValueMessage(index);
    }

    /**
     * Creates an instance wrapping a FlatSeq.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The {@link FlatSeq}.
     * @return The {@link FlatSeq} wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseMessage create(final int index, final @NotNull FlatSeq<?> result) {
        return new ListMessage(index, result);
    }

    /**
     * Creates an instance wrapping a ParseFailureNode.
     *
     * @param index  The last index of the previous parse, exclusive.
     * @param result The {@link ParseFailureNode}.
     * @return The {@link ParseFailureNode} wrapped into an instance of this class.
     */
    public static @NotNull AlphaParseMessage create(final int index, final @NotNull ParseFailureNode result) {
        return new ParseFailureNodeMessage(index, result);
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
     *
     * @return The wrapped object.
     */
    public @Nullable Object getResult() {
        return result;
    }

    public abstract @NotNull List<Node> nodeList();

    @Override
    public String toString() {
        return "AlphaParseMessage{" + getResult() + "}";
    }

    private static final class NoValueMessage extends AlphaParseMessage {
        private NoValueMessage(int index) {
            super(index, null);
        }

        @Override
        public @NotNull List<Node> nodeList() {
            return List.of();
        }
    }

    private static final class StringMessage extends AlphaParseMessage {
        private StringMessage(int index, @NotNull String result) {
            super(index, result);
        }

        @Override
        public @NotNull List<Node> nodeList() {
            return List.of(new Node.NodeString((String) Objects.requireNonNull(getResult())));
        }
    }

    private static final class ParseTreeMessage extends AlphaParseMessage {
        private ParseTreeMessage(int index, @NotNull ParseTree result) {
            super(index, result);
        }

        @Override
        public @NotNull List<Node> nodeList() {
            return List.of(new Node.NodeParseTree((ParseTree) Objects.requireNonNull(getResult())));
        }
    }

    private static final class ListMessage extends AlphaParseMessage {
        private ListMessage(int index, @NotNull FlatSeq<?> result) {
            super(index, result);
        }

        @Override
        public @NotNull List<Node> nodeList() {
            final @NotNull var res = new ArrayList<Node>();
            for (@NotNull var t : (FlatSeq<?>) Objects.requireNonNull(getResult())) res.add(Node.of(t));
            return res;
        }
    }

    private static final class ParseFailureNodeMessage extends AlphaParseMessage {
        private ParseFailureNodeMessage(int index, @NotNull ParseFailureNode result) {
            super(index, result);
        }

        @Override
        public @NotNull List<Node> nodeList() {
            return List.of(new Node.NodeFail((ParseFailureNode) Objects.requireNonNull(getResult())));
        }
    }
}
