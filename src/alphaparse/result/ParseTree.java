package alphaparse.result;

import alphaparse.Sym;
import alphaparse.flat.FlatSeq;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class represents parse trees. Throughout the documentation, trees are typically notated as lists.
 */
public final class ParseTree implements List<@NotNull Node>, AlphaParseResult {
    /**
     * A technically invalid tag. It is used to mark trees "without" a tag. Such trees should always be subtrees of other parse trees and are "flattened" when used. See {@link #create(Node.NodeTreeTag, List, boolean)}
     */
    public static @NotNull Node.NodeTreeTag NULL_TAG = new Node.NodeTreeTag(Sym.sym("\0\0\0\0"));

    private final @NotNull Node.NodeTreeTag tag;
    private final @NotNull List<@NotNull Node> content;
    private int hashCode = 0;
    private final boolean usedMemoryOptimization;

    private ParseTree(final @NotNull Node.NodeTreeTag tag,
                      final @NotNull List<@NotNull Node> content,
                      final boolean usedMemoryOptimization) {
        this.tag = tag;
        // this.content = flattenRawProductions(content);
        this.content = content;
        this.usedMemoryOptimization = usedMemoryOptimization;
    }

    /**
     * The head of the tree. That is the name of the production used to generate the tree.
     * <p>
     * E.g. for the tree {@code [:S, "a", "b", "c"]}, the tag is {@code :S}.
     *
     * <pre>
     * {@code
     *   var pt = Alpha.parser("S : 'a' 'b' 'c'").parse("abc");
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
     *   var pt = Alpha.parser("S : 'a' 'b' 'c'").parse("abc");
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
     * True if memory optimization was turned on when generating the tree.
     *
     * @return whether memory optimization was turned on when parsing.
     */
    public boolean usedMemoryOptimization() {
        return usedMemoryOptimization;
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


    @Override
    public int size() {
        return content.size() + 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return Objects.equals(tag, o) || content.contains(o);
    }

    @Override
    public @NotNull Iterator<@NotNull Node> iterator() {
        return new Iterator<>() {
            Iterator<@NotNull Node> delegate = null;

            @Override
            public boolean hasNext() {
                return delegate == null || delegate.hasNext();
            }

            @Override
            public Node next() {
                if (delegate == null) {
                    delegate = content.iterator();
                    return tag;
                } else {
                    return delegate.next();
                }
            }
        };
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return toList().toArray();
    }

    @Override
    public @NotNull <T> T @NotNull [] toArray(@NotNull T @NotNull [] ts) {
        return toList().toArray(ts);
    }

    @Override
    public boolean add(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return new HashSet<>(content).containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Node> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int i, @NotNull Collection<? extends Node> collection) {
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

    @Override
    public int hashCode() {
        if (hashCode != 0)
            return hashCode;
        int hc = 1;
        for (final var e : this)
            hc = hc * 31 + Objects.hashCode(e);
        hashCode = hc;
        return hc;
    }

    @Override
    public Node get(final int i) {
        if (i == 0) return tag;
        return content.get(i - 1);
    }

    @Override
    public Node set(final int i, final @NotNull Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int i, final @NotNull Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node remove(final int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(final Object o) {
        if (Objects.equals(tag, o)) return 0;
        int i = content.indexOf(o);
        if (i < 0) return i;
        return i + 1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        int i = content.lastIndexOf(o);
        if (i >= 0) return i + 1;
        if (Objects.equals(tag, o)) return 0;
        return -1;
    }

    @Override
    public @NotNull ListIterator<@NotNull Node> listIterator() {
        return toList().listIterator();
    }

    @Override
    public @NotNull ListIterator<@NotNull Node> listIterator(final int i) {
        return toList().listIterator();
    }

    @Override
    public @NotNull List<@NotNull Node> subList(final int i, final int i1) {
        if (i == 1 && i1 == content.size() + 1) return content;
        return toList().subList(i, i1);
    }

    @Override
    public @NotNull String toString() {
        return toList().toString();
    }

    /**
     * Creates a parse tree from a tag and content.
     *
     * @param tag     The tag as a node.
     * @param content The content as a node.
     * @return A new parse tree.
     * @see #create(Node.NodeTreeTag, List, boolean)
     */
    public static @NotNull ParseTree create(final @NotNull Node.NodeTreeTag tag,
                                            final @NotNull List<Node> content) {
        return create(tag, content, false);
    }

    /**
     * Creates a parse tree from a tag and content.
     * <p>
     * If {@param content} is null, the content list of the tree will be empty. If {@param content} is a {@link FlatSeq}, it becomes the content of the tree. Otherwise, {@param content} becomes a singleton list.
     *
     * @param tag     The tag as a node.
     * @param content The content as a node.
     * @return A new parse tree.
     * @see #create(Node.NodeTreeTag, List)
     */
    public static @NotNull ParseTree create(final @NotNull Sym tag,
                                            final @Nullable Object content) {
        final @NotNull var afs = switch (content) {
            case null -> List.<Node>of();
            case FlatSeq<?> objects -> {
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
        return create(new Node.NodeTreeTag(tag), afs);
    }

    /**
     * Creates a parse tree from a tag and content.
     * <p>
     * This method also "flattens" nested trees if necessary. Trees need to be flattened if the content includes any subtrees that have the {@link ParseTree#NULL_TAG}.
     * <p>
     * E.g. {@code [:S, "A", [:S, "A"]]} is flat, but {@code [:S, [NULL_TAG, "A"], [:S, "A"]]} is not.
     * This happens when hide-tags are used, for example in the grammar {@code "S : A S | EPS \n <A> : 'A'"}.
     * @param tag                    The tag as a node.
     * @param content                The content as a node.
     * @param usedMemoryOptimization Whether memory optimization was used when parsing.
     * @return A new parse tree.
     */
    public static @NotNull ParseTree create(final @NotNull Node.NodeTreeTag tag,
                                            final @NotNull List<Node> content,
                                            final boolean usedMemoryOptimization) {

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
            return new ParseTree(tag, content, usedMemoryOptimization);
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

        return new ParseTree(tag, entries, usedMemoryOptimization);
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
}
