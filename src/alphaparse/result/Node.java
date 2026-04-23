package alphaparse.result;


import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * TODO
 */
public sealed interface Node permits Node.NodeFail, Node.NodeParseTree, Node.NodeString, Node.NodeTreeTag {
    /**
     * TODO
     *
     * @param o TODO
     * @return TODO
     */
    static @NotNull Node of(final @Nullable Object o) {
        if (o == null)
            throw new IllegalArgumentException("Input cannot be null.");
        return switch (o) {
            case ParseTree nodes -> new NodeParseTree(nodes);
            case String s -> new NodeString(s);
            case ParseFailureNode parseFailureNode -> new NodeFail(parseFailureNode);
            default -> throw new IllegalArgumentException("Cannot handle input type " + o.getClass());
        };
    }

    /**
     * TODO
     *
     * @return TODO
     */
    @NotNull Object content();

    /**
     * TODO
     *
     * @param content TODO
     */
    record NodeTreeTag(@NotNull Keyword content) implements Node {
        @Override
        public boolean equals(Object o) {
            if (Objects.equals(content(), o)) return true;
            if (!(o instanceof Node that)) return false;
            return Objects.equals(content(), that.content());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(content());
        }

        @Override
        public @NotNull String toString() {
            return content().toString();
        }
    }

    /**
     * TODO
     *
     * @param content TODO
     */
    record NodeParseTree(@NotNull ParseTree content) implements Node {
        @Override
        public boolean equals(Object o) {
            if (Objects.equals(content(), o)) return true;
            if (!(o instanceof Node that)) return false;
            return Objects.equals(content(), that.content());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(content());
        }

        @Override
        public @NotNull String toString() {
            return content().toString();
        }
    }

    /**
     * TODO
     *
     * @param content TODO
     */
    record NodeString(@NotNull String content) implements Node {
        @Override
        public boolean equals(Object o) {
            if (Objects.equals(content(), o)) return true;
            if (!(o instanceof Node that)) return false;
            return Objects.equals(content(), that.content());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(content());
        }

        @Override
        public @NotNull String toString() {
            return content();
        }
    }

    /**
     * TODO
     *
     * @param content TODO
     */
    record NodeFail(@NotNull ParseFailureNode content) implements Node {
        @Override
        public boolean equals(Object o) {
            if (Objects.equals(content(), o)) return true;
            if (!(o instanceof Node that)) return false;
            return Objects.equals(content(), that.content());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(content());
        }

        @Override
        public @NotNull String toString() {
            return content().toString();
        }
    }
}
