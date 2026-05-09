package alphaparse.result;

import alphaparse.list.PretenderList;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents parse failure embedded into a parse tree when generating a parse forest.
 *
 * @param text  The text.
 * @param key   The production name which failed.
 * @param start Start index of the failure.
 * @param end   End index of the failure.
 */
public record TotalParsesFailureNode(
        @NotNull String text, @NotNull String key,
        int start, int end)
        implements AlphaParsesResult, PretenderList<ParseTree> {
    @Override
    public @NotNull String toString() {
        return "[" + key + ", could not parse \"" + text + "\" at " + start + ".." + end + "]";
    }
}
