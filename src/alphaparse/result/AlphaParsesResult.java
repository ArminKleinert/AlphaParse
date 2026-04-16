package alphaparse.result;

import alphaparse.list.LazySupplierList;
import alphaparse.list.PretenderList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public sealed interface AlphaParsesResult
        extends List<ParseTree>
        permits AlphaParsesResult.AlphaParsesResultList, AlphaParsesResult.LazyResultList, AlphaParsesResult.NoParsesResult, AlphaParsesResult.ParsesFailureResult, TotalParsesFailureNode {
    static @NotNull AlphaParsesResult make(final @NotNull Object o) {
        return switch (o) {
            case TotalParsesFailureNode node -> node;
            case LazyResultList lrl -> lrl;
            case ParsesFailureResult pfr -> pfr;
            default -> throw new IllegalArgumentException(o.getClass().toString());
        };
    }

    default boolean isSuccess() {
        return this instanceof LazyResultList;
    }

    default @NotNull AlphaParsesResult castToParsesSuccess() {
        if (this instanceof TotalParsesFailureNode)
            throw new ClassCastException("Cannot cast failure to success.");
        return this;
    }

    default ParsesFailureResult castToParsesFailure() {
        return (ParsesFailureResult) this;
    }

    default List<?> hiccup() {
        if (this instanceof TotalParsesFailureNode)
            throw new ClassCastException("Cannot cast failure to success.");
        return stream().map(ParseTree::hiccup).toList();
    }

    final class LazyResultList extends LazySupplierList<ParseTree> implements AlphaParsesResult {
        public LazyResultList(final @NotNull Supplier<ParseTree> nextFn, final int maxResults) {
            super(nextFn, maxResults);
        }
    }

    final class AlphaParsesResultList implements AlphaParsesResult {
        private final @NotNull List<ParseTree> inner;

        public AlphaParsesResultList(final @NotNull List<ParseTree> inner) {
            this.inner = inner;
        }

        @Override
        public boolean equals(final @NotNull Object o) {
            if (!(o instanceof List<?>)) return false;
            return Objects.equals(inner, o);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(inner);
        }

        @Override
        public String toString() {
            return inner.toString();
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean isEmpty() {
            return inner.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return inner.contains(o);
        }

        @Override
        public @NotNull Iterator<ParseTree> iterator() {
            return inner.iterator();
        }

        @Override
        public @NotNull Object @NotNull [] toArray() {
            return inner.toArray();
        }

        @Override
        public <T1> @NotNull T1 @NotNull [] toArray(final @NotNull T1 @NotNull [] ts) {
            return inner.toArray(ts);
        }

        @Override
        public boolean add(final @NotNull ParseTree o) {
            return inner.add(o);
        }

        @Override
        public boolean remove(final @NotNull Object o) {
            return inner.remove(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> collection) {
            return new HashSet<>(inner).containsAll(collection);
        }

        @Override
        public boolean addAll(final @NotNull Collection<? extends @NotNull ParseTree> collection) {
            return inner.addAll(collection);
        }

        @Override
        public boolean addAll(final int i, @NotNull Collection<? extends @NotNull ParseTree> collection) {
            return inner.addAll(i, collection);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> collection) {
            return inner.removeAll(collection);
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> collection) {
            return inner.retainAll(collection);
        }

        @Override
        public void clear() {
            inner.clear();
        }

        @Override
        public ParseTree get(final int i) {
            return inner.get(i);
        }

        @Override
        public ParseTree set(final int i, final @NotNull ParseTree o) {
            return inner.set(i, o);
        }

        @Override
        public void add(final int i, final @NotNull ParseTree o) {
            inner.add(o);
        }

        @Override
        public ParseTree remove(final int i) {
            return inner.remove(i);
        }

        @Override
        public int indexOf(final @NotNull Object o) {
            return inner.indexOf(o);
        }

        @Override
        public int lastIndexOf(final @NotNull Object o) {
            return inner.lastIndexOf(o);
        }

        @Override
        public @NotNull ListIterator<ParseTree> listIterator() {
            return inner.listIterator();
        }

        @Override
        public @NotNull ListIterator<ParseTree> listIterator(final int i) {
            return inner.listIterator(i);
        }

        @Override
        public @NotNull List<ParseTree> subList(final int i, final int i1) {
            return inner.subList(i, i1);
        }
    }

    final class ParsesFailureResult implements AlphaParsesResult, PretenderList<ParseTree> {
        final @NotNull AlphaParseFailure alphaParseFailure;

        public ParsesFailureResult(final @NotNull AlphaParseFailure alphaParseFailure) {
            this.alphaParseFailure = alphaParseFailure;
        }

        public @NotNull AlphaParseFailure asFailure() {
            return alphaParseFailure;
        }

        @Override
        public ParseTree get(final int i) {
            Objects.checkIndex(i, 0);
            throw new IllegalStateException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ParsesFailureResult that)) return false;
            if (!super.equals(o)) return false;
            return Objects.equals(alphaParseFailure, that.alphaParseFailure);
        }

        @Override
        public int hashCode() {
            return Objects.hash(alphaParseFailure);
        }

        @Override
        public String toString() {
            return alphaParseFailure.toString();
        }
    }

    final class NoParsesResult extends AbstractList<ParseTree> implements AlphaParsesResult {
        public NoParsesResult(List<?> ptl) {
            if (!(ptl.isEmpty())) throw new IllegalStateException();
        }

        @Override
        public ParseTree get(final int i) {
            Objects.checkIndex(i, 0);
            throw new IllegalStateException();
        }

        @Override
        public int size() {
            return 0;
        }
    }
}
