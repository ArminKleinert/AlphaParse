package alphaparse.result;

import alphaparse.Sym;
import alphaparse.collections.FlatResultSeq;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class represents parse trees. Throughout the documentation, trees are typically notated as lists.
 */
public final class ParseTree implements List<@NotNull Node>, AlphaParseResult {
    /**
     * A technically invalid tag. It is used to mark trees "without" a tag. Such trees should always be subtrees of other parse trees and are "flattened" when used. See {@link #create(List, int, int)}
     */
    public static @NotNull Node.NodeTreeTag NULL_TAG = new Node.NodeTreeTag(Sym.sym("\0\0\0\0"));

//    private final @NotNull Node.NodeTreeTag tag;
//    private final @NotNull List<@NotNull Node> content;
    private final @NotNull List<@NotNull Node> tagContent;

    //private final @NotNull List<@NotNull Node> tagAndContent;

    private final int spanStart;
    private final int spanEndExclusive;

    private ParseTree(final @NotNull List<@NotNull Node> tagContent,
                      final int spanStart,
                      final int spanEndExclusive) {
//        this.tag = tag;
//        this.content = content;

        this.tagContent = tagContent;
//        tagContent.add(tag);
//        tagContent.addAll(content);

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
        return (Node.NodeTreeTag) tagContent.getFirst();
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
        return tagContent.subList(1, tagContent.size());
    }

    /**
     * Returns the tag ({@link #getTag()} and content ({@link #getContent()}) into a single list.
     * This method is not recursive, ie does not apply to subtrees.
     *
     * @return The tree as a list of nodes.
     */
    public @NotNull List<@NotNull Node> toList() {
        return tagContent;
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
        var tagNode = new Node.NodeTreeTag(tag);
        final @NotNull var afs = switch (content) {
            case null -> List.<Node>of(tagNode);
            case FlatResultSeq objects -> {
                final @NotNull var res = new ArrayList<Node>();
                res.add(tagNode);
                for (@NotNull var t : objects) res.add(Node.of(t));
                yield res;
            }
            case String ignored -> List.of(tagNode,Node.of(content));
            case TotalParsesFailureNode ignored -> List.of(tagNode,Node.of(content));
            case ParseFailureNode ignored -> List.of(tagNode,Node.of(content));
            case ParseTree ignored -> List.of(tagNode,Node.of(content));
            case List<?> objects -> {
                final @NotNull var res = new ArrayList<Node>();
                res.add(tagNode);
                for (@NotNull var t : objects) res.add(Node.of(t));
                yield res;
            }
            default -> throw new IllegalArgumentException(content.getClass().toString());
        };
        return create(afs, spanStart, spanEnd);
    }

