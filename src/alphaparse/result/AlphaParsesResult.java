package alphaparse.result;

import alphaparse.list.LazySupplierList;
import alphaparse.list.PretenderList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * TODO
 */
public sealed interface AlphaParsesResult
        extends List<ParseTree>
        permits AlphaParsesResult.LazyResultList, AlphaParsesResult.NoParsesResult, AlphaParsesResult.ParsesFailureResult, TotalParsesFailureNode {
    /**
     * TODO
     *
     * @param o TODO
     * @return TODO
     */
    static @NotNull AlphaParsesResult make(final @NotNull Object o) {
        return switch (o) {
            case TotalParsesFailureNode node -> node;
            case LazyResultList lrl -> lrl;
            case ParsesFailureResult pfr -> pfr;
            case AlphaParseFailure apf -> new ParsesFailureResult(apf);
            default -> throw new IllegalArgumentException(o.getClass().toString());
        };
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default boolean isSuccess() {
        return this instanceof LazyResultList;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default @NotNull LazyResultList castToParsesSuccess() {
        if (!(this instanceof LazyResultList))
            throw new ClassCastException("Cannot cast failure to success.");
        return (LazyResultList) this;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default ParsesFailureResult castToParsesFailure() {
        return (ParsesFailureResult) this;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    default List<?> hiccup() {
        if (this instanceof TotalParsesFailureNode)
            throw new ClassCastException("Cannot cast failure to success.");
        return stream().map(ParseTree::hiccup).toList();
    }

    /**
     * TODO
     */
    final class LazyResultList extends LazySupplierList<ParseTree> implements AlphaParsesResult {
        /**
         * TODO
         *
         * @param nextFn     TODO
         * @param maxResults TODO
         */
        public LazyResultList(final @NotNull IntFunction<ParseTree> nextFn, final int maxResults) {
            super(nextFn, maxResults);
        }
    }

    /**
     * TODO
     */
    final class ParsesFailureResult implements AlphaParsesResult, PretenderList<ParseTree> {
        final @NotNull AlphaParseFailure alphaParseFailure;

        ParsesFailureResult(final @NotNull AlphaParseFailure alphaParseFailure) {
            this.alphaParseFailure = alphaParseFailure;
        }

        /**
         * TODO
         *
         * @return TODO
         */
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

    /**
     * TODO
     */
    final class NoParsesResult extends AbstractList<ParseTree> implements AlphaParsesResult {
        /**
         * TODO
         *
         * @param ptl TODO
         */
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
