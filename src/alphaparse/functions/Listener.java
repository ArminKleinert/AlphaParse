package alphaparse.functions;

import alphaparse.result.success.InstaSuccess;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Listener {
    void execute(final @NotNull InstaSuccess o);
}
