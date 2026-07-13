package alphaparse.result;

import alphaparse.Sym;
import alphaparse.collections.FlatResultSeq;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class represents parse trees. Throughout the documentation, trees are typically notated as lists.
 */
public final class ParseTree extends AbstractList<@NotNull Node> implements List<@NotNull Node>, AlphaParseResult {
    /**
     * A technically invalid tag. It is used to mark trees "without" a tag. Such trees should always be subtrees of other parse trees and are "flattened" when used. See {@link #create(Node.NodeTreeTag, List, int, int)}
     */
    public static @NotNull Node.NodeTreeTag NULL_TAG = new Node.NodeTreeTag(Sym.sym("\0\0\0\0"));

    private final @NotNull Node.NodeTreeTag tag;
    private final @NotNull List<@NotNull Node> content;

    private final int spanStart;
    private final int spanEndExclusive;

    private ParseTree(final @NotNull Node.NodeTreeTag tag,
                      final @NotNull List<@NotNull Node> content,
                      final int spanStart,
                      final int spanEndExclusive) {
        this.tag = tag;
        this.content = content;

        if ((spanStart == -1 && spanEndExclusive != -1) || (spanStart != -1 && spanStart > spanEndExclusive))
            throw new IllegalArgumentException("Invalid span " + spanStart + " to " + spanEndExclusive + ".");

        this.spanStart = spanStart;
        this.spanEndExclusive = spanEndExclusive;
    }

    /**
     * The head of the tree. That is the name of the production used to generate the tree.
     * <p>
     * E.g. for the tree {@code [:S, "a", "b", "c"]}, the tag is {@code :S}.
     *
     * <pre>
     * {@code
     *   var pt = Alpha.parser("S := 'a' 'b' 'c'").parse("abc");
     *   println(pt); // [:S, a, b, c]
     *   println(pt.castToParseSuccess().getTag().content()); // :S
     * }
     * </pre>
     *
     * @return The tag of the tree.
     */
    public @NotNull Node.NodeTreeTag getTag() {
        return tag;
    }

    /**
     * The content of the tree, without the tag.
     *
     * <pre>
     * {@code
     *   var pt = Alpha.parser("S := 'a' 'b' 'c'").parse("abc");
     *   println(pt); // [:S, a, b, c]
     *   println(pt.castToParseSuccess().getContent()); // [a, b, c]
     * }
     * </pre>
     *
     * @return The content of the tree.
     */
    public @NotNull List<@NotNull Node> getContent() {
        return content;
    }

    /**
     * Returns the node at a specific index. May throw {@link IndexOutOfBoundsException}.
     *
     * @param nodeIndex The index.
     * @return The node at the specified index.
     * @throws IndexOutOfBoundsException If there is no node at that index.
     */
    public @NotNull Node nodeAt(final int nodeIndex) {
        return content.get(nodeIndex);
    }

    /**
     * Returns the tag ({@link #getTag()} and content ({@link #getContent()}) into a single list.
     * This method is not recursive, ie does not apply to subtrees.
     *
     * @return The tree as a list of nodes.
     */
    public @NotNull List<@NotNull Node> toList() {
        final @NotNull List<@NotNull Node> alist = new ArrayList<>();
        if (!tag.equals(NULL_TAG)) alist.add(tag);
        alist.addAll(content);
        return Collections.unmodifiableList(alist);
    }

    /**
     * Creates a parse tree from a tag and content.
     * <p>
     * If {@param content} is null, the content list of the tree will be empty. If {@param content} is a {@link FlatResultSeq}, it becomes the content of the tree. Otherwise, {@param content} becomes a singleton list.
     *
     * @param tag     The tag as a node.
     * @param content The content as a node.
     * @return A new parse tree.
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
     * @param tag       The tag as a node.
     * @param content   The content as a node.
     * @param spanStart Starting index in the input (inclusive).
     * @param spanEnd   End index in the input (exclusive).
     * @return A new parse tree.
     */
    public static @NotNull ParseTree create(final @NotNull Sym tag,
                                            final @Nullable Object content,
                                            final int spanStart,
                                            final int spanEnd) {
        final @NotNull var afs = switch (content) {
            case null -> List.<Node>of();
            case FlatResultSeq objects -> {
                final @NotNull var res = new ArrayList<Node>();
                for (@NotNull var t : objects) res.add(Node.of(t));
                yield res;
            }
            case String ignored -> List.of(Node.of(content));
            case TotalParsesFailureNode ignored -> List.of(Node.of(content));
            case ParseFailureNode ignored -> List.of(Node.of(content));
            case ParseTree ignored -> List.of(Node.of(content));
            case List<?> objects -> objects.stream().map(Node::of).toList();
            default -> throw new IllegalArgumentException(content.getClass().toString());
        };
        return create(new Node.NodeTreeTag(tag), afs, spanStart, spanEnd);
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
        // If the content contains a ParseTree which has the NULL_TAG, it has to be "flattened".
        boolean isFlat = true;
        for (Node node : content) {
            if (node instanceof Node.NodeParseTree(ParseTree parseTree)
                    && parseTree.tag.equals(NULL_TAG)) {
                // Subtree with NULL_TAG found. Must merge.
                isFlat = false;
                break;
            }
        }
        if (isFlat) {
            return new ParseTree(tag, content, spanStart, spanEnd);
        }

        // The rest of this method handles the case that there is an unflattened subtree.

        final @NotNull var entries = new ArrayList<@NotNull Node>();
        for (@NotNull var e : content) {
            if (!(e instanceof Node.NodeParseTree)) {
                entries.add(e);
                continue;
            }

            final @NotNull var subTree = ((Node.NodeParseTree) e).content();

            if (subTree.tag.equals(NULL_TAG)) {
                entries.addAll(subTree.getContent());
            } else {
                entries.add(Node.of(subTree));
            }
        }

        return new ParseTree(tag, entries, spanStart, spanEnd);
    }

