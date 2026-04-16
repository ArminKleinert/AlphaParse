package alphaparse.result;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

public record TotalParsesFailureNode(
        @NotNull String text, @NotNull Keyword key,
        int start, int end)
        implements AlphaParsesResult, PretenderList<ParseTree> {

    @Override
    public @NotNull String toString() {
        return "[" + key + ", could not parse \"" + text + "\" at "+start+".." + end+"]";
    }
}
