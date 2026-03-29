package instarun;

import instarun.parser.Parser;
import instarun.result.InstaParseResult;
import instarun.result.ParseTree;
import instarun.result.InstaFailure;
import instarun.result.failure.failureReason.InstaFailureReason;
import instarun.result.failure.failureReason.InstaFailureReasonEpsilon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Repeat {
    public static InstaFailure failureSignal = new InstaFailure(-1, new ArrayList<>());

    private static boolean isEmptyResult(final @NotNull Object result) {
        if (result instanceof ParseTree) return ((ParseTree) result).getContent().isEmpty();
        throw new IllegalArgumentException("Cannot handle class " + result.getClass());
    }

    public static @NotNull InstaParseResult tryRepeatingParseStrategy(
            final @NotNull Parser parser,
            final @NotNull String text,
            final Keyword startProduction) {
        return new InstaFailure(0, List.of(new InstaFailureReasonEpsilon()));
    }
}
