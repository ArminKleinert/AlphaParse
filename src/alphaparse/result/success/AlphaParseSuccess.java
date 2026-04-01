package alphaparse.result.success;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.TotalParsesFailureNode;
import alphaparse.result.AlphaIntermediateResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AlphaParseSuccess implements AlphaIntermediateResult {
    public static final class AlphaParseSuccessNull extends AlphaParseSuccess {
        public AlphaParseSuccessNull(final int index) {
            super(index);
        }

        @Override
        public @Nullable Object getResult() {
            return null;
        }
    }

    public static final class AlphaParseSuccessString extends AlphaParseSuccess {
        private final @NotNull String result;

        public AlphaParseSuccessString(final int index, final @NotNull String result) {
            super(index);
            this.result = result;
            //try {throw new IllegalArgumentException();} catch (IllegalArgumentException e) {e.printStackTrace();}
        }

        @Override
        public @NotNull String getResult() {
            return result;
        }
    }

    public static final class AlphaParseSuccessList extends AlphaParseSuccess {
        private final @NotNull AutoFlattenSeq<Object> result; // TODO Do not use raw objects

        public AlphaParseSuccessList(final int index, final @NotNull AutoFlattenSeq<Object> result) {
            super(index);
            this.result = result;
        }

        @Override
        public @NotNull AutoFlattenSeq<Object> getResult() {
            return result;
        }
    }

    public static final class AlphaParseSuccessParseResult extends AlphaParseSuccess {
        private final @NotNull ParseTree result;

        public AlphaParseSuccessParseResult(final int index, final @NotNull ParseTree result) {
            super(index);
            //try {throw new IllegalArgumentException();} catch (IllegalArgumentException e) {e.printStackTrace();}
            this.result = result;
        }

        @Override
        public @NotNull ParseTree getResult() {
            return result;
        }
    }

    public static final class AlphaParseSuccessWithTotalFailure extends AlphaParseSuccess {
        private final @NotNull TotalParsesFailureNode result;

        public AlphaParseSuccessWithTotalFailure(final int index, final @NotNull TotalParsesFailureNode result) {
            super(index);
            this.result = result;
        }

        @Override
        public @NotNull TotalParsesFailureNode getResult() {
            return result;
        }
    }

    public static final class AlphaParseSuccessWithFailure extends AlphaParseSuccess {
        private final @NotNull ParseFailureNode result;

        public AlphaParseSuccessWithFailure(final int index, final @NotNull ParseFailureNode result) {
            super(index);
            this.result = result;
        }

        @Override
        public @NotNull ParseFailureNode getResult() {
            return result;
        }
    }

    private final int index;

    public AlphaParseSuccess(final int index) {
        //System.out.println("Result " + (result == null ? "null" : result.getClass()));
        this.index = index;
    }

    public static @NotNull AlphaParseSuccess create(int index, final @Nullable Object result) {
        return switch (result) {
            case null -> new AlphaParseSuccessNull(index);
            case String s -> new AlphaParseSuccessString(index, s);
            case ParseTree nodes -> new AlphaParseSuccessParseResult(index, nodes);
            case TotalParsesFailureNode parseTrees -> new AlphaParseSuccessWithTotalFailure(index, parseTrees);
            case AutoFlattenSeq<?> objects -> new AlphaParseSuccessList(index, (AutoFlattenSeq<Object>) objects);
            case ParseFailureNode parseFailureNode -> new AlphaParseSuccessWithFailure(index, parseFailureNode);
            default ->
                    throw new UnsupportedOperationException("Cannot create success node from type " + result.getClass());
        };
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof @NotNull AlphaParseSuccess that)) return false;
        return Objects.equals(index, that.index) && Objects.equals(getResult(), that.getResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, getResult());
    }

    public int getIndex() {
        return index;
    }

    public @NotNull AlphaParseSuccess withResult(final @Nullable Object result) {
        return create(index, result);
    }

    public abstract @Nullable Object getResult();

    @Override
    public String toString() {
        return "AlphaSuccess{"+getResult()+"}";
    }
}
