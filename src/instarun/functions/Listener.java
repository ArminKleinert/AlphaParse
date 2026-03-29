package instarun.functions;

import instarun.result.success.InstaSuccess;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Listener {
    void execute(final @NotNull InstaSuccess o);
}
