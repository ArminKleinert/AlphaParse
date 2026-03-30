package alphaparse;

import alphaparse.parser.Parser;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.ParseTree;
import alphaparse.result.AlphaFailure;
import alphaparse.result.failure.failureReason.InstaFailureReasonEpsilon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Repeat {
    public static AlphaFailure failureSignal = new AlphaFailure(-1, new ArrayList<>());

    private static boolean isEmptyResult(final @NotNull Object result) {
        if (result instanceof ParseTree) return ((ParseTree) result).getContent().isEmpty();
        throw new IllegalArgumentException("Cannot handle class " + result.getClass());
    }

    public static @NotNull AlphaParseResult tryRepeatingParseStrategy(
            final @NotNull Parser parser,
            final @NotNull String text,
            final Keyword startProduction) {
        return new AlphaFailure(0, List.of(new InstaFailureReasonEpsilon()));
    }
}
