package alphaparse.result;

import alphaparse.Keyword;
import alphaparse.list.UnmodList;
import alphaparse.parsetree.Node;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ParseTree implements List<Node>, AlphaParseResult {
    public static @NotNull String NULL_TAG_NAME = "\0\0\0\0";
    public static @NotNull Keyword NULL_TAG = Keyword.intern(NULL_TAG_NAME);

    private final @NotNull Node.NodeTreeTag tag;
    private final @NotNull List<Node> content;
    private int hashCode = 0;

    public ParseTree(final @NotNull String tag, final @NotNull Object... content) {
        this(new Node.NodeTreeTag(Keyword.intern(tag)), Arrays.stream(content).map(Node::of).toList());
    }

    public ParseTree(final @NotNull Keyword tag, final @NotNull List<Object> content) {
        this(new Node.NodeTreeTag(tag), content.stream().map(Node::of).toList());
    }

    public ParseTree(final @NotNull Node.NodeTreeTag tag, final @NotNull List<Node> content) {
        this.tag = tag;
        this.content = content;
    }

    public @NotNull Node.NodeTreeTag getTag() {
        return tag;
    }

    public @NotNull List<Node> getContent() {
        return content;
    }

    public @NotNull List<Node> toList() {
        final @NotNull List<Node> alist = new ArrayList<>();
        alist.add(tag);
        alist.addAll(content);
        return new UnmodList<>(alist);
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
    public @NotNull Iterator<Node> iterator() {
        return new Iterator<>() {
            Iterator<Node> delegate = null;

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
    public String toString() {
        return toList().toString();
    }

    public @NotNull ParseTree flattenRawProductions() {
        final @NotNull var entries = new ArrayList<@NotNull Node>();
        for (@NotNull var e : content) {
            if (!(e instanceof Node.NodeParseTree)) {
                entries.add(e);
                continue;
            }

            final @NotNull var eContent = ((Node.NodeParseTree) e).content();
            final @NotNull var e1 = eContent.flattenRawProductions();
            if (Objects.equals(eContent.getTag().content(), NULL_TAG)) {
                entries.addAll(e1.getContent());
            } else {
                entries.add(Node.of(e1));
            }
        }
        return new ParseTree(tag, entries);
    }

    public @NotNull List<@NotNull Object> hiccup() {
        int i = 0;

        final @NotNull Object[] l;
        if (tag.content().equals(NULL_TAG)) {
            l = new Object[content.size()];
        } else {
            l = new Object[content.size() + 1];
            l[i++] = tag.content();
        }

        for (@NotNull Node node : content) {
            switch (node) {
                case Node.NodeParseTree npt -> l[i++] = npt.content().hiccup();
                case Node.NodeTreeTag ntt -> l[i++] = ntt.content();
                case Node.NodeString ns -> l[i++] = ns.content();
                case Node.NodeFail nf -> l[i++] = nf.content();
            }
        }

        return new UnmodList<>(l);
    }
}
