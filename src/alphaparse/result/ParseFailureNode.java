package alphaparse.result;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents parse failure embedded into a parse tree when parsing only for the first parse.
 *
 * @param text  The text.
 * @param key   The production name which failed.
 * @param start Start index of the failure.
 * @param end   End index of the failure.
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
