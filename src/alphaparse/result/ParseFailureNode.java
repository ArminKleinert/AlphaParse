package alphaparse.result;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 *
 * @param text  TODO
 * @param key   TODO
 * @param start TODO
 * @param end   TODO
 */
public record ParseFailureNode(
        @NotNull String text,
        @NotNull Keyword key,
        int start,
        int end) implements AlphaParseResult {
    @Override
    public @NotNull String toString() {
        return "[" + key + ", could not parse \"" + text + "\" at " + start + ".." + end + "]";
    }
}
