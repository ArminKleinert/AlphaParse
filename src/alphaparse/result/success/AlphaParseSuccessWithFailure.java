package alphaparse.result.success;

import alphaparse.result.ParseFailureNode;
import org.jetbrains.annotations.NotNull;

final class AlphaParseSuccessWithFailure extends AlphaParseSuccess {
    private final @NotNull ParseFailureNode result;

    AlphaParseSuccessWithFailure(final int index, final @NotNull ParseFailureNode result) {
        super(index);
        this.result = result;
    }

    @Override
    public @NotNull ParseFailureNode getResult() {
        return result;
    }
}