    /**
     * Converts the tree into a nested list. Unlike {@link #toList()}, this method is recursive.
     *
     * @return A nested list of Objects.
     */
    public @NotNull List<@NotNull Object> toRawList() {
        int i = 0;

        final @NotNull Object[] l;
        if (tag.equals(NULL_TAG)) {
            l = new Object[content.size()];
        } else {
            l = new Object[content.size() + 1];
            l[i++] = tag.content();
        }

        for (@NotNull Node node : content) {
            switch (node) {
                case Node.NodeParseTree npt -> l[i++] = npt.content().toRawList();
                case Node.NodeTreeTag ntt -> l[i++] = ntt.content();
                case Node.NodeString ns -> l[i++] = ns.content();
                case Node.NodeFail nf -> l[i++] = nf.content();
            }
        }

        return Arrays.asList(l);
    }

    /**
     * The starting index in the input string of the parse (inclusive).
     * <pre>
     * {@code
     *         var p = Alpha.parser("S = 'b' A 'n'\nA = 'A'");
     *         var tree = p.parse("bAn").castToParseSuccess();
     *         System.out.println(tree.toString());            // [:S, b, [:A, A], n]
     *         System.out.println(tree.getSpanStart());        // 0
     *         System.out.println(tree.getSpanEndExclusive()); // 3
     * }
     * </pre>
     *
     * @return The starting index in the input string of the parse (inclusive).
     */
    public int getSpanStart() {
        return spanStart;
    }

    /**
     * The end index in the input string of the parse (exclusive).
     * <pre>
     * {@code
     *         var p = Alpha.parser("S = 'b' A 'n'\nA = 'A'");
     *         var tree = p.parse("bAn").castToParseSuccess();
     *         System.out.println(tree.toString());            // [:S, b, [:A, A], n]
     *         System.out.println(tree.getSpanStart());        // 0
     *         System.out.println(tree.getSpanEndExclusive()); // 3
     * }
     * </pre>
     *
     * @return The end index in the input string of the parse (exclusive).
     */
    public int getSpanEndExclusive() {
        return spanEndExclusive;
    }

    /**
     * Given a string (hopefully the one this tree was parsed from), will output the substring which is covered by this tree.
     * <pre>
     * {@code
     *         var p = Alpha.parser("S = 'b' A 'n'\nA = 'Aa'");
     *         var tree = p.parse("bAan").castToParseSuccess();
     *         var subTree = (ParseTree) tree.getContent().get(1).content();
     *         System.out.println(tree);                            // [:S, b, [:A, Aa], n]
     *         System.out.println(subTree);                         // [:A, Aa]
     *         System.out.println(subTree.getSpanStart());          // 1
     *         System.out.println(subTree.getSpanEndExclusive());   // 3
     *         System.out.println(subTree.containedString("bAan")); // Optional[Aa]
     * }
     * </pre>
     *
     * @param s Input string.
     * @return An {@link Optional} containing the string if present or an empty {@link Optional} if the string is not covered by this tree.
     * @throws IllegalArgumentException if this tree has no span specified.
     */
    public @NotNull Optional<@NotNull String> containedString(String s) {
        if (spanEndExclusive == -1) return Optional.empty();
        if (s.length() < spanEndExclusive) throw new IllegalArgumentException();
        return Optional.of(s.substring(spanStart, spanEndExclusive));
    }

    /* SECTION: Methods that allow treating the tree like a list. */

    @Override
    public int size() {
        return tag.equals(NULL_TAG) ? content.size() : content.size() + 1;
    }

    @Override
    public Node get(final int i) {
        if (tag.equals(NULL_TAG)) return content.get(i);
        if (i == 0) return tag;
        return content.get(i - 1);
    }
}
