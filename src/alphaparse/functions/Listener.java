package alphaparse.functions;

import alphaparse.result.success.AlphaParseSuccess;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Listener {
    void execute(final @NotNull AlphaParseSuccess o);
}
