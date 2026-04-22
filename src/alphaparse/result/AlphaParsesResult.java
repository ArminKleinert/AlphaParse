package alphaparse.result;

import alphaparse.list.LazySupplierList;
import alphaparse.list.PretenderList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;


/**
 *
 * @return
 */
public sealed interface AlphaParsesResult
        extends List<ParseTree>
        permits AlphaParsesResult.LazyResultList, AlphaParsesResult.NoParsesResult, AlphaParsesResult.ParsesFailureResult, TotalParsesFailureNode {
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
     *
     * @return
     */
    default boolean isSuccess() {
        return this instanceof LazyResultList;
    }

    /**
     *
     * @return
     */
    default @NotNull AlphaParsesResult castToParsesSuccess() {
        if (this instanceof TotalParsesFailureNode)
            throw new ClassCastException("Cannot cast failure to success.");
        return this;
    }

    /**
     *
     * @return
     */
    default ParsesFailureResult castToParsesFailure() {
        return (ParsesFailureResult) this;
    }

    /**
     *
     * @return
     */
    default List<?> hiccup() {
        if (this instanceof TotalParsesFailureNode)
            throw new ClassCastException("Cannot cast failure to success.");
        return stream().map(ParseTree::hiccup).toList();
    }

    /**
     *
     */
    final class LazyResultList extends LazySupplierList<ParseTree> implements AlphaParsesResult {
        public LazyResultList(final @NotNull Supplier<ParseTree> nextFn, final int maxResults) {
            super(nextFn, maxResults);
        }
    }

    /**
     *
     */
    final class ParsesFailureResult implements AlphaParsesResult, PretenderList<ParseTree> {
        final @NotNull AlphaParseFailure alphaParseFailure;

         ParsesFailureResult(final @NotNull AlphaParseFailure alphaParseFailure) {
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
