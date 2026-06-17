package alphaparse.result;

import alphaparse.Sym;
import alphaparse.collections.FlatResultSeq;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class PT {
    private PT() {}

    /**
     * Creates a parse tree from a tag and content.
     *
     * @param tag     The tag as a node.
     * @param content The content as a node.
     * @return A new parse tree.
     * @see #create(Node.NodeTreeTag, List, int, int)
     */
    public static @NotNull ParseTree create(final @NotNull Node.NodeTreeTag tag,
                                            final @NotNull List<Node> content) {
        return create(tag, content, -1, -1);
    }

    /**
     * Creates a parse tree from a tag and content.
     * <p>
     * If {@param content} is null, the content list of the tree will be empty. If {@param content} is a {@link FlatResultSeq}, it becomes the content of the tree. Otherwise, {@param content} becomes a singleton list.
     *
     * @param tag     The tag as a node.
     * @param content The content as a node.
     * @return A new parse tree.
     * @see #create(Node.NodeTreeTag, List)
     */
    public static @NotNull ParseTree create(final @NotNull Sym tag,
                                            final @Nullable Object content) {
        return create(tag, content, -1, -1);
    }

    /**
     * Creates a parse tree from a tag and content.
     * <p>
     * If {@param content} is null, the content list of the tree will be empty. If {@param content} is a {@link FlatResultSeq}, it becomes the content of the tree. Otherwise, {@param content} becomes a singleton list.
     *
     * @param tag     The tag as a node.
     * @param content The content as a node.
     * @param spanStart Starting index in the input (inclusive).
     * @param spanEnd   End index in the input (exclusive).
     * @return A new parse tree.
     * @see #create(Node.NodeTreeTag, List)
     */
    public static @NotNull ParseTree create(final @NotNull Sym tag,
                                            final @Nullable Object content,
                                            final int spanStart,
                                            final int spanEnd) {
        return ParseTree.create(tag,content,spanStart,spanEnd);
    }

    /**
     * Creates a parse tree from a tag and content.
     * <p>
     * This method also "flattens" nested trees if necessary. Trees need to be flattened if the content includes any subtrees that have the {@link ParseTree#NULL_TAG}.
     * <p>
     * E.g. {@code [:S, "A", [:S, "A"]]} is flat, but {@code [:S, [NULL_TAG, "A"], [:S, "A"]]} is not.
     * This happens when hide-tags are used, for example in the grammar {@code "S := A S | EPS \n <A> := 'A'"}.
     *
     * @param tag       The tag as a node.
     * @param content   The content as a node.
     * @param spanStart Starting index in the input (inclusive).
     * @param spanEnd   End index in the input (exclusive).
     * @return A new parse tree.
     */
    public static @NotNull ParseTree create(final @NotNull Node.NodeTreeTag tag,
                                            final @NotNull List<Node> content,
                                            final int spanStart,
                                            final int spanEnd) {
        return ParseTree.create(tag, content, spanStart, spanEnd);
    }

    /**
     * Convenience method for creating trees.
     * <pre>
     * {@code
     *   var pt1 = ParseTree.create("S", "a", "a");
     *   var pt2 = ParseTree.create((Node.NodeTreeTag) Node.of(Keyword.intern("S")), List.of(Node.of("a"), Node.of("a")));
     *   Assertions.assertEquals(pt2, pt1);
     * }
     * </pre>
     *
     * @param tag     The tag as a string.
     * @param content The content as variadic arguments.
     * @return A new parse tree.
     * @see #create(Node.NodeTreeTag, List)
     */
    public static @NotNull ParseTree create(final @NotNull String tag, final @NotNull Object... content) {
        return create(new Node.NodeTreeTag(Sym.sym(tag)), Arrays.stream(content).map(Node::of).toList());
    }
}
