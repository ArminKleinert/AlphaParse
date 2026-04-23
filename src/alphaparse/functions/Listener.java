package alphaparse.functions;

import alphaparse.result.success.AlphaParseSuccess;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
@FunctionalInterface
public interface Listener {
    /**
     * TODO
     * @param o TODO
     */
    void execute(final @NotNull AlphaParseSuccess o);
}
