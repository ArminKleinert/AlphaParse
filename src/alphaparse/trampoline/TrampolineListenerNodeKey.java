package alphaparse.trampoline;

import alphaparse.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;

public record TrampolineListenerNodeKey(int index, @NotNull Combinator parser) {
}
