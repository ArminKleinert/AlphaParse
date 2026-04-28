package alphaparse.functions;

import alphaparse.result.success.AlphaParseSuccess;
import org.jetbrains.annotations.NotNull;

/**
 * A functional type which takes a {@link AlphaParseSuccess} as an input and returns nothing.
 * This is equivalent to a {@link java.util.function.Consumer} taking an {@link AlphaParseSuccess}, but the name is a bit clearer.
 */
@FunctionalInterface
public interface Listener {
    /**
     * Runs the function.
     * @param o The input.
     */
    void execute(final @NotNull AlphaParseSuccess o);
}
