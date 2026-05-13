package alphaparse.functions;

import alphaparse.result.success.AlphaParseMessage;
import org.jetbrains.annotations.NotNull;

/**
 * A functional type which takes a {@link AlphaParseMessage} as an input and returns nothing.
 * This is equivalent to a {@link java.util.function.Consumer} taking an {@link AlphaParseMessage}, but the name is a bit clearer.
 */
@FunctionalInterface
public interface Listener {
    /**
     * Runs the function.
     * @param o The input.
     */
    void execute(final @NotNull AlphaParseMessage o);
}
