package instarun.parsetree;


import instarun.Keyword;
import instarun.result.ParseTree;
import instarun.result.ParseFailureNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public sealed interface Node permits Node.NodeFail, Node.NodeParseTree, Node.NodeString, Node.NodeTreeTag {
    static Node of(final @Nullable Object o) {
        if (o == null)
            throw new IllegalArgumentException();
        return switch (o) {
            case ParseTree nodes -> new NodeParseTree(nodes);
            case String s -> new NodeString(s);
            case ParseFailureNode parseFailureNode -> new NodeFail(parseFailureNode);
            default -> throw new IllegalArgumentException();
        };
    }

    Object content();

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
