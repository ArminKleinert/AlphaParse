package alphaparse;

import alphaparse.parser.EpsilonCombinator;
import alphaparse.parser.Parser;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.ParseTree;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.failure.ParseFailureReason;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

final class Repeat {
    public static AlphaParseFailure failureSignal = new AlphaParseFailure(-1, new ArrayList<>());

    private static boolean isEmptyResult(final @NotNull Object result) {
        if (result instanceof ParseTree) return ((ParseTree) result).getContent().isEmpty();
        throw new IllegalArgumentException("Cannot handle class " + result.getClass());
    }

    public static @NotNull AlphaParseResult tryRepeatingParseStrategy(
            final @NotNull Parser parser,
            final @NotNull String text,
            final Sym startProduction) {
        return new AlphaParseFailure(0, List.of(ParseFailureReason.ofEpsilon(EpsilonCombinator.getDefault(), false)));
    }
}