    /**
     * Creates a parse tree from a tag and content.
     * <p>
     * This method also "flattens" nested trees if necessary. Trees need to be flattened if the content includes any subtrees that have the {@link ParseTree#NULL_TAG}.
     * <p>
     * E.g. {@code [:S, "A", [:S, "A"]]} is flat, but {@code [:S, [NULL_TAG, "A"], [:S, "A"]]} is not.
     * This happens when hide-tags are used, for example in the grammar {@code "S := A S | EPS \n <A> := 'A'"}.
     *
//     * @param tag       The tag as a node.
     * @param content   The content as a node.
     * @param spanStart Starting index in the input (inclusive).
     * @param spanEnd   End index in the input (exclusive).
     * @return A new parse tree.
     */
    public static @NotNull ParseTree create(final @NotNull List<Node> content,
                                            final int spanStart,
                                            final int spanEnd) {
        // If the content contains a ParseTree which has the NULL_TAG, it has to be "flattened".
        boolean isFlat = true;
        for (Node node : content) {
            if (node instanceof Node.NodeParseTree(ParseTree parseTree)
                    && parseTree.getTag().equals(NULL_TAG)) {
                // Subtree with NULL_TAG found. Must merge.
                isFlat = false;
                break;
            }
        }
        if (isFlat) {
            return new ParseTree(content, spanStart, spanEnd);
        }

        // The rest of this method handles the case that there is an unflattened subtree.

        final @NotNull var entries = new ArrayList<@NotNull Node>();
        for (@NotNull var e : content) {
            if (!(e instanceof Node.NodeParseTree)) {
                entries.add(e);
                continue;
            }

            final @NotNull var subTree = ((Node.NodeParseTree) e).content();

            if (subTree.getTag().equals(NULL_TAG)) {
                entries.addAll(subTree.getContent());
            } else {
                entries.add(Node.of(subTree));
            }
        }

        return new ParseTree(entries, spanStart, spanEnd);
    }

//    /**
//     * Convenience method for creating trees.
//     * <pre>
//     * {@code
//     *   var pt1 = ParseTree.create("S", "a", "a");
//     *   var pt2 = ParseTree.create((Node.NodeTreeTag) Node.of(Keyword.intern("S")), List.of(Node.of("a"), Node.of("a")));
//     *   Assertions.assertEquals(pt2, pt1);
//     * }
//     * </pre>
//     *
//     * @param tag     The tag as a string.
//     * @param content The content as variadic arguments.
//     * @return A new parse tree.
//     * @see #create(Node.NodeTreeTag, List)
//     */
//    public static @NotNull ParseTree create(final @NotNull String tag, final @NotNull Object... content) {
//        return create(new Node.NodeTreeTag(Sym.sym(tag)), Arrays.stream(content).map(Node::of).toList());
//    }

    /**
     * Converts the tree into a nested list. Unlike {@link #toList()}, this method is recursive.
     *
     * @return A nested list of Objects.
     */
    public @NotNull List<@NotNull Object> toRawList() {
        return tagContent.stream()
                .map(node -> (node instanceof Node.NodeParseTree)
                        ? ((Node.NodeParseTree) node).content().toRawList()
                        : node.content())
                .filter(it -> it != NULL_TAG.content())
                .toList();
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

    @Override
    public @NotNull String toString() {
        //return getSpanStart() + " " + getSpanEndExclusive() + " " + toList();
        return toList().toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof List<?> c)) {
            return false;
        }
        if (o instanceof ParseTree that) {
            return Objects.equals(getTag(), that.getTag()) && Objects.equals(getContent(), that.getContent());
        }
        final @NotNull var otherIter = c.iterator();
        if (!otherIter.hasNext()) return false;

        for (var thisNext : this) {
            if (!otherIter.hasNext()) return false;
            final @NotNull var otherNext = otherIter.next();
            if (!Objects.equals(thisNext, otherNext)) return false;
        }

        return !otherIter.hasNext();
    }

    /* SECTION: Methods that allow treating the tree like a list. */

    @Override
    public int size() {
        return tagContent.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return toList().contains(o);
    }

    @Override
    public @NotNull Iterator<@NotNull Node> iterator() {
        return tagContent.iterator();
    }

    @Override
    public @NotNull Object[] toArray() {
        return toList().toArray();
    }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] ts) {
        return toList().toArray(ts);
    }

    @Override
    public boolean add(@NotNull Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return toList().containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends @NotNull Node> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int i, @NotNull Collection<? extends @NotNull Node> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return tagContent.hashCode();
    }

    @Override
    public Node get(final int i) {
        return tagContent.get(i);
    }

    @Override
    public @NotNull Node set(int i, @NotNull Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, @NotNull Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Node remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return toList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return toList().lastIndexOf(o);
    }

    @Override
    public @NotNull ListIterator<@NotNull Node> listIterator() {
        return toList().listIterator();
    }

    @Override
    public @NotNull ListIterator<@NotNull Node> listIterator(int i) {
        return toList().listIterator(i);
    }

    @Override
    public @NotNull List<@NotNull Node> subList(int i, int i1) {
        return toList().subList(i,i1);
    }

    /* SECTION: Methods that always throw UnsupportedOperationException. */

}
