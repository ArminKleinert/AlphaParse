package instarun.result;

import instarun.Keyword;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record TotalParsesFailureNode(
        @NotNull String text, @NotNull Keyword key,
        int start, int end)
        implements InstaParsesResult, PretenderList<ParseTree> {

    @Override
    public String toString() {
        return "[" + key + ", could not parse \"" + text + "\"]";
    }
}
