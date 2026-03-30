package alphaparse.trampoline;

import alphaparse.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;

public record TrampolineListenerNodeKey1(int index, @NotNull Combinator parser) {
}
