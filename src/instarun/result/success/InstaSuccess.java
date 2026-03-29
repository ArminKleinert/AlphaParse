package instarun.result.success;

import instarun.IO2;
import instarun.flat.AutoFlattenSeq;
import instarun.result.ParseTree;
import instarun.result.ParseFailureNode;
import instarun.result.TotalParsesFailureNode;
import instarun.result.InstaIntermediateResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class InstaSuccess implements InstaIntermediateResult {
    public static final class InstaSuccessNull extends InstaSuccess {
        public InstaSuccessNull(final int index) {
            super(index);
        }

        @Override
        public @Nullable Object getResult() {
            return null;
        }
    }

    public static final class InstaSuccessString extends InstaSuccess {
        private final String result;

        public InstaSuccessString(final int index, final @NotNull String result) {
            super(index);
            this.result = result;
            //try {throw new IllegalArgumentException();} catch (IllegalArgumentException e) {e.printStackTrace();}
        }

        @Override
        public @NotNull String getResult() {
            return result;
        }
    }

    public static final class InstaSuccessList extends InstaSuccess {
        private final List<Object> result; // TODO Do not use raw objects

        public InstaSuccessList(final int index, final @NotNull List<Object> result) {
            super(index);
            this.result = result;
            if (!(result instanceof AutoFlattenSeq<Object>)) {
                IO2.println("HERE: " + result.getClass());
                throw new RuntimeException();
            }
        }

        @Override
        public @NotNull List<Object> getResult() {
            return result;
        }
    }

    public static final class InstaSuccessParseResult extends InstaSuccess {
        private final ParseTree result;

        public InstaSuccessParseResult(final int index, final @NotNull ParseTree result) {
            super(index);
            //try {throw new IllegalArgumentException();} catch (IllegalArgumentException e) {e.printStackTrace();}
            this.result = result;
        }

        @Override
        public @NotNull ParseTree getResult() {
            return result;
        }
    }

    public static final class InstaSuccessWithTotalFailure extends InstaSuccess {
        private final TotalParsesFailureNode result;

        public InstaSuccessWithTotalFailure(final int index, final @NotNull TotalParsesFailureNode result) {
            super(index);
            this.result = result;
        }

        @Override
        public @NotNull TotalParsesFailureNode getResult() {
            return result;
        }
    }

    public static final class InstaSuccessWithFailure extends InstaSuccess {
        private final ParseFailureNode result;

        public InstaSuccessWithFailure(final int index, final @NotNull ParseFailureNode result) {
            super(index);
            this.result = result;
        }

        @Override
        public @NotNull ParseFailureNode getResult() {
            return result;
        }
    }

    private final int index;

    public InstaSuccess(final int index) {
        //System.out.println("Result " + (result == null ? "null" : result.getClass()));
        this.index = index;
    }

    public static InstaSuccess create(int index, final @Nullable Object result) {
        return switch (result) {
            case null -> new InstaSuccessNull(index);
            case String s -> new InstaSuccessString(index, s);
            case ParseTree nodes -> new InstaSuccessParseResult(index, nodes);
            case TotalParsesFailureNode parseTrees -> new InstaSuccessWithTotalFailure(index, parseTrees);
            case List<?> objects -> new InstaSuccessList(index, (List<Object>) objects);
            case ParseFailureNode parseFailureNode -> new InstaSuccessWithFailure(index, parseFailureNode);
            default ->
                    throw new UnsupportedOperationException("Cannot create success node from type " + result.getClass());
        };
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof @NotNull InstaSuccess that)) return false;
        return Objects.equals(index, that.index) && Objects.equals(getResult(), that.getResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, getResult());
    }

    public int getIndex() {
        return index;
    }

    public @NotNull InstaSuccess withResult(final @Nullable Object result) {
        //return new InstaSuccess(index, new InstaSuccessReason(result));
        return create(index, result);
    }

    public abstract @Nullable Object getResult();
}
